package com.rosten.app.dsj

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.util.SystemUtil
import com.rosten.app.util.Util
import com.rosten.app.system.Attachment
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.start.StartService
import com.rosten.app.system.Depart
import com.rosten.app.gtask.Gtask
import com.rosten.app.workflow.WorkFlowService
import com.rosten.app.system.Model

import org.activiti.engine.runtime.ProcessInstance
import org.activiti.engine.runtime.ProcessInstanceQuery
import org.activiti.engine.task.Task
import org.activiti.engine.task.TaskQuery

class DsjController {
	def springSecurityService
	def dsjService
	def startService
	def workFlowService
	def taskService
	
	def dsjGetNextTest ={
		def json=[:]
		def dsj = Dsj.get(params.id)
		
		if(dsj.taskId){
			def dsjDefEntity = workFlowService.getNextTaskDefinition(dsj.taskId);
			
			if(dsjDefEntity){
				def expEntity = dsjDefEntity.getAssigneeExpression()
				if(expEntity){
					json.username = expEntity.getExpressionText()
				}
				
				def groupEntity = dsjDefEntity.getCandidateGroupIdExpressions()
				if(groupEntity.size()>0){
					groupEntity.each{
						json.group = it.getExpressionText()
					}	
				}
			}
		}
		render json as JSON
	}
	def flowActiveExport ={
		def dsj = Dsj.get(params.id)
		InputStream imageStream = workFlowService.getflowActiveStream(dsj.processDefinitionId,dsj.taskId)
		
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
		
		def dsj = Dsj.get(params.id)
		def dsjDefEntity = workFlowService.getNextTaskDefinition(dsj.taskId);
		
		def expEntity = dsjDefEntity.getAssigneeExpression()
		if(expEntity){
			
			def expEntityText = expEntity.getExpressionText()
			if(expEntityText.contains("{")){
				params.user = dsj.drafter.username
			}else{
				params.user = expEntity.getExpressionText()
			}
			
			redirect controller: "system",action:'userTreeDataStore', params: params
			return
		}
		
		def groupEntity = dsjDefEntity.getCandidateGroupIdExpressions()
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
	def dsjGetContent ={
		def json=[:]
		def dsj = Dsj.get(params.id)
		json["subject"] = dsj.subject
		json["description"] = dsj.description
		render json as JSON
	}
	def dsjFlowDeal = {
		def json=[:]
		def dsj = Dsj.get(params.id)
		
		//处理当前人的待办事项
		def currentUser = springSecurityService.getCurrentUser()
		
		def frontStatus = dsj.status
		def nextStatus
		def nextUsers=[]
		
		//流程引擎相关信息处理-------------------------------------------------------------------------------------
		
		//结束当前任务，并开启下一节点任务
		taskService.complete(dsj.taskId)	//结束当前任务
		
		ProcessInstance processInstance = workFlowService.getProcessIntance(dsj.processInstanceId)
		if(!processInstance || processInstance.isEnded()){
			//流程已结束
			nextStatus = "已归档"
			dsj.currentUser = null
			dsj.currentDepart = null
			dsj.taskId = null
			
		}else{
			//获取下一节点任务，目前处理串行情况
			def tasks = workFlowService.getTasksByFlow(dsj.processInstanceId)
			def task = tasks[0]
			if(task.getDescription() && !"".equals(task.getDescription())){
				nextStatus = task.getDescription()
			}else{
				nextStatus = task.getName()
			}
			dsj.taskId = task.getId()
			
			if(params.dealUser){
				//下一步相关信息处理
				def dealUsers = params.dealUser.split(",")
				if(dealUsers.size() >1){
					//并发
				}else{
					//串行
					def nextUser = User.get(Util.strLeft(params.dealUser,":"))
					
					//任务指派给当前拟稿人
					taskService.claim(dsj.taskId, nextUser.username)
					
					def args = [:]
					args["type"] = "【大事记】"
					args["content"] = "请您审核名称为  【" + dsj.subject +  "】 的大事记"
					args["contentStatus"] = nextStatus
					args["contentId"] = dsj.id
					args["user"] = nextUser
					args["company"] = nextUser.company
					
					startService.addGtask(args)
					
					dsj.currentUser = nextUser
					dsj.currentDepart = Util.strRight(params.dealUser, ":")
					
					if(!dsj.readers.find{ item->
						item.id.equals(nextUser.id)
					}){
						dsj.addToReaders(nextUser)
					}
					nextUsers << nextUser.getFormattedName()
				}
				
			}
			
		}
		
		dsj.status = nextStatus
		dsj.currentDealDate = new Date()
		
		//判断下一处理人是否与当前处理人员为同一人
		if(currentUser.equals(dsj.currentUser)){
			json["refresh"] = true
		}
		
		//----------------------------------------------------------------------------------------------------
		
		def gtask = Gtask.findWhere(
			user:currentUser,
			company:currentUser.company,
			contentId:dsj.id,
			contentStatus:frontStatus
		)
		if(gtask!=null && "0".equals(gtask.status)){
			gtask.dealDate = new Date()
			gtask.status = "1"
			gtask.save()
		}
		
		if(dsj.save(flush:true)){
			//添加日志
			def dsjLog = new DsjLog()
			dsjLog.user = currentUser
			dsjLog.dsj = dsj
			
			switch (true){
				case dsj.status.contains("已签发"):
					dsjLog.content = "签发文件【" + nextUsers.join("、") + "】" 
					break
				case dsj.status.contains("归档"):
					dsjLog.content = "归档"
					break
				case dsj.status.contains("不同意"):
					dsjLog.content = "不同意！"
					break
				default:
					dsjLog.content = "提交" + dsj.status + "【" + nextUsers.join("、") + "】"
					break	
			}
			dsjLog.save(flush:true)
			
			json["result"] = true
		}else{
			dsj.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	
	def getFileUpload ={
		def model =[:]
		model["docEntity"] = "dsj"
		model["isShowFile"] = false
		if(params.id){
			//已经保存过
			def dsj = Dsj.get(params.id)
			model["docEntityId"] = params.id
			//获取附件信息
			model["attachFiles"] = Attachment.findAllByBeUseId(params.id)
			
			def user = springSecurityService.getCurrentUser()
			if("admin".equals(user.getUserType())){
				model["isShowFile"] = true
			}else if(user.equals(dsj.currentUser) && !"已归档".equals(dsj.status) ){
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
		
//		def uploadPath = sysUtil.getUploadPath("dsj")
		def uploadPath
		def currentUser = (User) springSecurityService.getCurrentUser()
		def companyPath = currentUser.company?.shortName
		if(companyPath == null){
			uploadPath = sysUtil.getUploadPath("dsj")
		}else{
			uploadPath = sysUtil.getUploadPath(currentUser.company.shortName + "/dsj")
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
		attachment.type = "dsj"
		attachment.url = uploadPath
		attachment.size = f.size
		attachment.beUseId = params.id
		attachment.upUser = (User) springSecurityService.getCurrentUser()
		
		//保存附件
		def dsj = Dsj.get(params.id)
		dsj.addToAttachments(attachment)
		dsj.save(flush:true)
//		attachment.save(flush:true)
		
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
		def dsj = Dsj.get(params.id)
		def user = User.get(params.userId)
		if(dsj){
			def comment = new DsjComment()
			comment.user = user
			comment.status = dsj.status
			comment.content = params.dataStr
			comment.dsj = dsj
			
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
		def dsj = Dsj.get(params.id)
		if(dsj){
			def logs = DsjComment.findAllByDsj(dsj,[ sort: "createDate", order: "desc"])
			model["log"] = logs
		}
		
		render(view:'/share/commentLog',model:model)
	}
	def getFlowLog={
		def model =[:]
		def dsj = Dsj.get(params.id)
		if(dsj){
			def logs = DsjLog.findAllByDsj(dsj,[ sort: "createDate", order: "asc"])
			model["log"] = logs
		}
		
		render(view:'/share/flowLog',model:model)
	}
	def dsjDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def dsj = Dsj.get(it)
				if(dsj){
					//删除相关的gtask待办事项
					Gtask.findAllByContentId(it).each{item->
						item.delete()
					}
					dsj.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def dsjSave = {
		def json=[:]
		
		//获取配置文档
		def dsjConfig = DsjConfig.first()
		if(!dsjConfig){
			json["result"] = "noConfig"
			render json as JSON
			return
		}
		
		def user = springSecurityService.getCurrentUser()
		
		def dsjStatus = "new"
		def dsj
		if(params.id && !"".equals(params.id)){
			dsj = Dsj.get(params.id)
			dsj.properties = params
			dsj.clearErrors()
			dsjStatus = "old"
		}else{
			dsj = new Dsj()
			dsj.properties = params
			dsj.clearErrors()
			
			dsj.company = Company.get(params.companyId)
			dsj.currentUser = user
			dsj.currentDepart = params.drafterDepart
			dsj.currentDealDate = new Date()
			
			dsj.drafter = user
			dsj.drafterDepart = params.drafterDepart
			
			dsj.serialNo = dsjConfig.nowYear + dsjConfig.nowSN.toString().padLeft(4,"0")
			
		}
		
		if(!dsj.readers.find{ it.id.equals(user.id) }){
			dsj.addToReaders(user)
		}
		
		//流程引擎相关信息处理-------------------------------------------------------------------------------------
		if(!dsj.processInstanceId){
			//启动流程实例
			def _model = Model.findByModelCode("dsj")
			def _processInstance = workFlowService.getProcessDefinition(_model.relationFlow)
			Map<String, Object> variables = new HashMap<String, Object>();
			ProcessInstance processInstance = workFlowService.addFlowInstance(_processInstance.key, user.username,dsj.id, variables);
			dsj.processInstanceId = processInstance.getProcessInstanceId()
			dsj.processDefinitionId = processInstance.getProcessDefinitionId()
			
			//获取下一节点任务
			def task = workFlowService.getTasksByFlow(processInstance.getProcessInstanceId())[0]
			dsj.taskId = task.getId()
			
			//任务指派给当前拟稿人
			//taskService.claim(task.getId(), user.username)
		}
		//-------------------------------------------------------------------------------------------------
		
		if(dsj.save(flush:true)){
			json["result"] = true
			json["id"] = dsj.id
			json["companyId"] = dsj.company.id
			
			if("new".equals(dsjStatus)){
				//添加日志
				def dsjLog = new DsjLog()
				dsjLog.user = user
				dsjLog.dsj = dsj
				dsjLog.content = "拟稿"
				dsjLog.save(flush:true)
				
				//修改配置文档中的流水号
				dsjConfig.nowSN += 1
				dsjConfig.save(flush:true)
			}
		}else{
			dsj.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	def dsjAdd ={
		redirect(action:"dsjShow",params:params)
	}
	def dsjShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def dsj = new Dsj()
		if(params.id){
			dsj = Dsj.get(params.id)
		}else{
			model.companyId = params.companyId
			
			dsj.drafter = user
			dsj.drafterDepart = user.getDepartName()
			dsj.currentUser = user
			dsj.currentDepart = user.getDepartName()
			
		}
		if(!dsj){
			render '<h2 style="color:red;width:500px;margin:0 auto">此大事记已过期或删除，请联系管理员！</h2>'
			return
		}
		model["user"]=user
		model["company"] = company
		model["dsj"] = dsj
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			fa.readOnly = []
		}
		model["fieldAcl"] = fa
		
		render(view:'/dsj/dsj',model:model)
	}

	def dsjGrid ={
		def json=[:]
		def user = User.get(params.userId)
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = dsjService.getDsjListLayout()
		}
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
				gridData = dsjService.getDsjListDataStoreByUser(args)
			}else if("all".equals(params.type)){
				//所有文档
				gridData = dsjService.getDsjListDataStore(args)
			}
			
			json["gridData"] = gridData
			
		}
		if(params.refreshPageControl){
			def total
			if("person".equals(params.type)){
				//个人待办
				total = dsjService.getDsjCountByUser(company,user)
			}else if("all".equals(params.type)){
				//所有文档
				total = dsjService.getDsjCount(company)
			}
			
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	def dsjConfig = {
		def model = [:]
		def user = springSecurityService.getCurrentUser()
		
		def dsjConfig = DsjConfig.findWhere(company:user.company)
		if(dsjConfig==null) {
			dsjConfig = new DsjConfig()
			
			Calendar cal = Calendar.getInstance();
			dsjConfig.nowYear = cal.get(Calendar.YEAR)
			dsjConfig.frontYear = dsjConfig.nowYear -1
			
			model.companyId = user.company.id
		}else{
			model.companyId = dsjConfig.company.id
		}
		model.dsjConfig = dsjConfig
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			fa.readOnly = ["nowYear","nowSN","nowCancel","frontYear","frontSN","frontCancel"]
		}else{
//			fa.readOnly = ["nowCancel","frontCancel"]
		}
		model["fieldAcl"] = fa
		
		render(view:'/dsj/dsjConfig',model:model)
	}
	def dsjConfigSave ={
		def json=[:]
		def dsjConfig = new DsjConfig()
		if(params.id && !"".equals(params.id)){
			dsjConfig = DsjConfig.get(params.id)
		}
		dsjConfig.properties = params
		dsjConfig.clearErrors()
		dsjConfig.company = Company.get(params.companyId)
		
		dsjConfig.nowCancel = params.dsjConfig_nowCancel
		dsjConfig.frontCancel = params.dsjConfig_frontCancel
		
		if(dsjConfig.save(flush:true)){
			json["result"] = true
			json["dsjConfigId"] = dsjConfig.id
			json["companyId"] = dsjConfig.company.id
		}else{
			dsjConfig.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
    def index() { }
}
