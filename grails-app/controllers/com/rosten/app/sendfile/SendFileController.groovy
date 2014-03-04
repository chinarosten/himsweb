package com.rosten.app.sendfile

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.util.SystemUtil
import com.rosten.app.util.Util
import com.rosten.app.system.Attachment
import com.rosten.app.system.Company
import com.rosten.app.system.User


class SendFileController {
	
	def springSecurityService
	def sendFileService
	
	def getFileUpload ={
		def model =[:]
		model["docEntity"] = "sendFile"
		model["isShowFile"] = false
		if(params.id){
			//已经保存过
			def sendFile = SendFile.get(params.id)
			model["sendFile"] = sendFile
			//获取附件信息
			model["attachFiles"] = Attachment.findAllByBeUseId(params.id)
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
			def logs = sendFileComment.findAllBySendFile(sendFile,[ sort: "createDate", order: "desc"])
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
			json["gridData"] = sendFileService.getSendFileListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = sendFileService.getSendFileCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	def index() {
	}
}
