package com.rosten.app.meeting

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.util.SystemUtil
import com.rosten.app.util.Util
import com.rosten.app.system.Attachment
import com.rosten.app.system.Company
import com.rosten.app.system.User

class MeetingController {
	def springSecurityService
	def meetingService
	
	def meetingGetContent ={
		def meeting = Meeting.get(params.id)
		render meeting.content
	}
	def getFileUpload ={
		def model =[:]
		model["docEntity"] = "meeting"
		model["isShowFile"] = false
		if(params.id){
			//已经保存过
			def meeting = Meeting.get(params.id)
			model["meeting"] = params.id
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
		
		def uploadPath = sysUtil.getUploadPath("meeting")
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
		render json as JSON
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
			def logs = meetingComment.findAllByMeeting(meeting,[ sort: "createDate", order: "desc"])
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
		redirect(action:"meetingShow",params:params)
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
		params.joinerId.split(",").each{
			meeting.addToGuesters(User.get(it))
		}
		params.guestersId.split(",").each{
			meeting.addToGuesters(User.get(it))
		}
		
		if(!meeting.readers.find{ it.id.equals(user.id) }){
			meeting.addToReaders(user)
		}
		
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
		model["user"]=user
		model["company"] = company
		model["meeting"] = meeting
		
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
			json["gridData"] = meetingService.getMeetingListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = meetingService.getMeetingCount(company)
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
