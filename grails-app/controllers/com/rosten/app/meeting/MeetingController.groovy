package com.rosten.app.meeting

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.util.SystemUtil
import com.rosten.app.util.Util
import com.rosten.app.system.Attachment
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.gtask.Gtask

import com.rosten.app.system.Model
import com.rosten.app.system.SystemService
import com.rosten.app.system.Authorize

import com.rosten.app.workflow.WorkFlowService
import org.activiti.engine.runtime.ProcessInstance
import org.activiti.engine.runtime.ProcessInstanceQuery
import org.activiti.engine.task.Task
import org.activiti.engine.task.TaskQuery

class MeetingController {
	def springSecurityService
	def meetingService
	def startService
	def workFlowService
	def taskService
	def systemService
	
	def flowActiveExport ={
		def meeting = Meeting.get(params.id)
		InputStream imageStream = workFlowService.getflowActiveStream(meeting.processDefinitionId,meeting.taskId)
		
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
		
		def meeting = Meeting.get(params.id)
		def meetingDefEntity = workFlowService.getNextTaskDefinition(meeting.taskId);
		
		def expEntity = meetingDefEntity.getAssigneeExpression()
		if(expEntity){
			def expEntityText = expEntity.getExpressionText()
			if(expEntityText.contains("{")){
				params.user = meeting.drafter.username
			}else{
				params.user = expEntity.getExpressionText()
			}
			
			redirect controller: "system",action:'userTreeDataStore', params: params
			return
		}
		
		def groupEntity = meetingDefEntity.getCandidateGroupIdExpressions()
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
	def meetingFlowDeal = {
		def json=[:]
		def meeting = Meeting.get(params.id)
		
		//处理当前人的待办事项
		def currentUser = springSecurityService.getCurrentUser()
		def frontStatus = meeting.status
		def nextStatus,nextDepart,nextLogContent
		def nextUsers=[]
		
		//流程引擎相关信息处理-------------------------------------------------------------------------------------
		
		//结束当前任务，并开启下一节点任务
		taskService.complete(meeting.taskId)	//结束当前任务
		
		ProcessInstance processInstance = workFlowService.getProcessIntance(meeting.processInstanceId)
		if(!processInstance || processInstance.isEnded()){
			//流程已结束
			nextStatus = "已归档"
			meeting.currentUser = null
			meeting.currentDepart = null
			meeting.taskId = null
		}else{
			//获取下一节点任务，目前处理串行情况
			def tasks = workFlowService.getTasksByFlow(meeting.processInstanceId)
			def task = tasks[0]
			if(task.getDescription() && !"".equals(task.getDescription())){
				nextStatus = task.getDescription()
			}else{
				nextStatus = task.getName()
			}
			meeting.taskId = task.getId()
		
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
					def _model = Model.findByModelCodeAndCompany("meeting",meeting.company)
					def authorize = systemService.checkIsAuthorizer(nextUser,_model,new Date())
					if(authorize){
						meetingService.addFlowLog(meeting,nextUser,"委托授权给【" + authorize.beAuthorizerDepart + ":" + authorize.getFormattedAuthorizer() + "】")
						
						nextUser = authorize.beAuthorizer
						nextDepart = authorize.beAuthorizerDepart
					}
					//-------------------------------------------------------------------------
					
					//任务指派给当前拟稿人
					taskService.claim(meeting.taskId, nextUser.username)
					
					def args = [:]
					args["type"] = "【会议通知】"
					args["content"] = "请您审核名称为  【" + meeting.subject +  "】 的会议通知"
					args["contentStatus"] = meeting.status
					args["contentId"] = meeting.id
					args["user"] = nextUser
					args["company"] = nextUser.company
					
					startService.addGtask(args)
					
					meeting.currentUser = nextUser
					meeting.currentDepart = nextDepart
					
					if(!meeting.readers.find{ item->
						item.id.equals(nextUser.id)
					}){
						meeting.addToReaders(nextUser)
					}
					nextUsers << nextUser.getFormattedName()
				}
			}
		}
		meeting.status = nextStatus
		meeting.currentDealDate = new Date()
		
		//判断下一处理人是否与当前处理人员为同一人
		if(currentUser.equals(meeting.currentUser)){
			json["refresh"] = true
		}
		
		//----------------------------------------------------------------------------------------------------
		
		//修改代办事项状态
		def gtask = Gtask.findWhere(
			user:currentUser,
			company:currentUser.company,
			contentId:meeting.id,
			contentStatus:frontStatus
		)
		if(gtask!=null && "0".equals(gtask.status)){
			gtask.dealDate = new Date()
			gtask.status = "1"
			gtask.save()
		}
		
		if(meeting.save(flush:true)){
			//添加日志
			def meetingLog = new MeetingLog()
			meetingLog.user = currentUser
			meetingLog.meeting = meeting
			
			switch (meeting.status){
				case "审核":
					meetingLog.content = "提交审核【" + nextUsers.join("、") + "】"
					break
				case "已签发":
					meetingLog.content = "签发文件【" + nextUsers.join("、") + "】" 
					
					//增加相关与会人员的待办工作任务
					def gtaskList = []
					gtaskList += meeting.guesters
					gtaskList += meeting.joiners
					gtaskList.unique().each{
						def args = [:]
						args["type"] = "【会议通知】"
						args["content"] = "请您参加名称为  【" + meeting.subject +  "】 的会议"
						args["contentStatus"] = "查看"
						args["contentId"] = meeting.id
						args["user"] = it
						args["company"] = it.company
						args["openDeal"] = true
						
						startService.addGtask(args)
					}
					
					break
				case "已归档":
					meetingLog.content = "归档发文"
					break
				case "不同意":
					meetingLog.content = "不同意签发！"
					break
			}
			meetingLog.save(flush:true)
			
			json["result"] = true
		}else{
			meeting.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	def meetingGetContent ={
		def json=[:]
		def meeting = Meeting.get(params.id)
		
		json["content"] = meeting.content
		json["description"] = meeting.description
		render json as JSON
	}
	def getFileUpload ={
		def model =[:]
		model["docEntity"] = "meeting"
		model["isShowFile"] = false
		if(params.id){
			//已经保存过
			def meeting = Meeting.get(params.id)
			model["docEntityId"] = params.id
			//获取附件信息
			model["attachFiles"] = Attachment.findAllByBeUseId(params.id)
			
			def user = springSecurityService.getCurrentUser()
			if("admin".equals(user.getUserType())){
				model["isShowFile"] = true
			}else if(user.equals(meeting.currentUser) && !"已归档".equals(meeting.status) ){
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
		
//		def uploadPath = sysUtil.getUploadPath("meeting")
		def uploadPath
		def currentUser = (User) springSecurityService.getCurrentUser()
		def companyPath = currentUser.company?.shortName
		if(companyPath == null){
			uploadPath = sysUtil.getUploadPath("meeting")
		}else{
			uploadPath = sysUtil.getUploadPath(currentUser.company.shortName + "/meeting")
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
		attachment.type = "meeting"
		attachment.url = uploadPath
		attachment.size = f.size
		attachment.beUseId = params.id
		attachment.upUser = (User) springSecurityService.getCurrentUser()
		attachment.save(flush:true)
		
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
		def meeting = Meeting.get(params.id)
		def user = User.get(params.userId)
		if(meeting){
			def comment = new MeetingComment()
			comment.user = user
			comment.status = meeting.status
			comment.content = params.dataStr
			comment.meeting = meeting
			
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
		def meeting = Meeting.get(params.id)
		if(meeting){
			def logs = MeetingComment.findAllByMeeting(meeting,[ sort: "createDate", order: "desc"])
			model["log"] = logs
		}
		
		render(view:'/share/commentLog',model:model)
	}
	def getFlowLog={
		def model =[:]
		def meeting = Meeting.get(params.id)
		if(meeting){
			def logs = MeetingLog.findAllByMeeting(meeting,[ sort: "createDate", order: "asc"])
			model["log"] = logs
			
			model["logEntityId"] = params.id
			model["logEntityName"] = "meeting"
		}
		
		render(view:'/share/flowLog',model:model)
	}
	def meetingDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def meeting = Meeting.get(it)
				if(meeting){
					//删除相关的gtask待办事项
					Gtask.findAllByContentId(it).each{item->
						item.delete()
					}
					meeting.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def meetingAdd ={
		//判断是否关联流程引擎
		def company = Company.get(params.companyId)
		def model = Model.findByModelCodeAndCompany("meeting",company)
		if(model.relationFlow && !"".equals(model.relationFlow)){
			redirect(action:"meetingShow",params:params)
		}else{
			//不存在流程引擎关联数据
			render '<h2 style="color:red;width:660px;margin:0 auto;margin-top:60px">当前模块不存在流程设置，无法创建，请联系管理员！</h2>'
		}
	}
	def meetingSave = {
		def json=[:]
		
		//获取配置文档
		def meetingConfig = MeetingConfig.first()
		if(!meetingConfig){
			json["result"] = "noConfig"
			render json as JSON
			return
		}
		
		def user = springSecurityService.getCurrentUser()
		
		def meetingStatus = "new"
		def meeting
		if(params.id && !"".equals(params.id)){
			meeting = Meeting.get(params.id)
			meeting.properties = params
			meeting.clearErrors()
			meetingStatus = "old"
		}else{
			meeting = new Meeting()
			meeting.properties = params
			meeting.clearErrors()
			
			meeting.company = Company.get(params.companyId)
			meeting.currentUser = user
			meeting.currentDepart = params.drafterDepart
			meeting.currentDealDate = new Date()
			
			meeting.drafter = user
			meeting.drafterDepart = params.drafterDepart
			
			meeting.serialNo = meetingConfig.nowYear + meetingConfig.nowSN.toString().padLeft(4,"0")
		}
		meeting.presider = User.get(params.presiderId)
		
		if(params.joinerIds){
			meeting.joiners?.clear()
			params.joinerIds.split(",").each{
				meeting.addToJoiners(User.get(it))
			}
		}
		
		if(params.guesterIds){
			meeting.guesters?.clear()
			params.guesterIds.split(",").each{
				meeting.addToGuesters(User.get(it))
			}
		}
		if(!meeting.readers.find{ it.id.equals(user.id) }){
			meeting.addToReaders(user)
		}
		
		//流程引擎相关信息处理-------------------------------------------------------------------------------------
		if(!meeting.processInstanceId){
			//启动流程实例
			def _model = Model.findByModelCodeAndCompany("meeting",meeting.company)
			def _processInstance = workFlowService.getProcessDefinition(_model.relationFlow)
			Map<String, Object> variables = new HashMap<String, Object>();
			ProcessInstance processInstance = workFlowService.addFlowInstance(_processInstance.key, user.username,meeting.id, variables);
			meeting.processInstanceId = processInstance.getProcessInstanceId()
			meeting.processDefinitionId = processInstance.getProcessDefinitionId()
			
			//获取下一节点任务
			def task = workFlowService.getTasksByFlow(processInstance.getProcessInstanceId())[0]
			meeting.taskId = task.getId()
			
			//任务指派给当前拟稿人
			//taskService.claim(task.getId(), user.username)
		}
		//-------------------------------------------------------------------------------------------------
		
		if(meeting.save(flush:true)){
			json["result"] = true
			json["id"] = meeting.id
			json["companyId"] = meeting.company.id
			
			if("new".equals(meetingStatus)){
				//添加日志
				def meetingLog = new MeetingLog()
				meetingLog.user = user
				meetingLog.meeting = meeting
				meetingLog.content = "拟稿"
				meetingLog.save(flush:true)
				
				//修改配置文档中的流水号
				meetingConfig.nowSN += 1
				meetingConfig.save(flush:true)
			}
		}else{
			meeting.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	def meetingShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def meeting = new Meeting()
		if(params.id){
			meeting = Meeting.get(params.id)
		}else{
			model.companyId = params.companyId
			
			meeting.drafter = user
			meeting.drafterDepart = user.getDepartName()
			meeting.currentUser = user
			meeting.currentDepart = user.getDepartName()
			
		}
		
		if(!meeting){
			render '<h2 style="color:red;width:500px;margin:0 auto">此会议通知已过期或删除，请联系管理员！</h2>'
			return
		}
		
		model["user"]=user
		model["company"] = company
		model["meeting"] = meeting
		
		//参与人员
		def joiners =[]
		def joinerIds =[]
		meeting.joiners.each { elem ->
			joiners << elem.getFormattedName()
			joinerIds << elem.id
		}
		model["joiners"] = joiners
		model["joinerIds"] = joinerIds
		
		//列些人员
		def guesters =[]
		def guesterIds =[]
		meeting.guesters.each { elem ->
			guesters << elem.getFormattedName()
			guesterIds << elem.id
		}
		model["guesters"] = guesters
		model["guesterIds"] = guesterIds
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			fa.readOnly = []
		}
		model["fieldAcl"] = fa
		
		render(view:'/meeting/meeting',model:model)
	}

	def meetingGrid ={
		def json=[:]
		def user = User.get(params.userId)
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = meetingService.getMeetingListLayout()
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
				gridData = meetingService.getMeetingListDataStoreByUser(args)
			}else if("all".equals(params.type)){
				//所有文档
				gridData = meetingService.getMeetingListDataStore(args)
			}
			
			json["gridData"] = gridData
			
		}
		if(params.refreshPageControl){
			def total
			if("person".equals(params.type)){
				//个人待办
				total = meetingService.getMeetingCountByUser(company,user)
			}else if("all".equals(params.type)){
				//所有文档
				total = meetingService.getMeetingCount(company)
			}
			
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	def meetingConfig = {
		def model = [:]
		def user = springSecurityService.getCurrentUser()
		
		def meetingConfig = MeetingConfig.findWhere(company:user.company)
		if(meetingConfig==null) {
			meetingConfig = new MeetingConfig()
			
			Calendar cal = Calendar.getInstance();
			meetingConfig.nowYear = cal.get(Calendar.YEAR)
			meetingConfig.frontYear = meetingConfig.nowYear -1
			
			model.companyId = user.company.id
		}else{
			model.companyId = meetingConfig.company.id
		}
		model.meetingConfig = meetingConfig
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			fa.readOnly = ["nowYear","nowSN","nowCancel","frontYear","frontSN","frontCancel"]
		}else{
//			fa.readOnly = ["nowCancel","frontCancel"]
		}
		model["fieldAcl"] = fa
		
		render(view:'/meeting/meetingConfig',model:model)
	}
	def meetingConfigSave ={
		def json=[:]
		def meetingConfig = new MeetingConfig()
		if(params.id && !"".equals(params.id)){
			meetingConfig = MeetingConfig.get(params.id)
		}
		meetingConfig.properties = params
		meetingConfig.clearErrors()
		meetingConfig.company = Company.get(params.companyId)
		
		meetingConfig.nowCancel = params.meetingConfig_nowCancel
		meetingConfig.frontCancel = params.meetingConfig_frontCancel
		
		if(meetingConfig.save(flush:true)){
			json["result"] = true
			json["meetingConfigId"] = meetingConfig.id
			json["companyId"] = meetingConfig.company.id
		}else{
			meetingConfig.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
    def index() { }
}
