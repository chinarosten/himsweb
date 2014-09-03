package com.rosten.app.sendfile

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.util.SystemUtil
import com.rosten.app.util.Util
import com.rosten.app.system.Attachment
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.system.Depart
import com.rosten.app.start.StartService
import com.rosten.app.gtask.Gtask

import com.rosten.app.system.Model
import com.rosten.app.system.SystemService
import com.rosten.app.system.Authorize

import com.rosten.app.workflow.WorkFlowService
import org.activiti.engine.runtime.ProcessInstance
import org.activiti.engine.runtime.ProcessInstanceQuery
import org.activiti.engine.task.Task
import org.activiti.engine.task.TaskQuery


class SendFileController {
	
	def springSecurityService
	def sendFileService
	def startService
	def workFlowService
	def taskService
	def systemService
	
	def searchView ={
		def model =[:]
		render(view:'/sendFile/sendFileSearch',model:model)
	}
	
	def flowActiveExport ={
		def sendFile = SendFile.get(params.id)
		InputStream imageStream = workFlowService.getflowActiveStream(sendFile.processDefinitionId,sendFile.taskId)
		
		byte[] b = new byte[1024];
		int len = -1;
		while ((len = imageStream.read(b, 0, 1024)) != -1) {
		  response.outputStream.write(b, 0, len);
		}
		response.outputStream.flush()
		response.outputStream.close()
		
	}
	def getDealWithUser ={
		def currentUser = springSecurityService.getCurrentUser()
		
		def sendFile = SendFile.get(params.id)
		def sendFileDefEntity = workFlowService.getNextTaskDefinition(sendFile.taskId);
		
		def expEntity = sendFileDefEntity.getAssigneeExpression()
		if(expEntity){
			def expEntityText = expEntity.getExpressionText()
			if(expEntityText.contains("{")){
				params.user = sendFile.drafter.username
			}else{
				params.user = expEntity.getExpressionText()
			}
			
			redirect controller: "system",action:'userTreeDataStore', params: params
			return
		}
		
		def groupEntity = sendFileDefEntity.getCandidateGroupIdExpressions()
		if(groupEntity.size()>0){
			//默认有一组group的方式为true，则整组均为true;true:严格控制本部门权限
			def groupIds = []
			def limit = false
			groupEntity.each{
				groupIds << Util.strLeft(it.getExpressionText(), ":")
				if(!limit && "true".equals(Util.strRight(it.getExpressionText(), ":"))){
					limit = true
				}
			}
			params.groupIds = groupIds.unique().join("-")
			if(limit){
				params.limitDepart = currentUser.getDepartEntityTrueName()
			}
			
			redirect controller: "system",action:'userTreeDataStore', params: params
			return
		}
		
	}
	def sendFileFlowDeal = {
		def json=[:]
		def sendFile = SendFile.get(params.id)
		def currentUser = springSecurityService.getCurrentUser()
		
		//分发情况特殊处理
		if ("send".equals(params.deal)){
			//分发
			if(!sendFile.isSend){
				sendFile.isSend = true
			}
			//发送相关人员并记录日志
			def _nextUsers=[]
			params.dealUser.split(",").each{
				def _nextUser = User.get(Util.strLeft(it,":"))
				if(!sendFile.readers.contains(_nextUser)){
					sendFile.addToReaders(_nextUser)
				}
				def _name
				if(_nextUser.chinaName!=null){
					_name = _nextUser.chinaName
				}else{
					_name = _nextUser.username
				}
				_nextUsers << _name
				
				//创建待办文件
				def args = [:]
				args["type"] = "【发文】"
				args["content"] = "请您查看名称为  【" + sendFile.title +  "】 的发文"
				args["contentStatus"] = sendFile.status
				args["contentId"] = sendFile.id
				args["user"] = _nextUser
				args["company"] = _nextUser.company
				
				startService.addGtask(args)
			}
			//添加日志
			def sendFileLog = new SendFileLog()
			sendFileLog.user = currentUser
			sendFileLog.sendFile = sendFile
			sendFileLog.content = "分发文件【" + _nextUsers.join("、") + "】"
			sendFileLog.save()
			
			if(sendFile.save(flush:true)){
				json["result"] = true
			}else{
				json["result"] = false
			}
			
			render json as JSON
			
			return;
		}
		
		//其他情况正常处理-----
		def sendFileLabel = SendLable.get(params.fileTypeId)
		def frontStatus = sendFile.status
		def nextStatus,nextDepart,nextLogContent
		def nextUsers=[]
		
		//流程引擎相关信息处理-------------------------------------------------------------------------------------
		
		//结束当前任务，并开启下一节点任务
		taskService.complete(sendFile.taskId)	//结束当前任务
		ProcessInstance processInstance = workFlowService.getProcessIntance(sendFile.processInstanceId)
		if(!processInstance || processInstance.isEnded()){
			//流程已结束
			nextStatus = "已归档"
			sendFile.currentUser = null
			sendFile.currentDepart = null
			sendFile.taskId = null
		}else{
			//获取下一节点任务，目前处理串行情况
			def tasks = workFlowService.getTasksByFlow(sendFile.processInstanceId)
			def task = tasks[0]
			if(task.getDescription() && !"".equals(task.getDescription())){
				nextStatus = task.getDescription()
			}else{
				nextStatus = task.getName()
			}
			sendFile.taskId = task.getId()
			
			if(params.dealUser){
				//下一步相关信息处理
				def dealUsers = params.dealUser.split(",")
				if(dealUsers.size() >1){
					//并发
				}else{
					//串行
					def nextUser = User.get(Util.strLeft(params.dealUser,":"))
					nextDepart = Util.strRight(params.dealUser, ":")
					
					//判断是否有公务授权------------------------------------------------------------
					def _model = Model.findByModelCodeAndCompany("sendfile",sendFile.company)
					def authorize = systemService.checkIsAuthorizer(nextUser,_model,new Date())
					if(authorize){
						sendFileService.addFlowLog(sendFile,nextUser,"委托授权给【" + authorize.beAuthorizerDepart + ":" + authorize.getFormattedAuthorizer() + "】")
						
						nextUser = authorize.beAuthorizer
						nextDepart = authorize.beAuthorizerDepart
					}
					//-------------------------------------------------------------------------
					//任务指派给当前拟稿人
					taskService.claim(sendFile.taskId, nextUser.username)
					
					//创建待办文件
					def args = [:]
					args["type"] = "【发文】"
					args["content"] = "请您审核名称为  【" + sendFile.title +  "】 的发文"
					args["contentStatus"] = sendFile.status
					args["contentId"] = sendFile.id
					args["user"] = nextUser
					args["company"] = nextUser.company
					
					startService.addGtask(args)
					
					sendFile.currentUser = nextUser
					sendFile.currentDepart = nextDepart
					
					if(!sendFile.readers.find{ item->
						item.id.equals(nextUser.id)
					}){
						sendFile.addToReaders(nextUser)
					}
					nextUsers << nextUser.getFormattedName()
					
		
				}
			}
		}
		sendFile.status = nextStatus
		sendFile.currentDealDate = new Date()
		
		//判断下一处理人是否与当前处理人员为同一人
		if(currentUser.equals(sendFile.currentUser)){
			json["refresh"] = true
		}
		
		//----------------------------------------------------------------------------------------------------
		
		//修改当前处理人的待办事项
		def gtask = Gtask.findWhere(
			user:currentUser,
			company:currentUser.company,
			contentId:sendFile.id,
			contentStatus:frontStatus
		)
		if(gtask!=null && "0".equals(gtask.status)){
			gtask.dealDate = new Date()
			gtask.status = "1"
			gtask.save()
		}
		
		//当前文档特殊字段处理
		if(nextStatus.equals("已签发")){
			sendFile.fileNo = sendFileLabel.nowYear + "【" + sendFileLabel.subCategory + "】" + sendFileLabel.nowSN.toString().padLeft(4,"0")
			sendFile.fileDate = new Date()
		}
				
		if(sendFile.save(flush:true)){
			//添加日志
			def sendFileLog = new SendFileLog()
			sendFileLog.user = currentUser
			sendFileLog.sendFile = sendFile
			
			switch (sendFile.status){
				case "审核":
					sendFileLog.content = "提交审核【" + nextUsers.join("、") + "】"
					break
				case "已签发":
					sendFileLog.content = "签发文件【" + nextUsers.join("、") + "】,文件编号为:" + sendFile.fileNo
					
					//修改配置文档中的流水号
					sendFileLabel.nowSN += 1
					sendFileLabel.save(flush:true)
					
					break
				case "已归档":
					sendFileLog.content = "归档发文"
					break
				case "不同意":
					sendFileLog.content = "不同意签发！"
					break
			}
			sendFileLog.save(flush:true)
			
			json["result"] = true
		}else{
			sendFile.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	def getFileUpload ={
		def model =[:]
		model["docEntity"] = "sendFile"
		model["isShowFile"] = false
		if(params.id){
			//已经保存过
			def sendFile = SendFile.get(params.id)
			model["docEntityId"] = params.id
			//获取附件信息
			model["attachFiles"] = Attachment.findAllByBeUseId(params.id)
			
			def user = springSecurityService.getCurrentUser()
			if("admin".equals(user.getUserType())){
				model["isShowFile"] = true
			}else if(user.equals(sendFile.currentUser) && !"已归档".equals(sendFile.status) ){
				model["isShowFile"] = true
			}
		}else{
			//尚未保存
			model["newDoc"] = true
		}
		render(view:'/share/fileUpload',model:model)
	}
	
	def uploadFile = {
		def json=[:]
		SystemUtil sysUtil = new SystemUtil()
		
//		def uploadPath = sysUtil.getUploadPath("sendfile")
		def uploadPath
		def currentUser = (User) springSecurityService.getCurrentUser()
		def companyPath = currentUser.company?.shortName
		if(companyPath == null){
			uploadPath = sysUtil.getUploadPath("sendfile")
		}else{
			uploadPath = sysUtil.getUploadPath(currentUser.company.shortName + "/sendfile")
		}
		
		def f = request.getFile("uploadedfile")
		if (f.empty) {
			json["result"] = "blank"
			render json as JSON
			return
		}
		
		def uploadSize = sysUtil.getUploadSize()
		if(uploadSize!=null){
			//控制附件上传大小
			def maxSize = uploadSize * 1024 * 1024
			if(f.size>=maxSize){
				json["result"] = "big"
				render json as JSON
				return
			}
		}
		String name = f.getOriginalFilename()//获得文件原始的名称
		def realName = sysUtil.getRandName(name)
		f.transferTo(new File(uploadPath,realName))
		
		def attachment = new Attachment()
		attachment.name = name
		attachment.realName = realName
		attachment.type = "sendfile"
		attachment.url = uploadPath
		attachment.size = f.size
		attachment.beUseId = params.id
		attachment.upUser = (User) springSecurityService.getCurrentUser()
		
		def sendFile = SendFile.get(params.id)
		sendFile.addToAttachments(attachment)
		sendFile.save(flush:true)
		
		json["result"] = "true"
		json["fileId"] = attachment.id
		json["fileName"] = name
		
		if("yes".equals(params.isIE)){
			def resultStr  = '{"result":"true", "fileId":"' + json["fileId"]  + '","fileName":"' + json["fileName"] +'"}'
			render "<textarea>" + resultStr +  "</textarea>"
			return
		}else{
			render json as JSON
		}
	}
	
	def addComment ={
		def json=[:]
		def sendFile = SendFile.get(params.id)
		def user = User.get(params.userId)
		if(sendFile){
			def comment = new SendFileComment()
			comment.user = user
			comment.status = sendFile.status
			comment.content = params.dataStr
			comment.sendFile = sendFile
			
			if(comment.save(flush:true)){
				json["result"] = true
			}else{
				comment.errors.each{
					println it
				}
				json["result"] = false
			}
			
		}else{
			json["result"] = false
		}
		
		render json as JSON
	}
	
	def getCommentLog ={
		def model =[:]
		def sendFile = SendFile.get(params.id)
		if(sendFile){
			def logs = SendFileComment.findAllBySendFile(sendFile,[ sort: "createDate", order: "desc"])
			model["log"] = logs
		}
		
		render(view:'/share/commentLog',model:model)
	}
	def getFlowLog={
		def model =[:]
		def sendFile = SendFile.get(params.id)
		if(sendFile){
			def logs = SendFileLog.findAllBySendFile(sendFile,[ sort: "createDate", order: "asc"])
			model["log"] = logs
			
			model["logEntityId"] = params.id
			model["logEntityName"] = "sendFile"
		}
		
		render(view:'/share/flowLog',model:model)
	}
	
	def getAllSendFileLabel ={
		def json = [identifier:'id',label:'name',items:[]]
		def company = Company.get(params.companyId)
		def sendFileLabelList = SendLable.findAllByCompany(company)
		sendFileLabelList.each{
			def sMap = ["id":it.id,"category":it.category,"org2Name":it.org2Name,"subCategory":it.subCategory]
			json.items+=sMap
		}
		render json as JSON
	}
	def sendFileLabelDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def sendFileLabel = SendLable.get(it)
				if(sendFileLabel){
					sendFileLabel.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def sendFileLabelSave ={
		def json=[:]
		def sendFileLabel = new SendLable()
		if(params.id && !"".equals(params.id)){
			sendFileLabel = SendLable.get(params.id)
		}else{
			sendFileLabel.company = Company.get(params.companyId)
		}
		sendFileLabel.properties = params
		sendFileLabel.clearErrors()
		
		if(sendFileLabel.save(flush:true)){
			json["result"] = true
			json["sendFileId"] = sendFileLabel.id
			json["companyId"] = sendFileLabel.company.id
		}else{
			sendFileLabel.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	def sendFileLabelAdd ={
		redirect(action:"sendFileLabelShow",params:params)
	}
	def sendFileLabelShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def sendFileLabel = new SendLable()
		if(params.id){
			sendFileLabel = SendLable.get(params.id)
		}else{
			Calendar cal = Calendar.getInstance();
			sendFileLabel.nowYear = cal.get(Calendar.YEAR)
			sendFileLabel.frontYear = sendFileLabel.nowYear -1
			
			model.companyId = user.company.id
		}
		model["user"]=user
		model["company"] = company
		model["sendFileLabel"] = sendFileLabel
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			fa.readOnly = ["category","subCategory","nowYear","nowSN","nowCancel","frontYear","frontSN","frontCancel"]
		}
		model["fieldAcl"] = fa
		
		render(view:'/sendFile/sendFileLabel',model:model)
	}
	def sendFileLabelGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = sendFileService.getSendFileLabelListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = sendFileService.getSendFileLabelListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = sendFileService.getSendFileLabelCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def addWord = {
		def model =[:]
		render(view:'/sendFile/word',model:model)
	}
	def sendFileDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def sendFile = SendFile.get(it)
				if(sendFile){
					//删除相关的gtask待办事项
					Gtask.findAllByContentId(it).each{item->
						item.delete()
					}
					sendFile.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def sendFileSave = {
		def json=[:]
		
		//获取配置文档
		def sendFileConfig = SendFileConfig.first()
		if(!sendFileConfig){
			json["result"] = "noConfig"
			render json as JSON
			return
		}
		
		//获取发文代字
		def sendFileLabel = SendLable.get(params.fileTypeId)
		if(!sendFileLabel){
			json["result"] = "noSendFileLabel"
			render json as JSON
			return
		}
		
		def user = springSecurityService.getCurrentUser()
		
		def sendFileStatus = "new"
		def sendFile
		if(params.id && !"".equals(params.id)){
			sendFile = SendFile.get(params.id)
			sendFile.properties = params
			sendFile.clearErrors()
			sendFileStatus = "old"
		}else{
			sendFile = new SendFile()
			sendFile.properties = params
			sendFile.clearErrors()
			
			sendFile.fileType = sendFileLabel
			
			sendFile.company = Company.get(params.companyId)
			sendFile.currentUser = user
			sendFile.currentDepart = params.dealDepart
			sendFile.currentDealDate = new Date()
			
			sendFile.drafter = user
			sendFile.drafterDepart = params.dealDepart
			
			sendFile.serialNo = sendFileConfig.nowYear + sendFileConfig.nowSN.toString().padLeft(4,"0")
			
		}
		
		if(!sendFile.readers.find{ it.id.equals(user.id) }){
			sendFile.addToReaders(user)
		}
		
		//流程引擎相关信息处理-------------------------------------------------------------------------------------
		if(!sendFile.processInstanceId){
			//启动流程实例
			def _model = Model.findByModelCodeAndCompany("sendfile",sendFile.company)
			def _processInstance = workFlowService.getProcessDefinition(_model.relationFlow)
			Map<String, Object> variables = new HashMap<String, Object>();
			ProcessInstance processInstance = workFlowService.addFlowInstance(_processInstance.key, user.username,sendFile.id, variables);
			sendFile.processInstanceId = processInstance.getProcessInstanceId()
			sendFile.processDefinitionId = processInstance.getProcessDefinitionId()
			
			//获取下一节点任务
			def task = workFlowService.getTasksByFlow(processInstance.getProcessInstanceId())[0]
			sendFile.taskId = task.getId()
			
			//任务指派给当前拟稿人
			//taskService.claim(task.getId(), user.username)
		}
		//-------------------------------------------------------------------------------------------------
		
		if(sendFile.save(flush:true)){
			json["result"] = true
			json["id"] = sendFile.id
			json["companyId"] = sendFile.company.id
			
			if("new".equals(sendFileStatus)){
				//添加日志
				def sendFileLog = new SendFileLog()
				sendFileLog.user = user
				sendFileLog.sendFile = sendFile
				sendFileLog.content = "拟稿发文,流水号为:" + sendFile.serialNo
				sendFileLog.save(flush:true)
				
				//修改配置文档中的流水号
				sendFileConfig.nowSN += 1
				sendFileConfig.save(flush:true)
			}
		}else{
			sendFile.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	def sendFileAdd ={
		//判断是否关联流程引擎
		def company = Company.get(params.companyId)
		def model = Model.findByModelCodeAndCompany("sendfile",company)
		if(model.relationFlow && !"".equals(model.relationFlow)){
			redirect(action:"sendFileShow",params:params)
		}else{
			//不存在流程引擎关联数据
			render '<h2 style="color:red;width:660px;margin:0 auto;margin-top:60px">当前模块不存在流程设置，无法创建，请联系管理员！</h2>'
		}
	}
	def sendFileShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def sendFile = new SendFile()
		if(params.id){
			sendFile = SendFile.get(params.id)
		}else{
			model.companyId = params.companyId
			sendFile.drafter = user
			sendFile.drafterDepart = user.getDepartName()
			sendFile.currentUser = user
			sendFile.currentDepart = user.getDepartName()
		}
		
		if(!sendFile){
			render '<h2 style="color:red;width:500px;margin:0 auto">此发文已过期或删除，请联系管理员！</h2>'
			return
		}
		
		model["user"]=user
		model["company"] = company
		model["sendFile"] = sendFile
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			fa.readOnly = []
		}
		model["fieldAcl"] = fa
		
		render(view:'/sendFile/sendFile',model:model)
	}

	def sendFileGrid ={
		def json=[:]
		def user = User.get(params.userId)
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = sendFileService.getSendFileListLayout()
		}
		
		//2014-9-3 增加搜索功能
		def searchArgs =[:]
		
		if(params.fileNo && !"".equals(params.fileNo)) searchArgs["fileNo"] = params.fileNo
		if(params.title && !"".equals(params.title)) searchArgs["title"] = params.title
		if(params.status && !"".equals(params.status)) searchArgs["status"] = params.status
		
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			
			def gridData
			if("person".equals(params.type)){
				//个人待办
				args["user"] = user
				gridData = sendFileService.getSendFileListDataStoreByUser(args,searchArgs)
			}else if("all".equals(params.type)){
				//所有文档
				gridData = sendFileService.getSendFileListDataStore(args,searchArgs)
			}
			
			json["gridData"] = gridData
			
		}
		if(params.refreshPageControl){
			def total
			if("person".equals(params.type)){
				//个人待办
				total = sendFileService.getSendFileCountByUser(company,user,searchArgs)
			}else if("all".equals(params.type)){
				//所有文档
				total = sendFileService.getSendFileCount(company,searchArgs)
			}
			
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	def sendFileConfigView = {
		def model = [:]
		def user = springSecurityService.getCurrentUser()
		
		def sendFileConfig = SendFileConfig.findWhere(company:user.company)
		if(sendFileConfig==null) {
			sendFileConfig = new SendFileConfig()
			
			Calendar cal = Calendar.getInstance();
			sendFileConfig.nowYear = cal.get(Calendar.YEAR)
			sendFileConfig.frontYear = sendFileConfig.nowYear -1
			
			model.companyId = user.company.id
		}else{
			model.companyId = sendFileConfig.company.id
		}
		model.sendFileConfig = sendFileConfig
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			fa.readOnly = ["nowYear","nowSN","nowCancel","frontYear","frontSN","frontCancel"]
		}
		model["fieldAcl"] = fa
		
		render(view:'/sendFile/sendFileConfig',model:model)
	}
	def sendFileConfigSave ={
		def json=[:]
		def sendFileConfig = new SendFileConfig()
		if(params.id && !"".equals(params.id)){
			sendFileConfig = SendFileConfig.get(params.id)
		}
		sendFileConfig.properties = params
		sendFileConfig.clearErrors()
		sendFileConfig.company = Company.get(params.companyId)
		
		if(sendFileConfig.save(flush:true)){
			json["result"] = true
			json["sendFileConfigId"] = sendFileConfig.id
			json["companyId"] = sendFileConfig.company.id
		}else{
			sendFileConfig.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	def index() {
	}
}
