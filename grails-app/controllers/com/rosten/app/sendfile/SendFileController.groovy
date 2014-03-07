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


class SendFileController {
	
	def springSecurityService
	def sendFileService
	
	def sendFileFlowDeal = {
		def json=[:]
		
		//获取配置文档
		def sendFileLabel = SendLable.get(params.fileTypeId)
		if(!sendFileLabel){
			json["result"] = "noSendFileLabel"
			render json as JSON
			return
		}
		
		def sendFile = SendFile.get(params.id)
		
		//处理当前人的待办事项
		def currentUser = springSecurityService.getCurrentUser()
		def frontStatus = sendFile.status
		
		switch (params.deal){
			case "submit":
				sendFile.status="审核"
				break;
			case "agrain":
				sendFile.status = "已签发"
				sendFile.fileNo = sendFileLabel.nowYear + sendFileLabel.nowSN.toString().padLeft(4,"0")
				
				break;
			case "notAgrain":
				sendFile.status = "不同意"
				break;
		}
		
		def nextUser=""
		if(params.dealUser){
			//下一步相关信息处理
			def dealUsers = params.dealUser.split(",")
			if(dealUsers.size() >1){
				//并发
			}else{
				//串行
				def _user = User.get(Util.strLeft(params.dealUser,":"))
				def args = [:]
				args["type"] = "【发文】"
				args["content"] = "请您审核名称为  【" + sendFile.title +  "】 的发文"
				args["contentStatus"] = sendFile.status
				args["contentId"] = sendFile.id
				args["user"] = _user
				args["company"] = _user.company
				
				startService.addGtask(args)
				
				sendFile.currentUser = _user
				def departEntity = Depart.get(Util.strRight(params.dealUser, ":"))
				sendFile.currentDepart = departEntity.departName
				sendFile.currentDealDate = new Date()
				
				if(!sendFile.readers.find{ item->
					item.id.equals(_user.id)
				}){
					sendFile.addToReaders(_user)
				}
				nextUser = _user.username
			}
			
		}
		
		def gtask = Gtask.findWhere(
			user:currentUser,
			company:currentUser.company,
			contentId:sendFile.id,
			contentStatus:frontStatus
		)
		if(gtask!=null){
			gtask.dealDate = new Date()
			gtask.status = "1"
			gtask.save()
		}
		
		if(sendFile.save(flush:true)){
			//添加日志
			def sendFileLog = new SendFileLog()
			sendFileLog.user = currentUser
			sendFileLog.sendFile = sendFile
			
			switch (sendFile.status){
				case "审核":
					sendFileLog.content = "提交【" + nextUser + "】" + sendFile.status
					break
				case "已签发":
					sendFileLog.content = sendFile.status
					
					//修改配置文档中的流水号
					sendFileLabel.nowSN += 1
					sendFileLabel.save(flush:true)
					
					break
				case "不同意":
					sendFileLog.content = sendFile.status
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
			model["docEntityId"] = sendFile
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
		
		def uploadPath = sysUtil.getUploadPath("sendfile")
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
		attachment.save(flush:true)
		
		json["result"] = "true"
		json["fileId"] = attachment.id
		json["fileName"] = name
		render json as JSON
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
		
		if(sendFile.save(flush:true)){
			json["result"] = true
			json["id"] = sendFile.id
			json["companyId"] = sendFile.company.id
			
			if("new".equals(sendFileStatus)){
				//添加日志
				def sendFileLog = new SendFileLog()
				sendFileLog.user = user
				sendFileLog.sendFile = sendFile
				sendFileLog.content = "拟稿"
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
		redirect(action:"sendFileShow",params:params)
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
				gridData = sendFileService.getSendFileListDataStoreByUser(args)
			}else if("all".equals(params.type)){
				//所有文档
				gridData = sendFileService.getSendFileListDataStore(args)
			}
			
			json["gridData"] = gridData
			
		}
		if(params.refreshPageControl){
			def total
			if("person".equals(params.type)){
				//个人待办
				total = sendFileService.getSendFileCountByUser(company,user)
			}else if("all".equals(params.type)){
				//所有文档
				total = sendFileService.getSendFileCount(company)
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
