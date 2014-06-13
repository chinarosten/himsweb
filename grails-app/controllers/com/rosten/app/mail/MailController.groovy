package com.rosten.app.mail

import grails.converters.JSON
import java.text.SimpleDateFormat

import com.rosten.app.util.GB2Alpha;
import com.rosten.app.util.GridUtil
import com.rosten.app.util.Util
import com.rosten.app.system.User
import com.rosten.app.system.Depart
import com.rosten.app.util.GB2Alpha
import com.rosten.app.util.SystemUtil
import com.rosten.app.system.Attachment
import com.rosten.app.mail.EmailBox

class MailController {
	def springSecurityService
	
	def receiveMail = {
		def json=[:]
		def user = User.get(params.userId)
		if(user.isOnMail && user.company.isOnMail){
			ReceiveMail rmail=new ReceiveMail(user.emailConfig.loginName, user.emailConfig.loginPassword, user.emailConfig.popName, user.emailConfig.popPort)
			try {
				def messages = rmail.getMessage();
				messages.each{
					//创建收件信息
					def receiveMail = new EmailBox()
					receiveMail.sender = rmail.getSender(it)
					receiveMail.senderCode = rmail.getSender(it)
					receiveMail.receiver = user.username
					receiveMail.receiverCode = rmail.getAllRecipients(it)
					receiveMail.subject = it.getSubject()
					receiveMail.content = rmail.getContent(it)
					receiveMail.sendDate = rmail.getReceivedDate(it)
					receiveMail.boxType = 1
					receiveMail.mailUser = user
					receiveMail.save()
					
					//增加附件的处理
				}
				json["result"] = true
			}catch (Exception e) {
				e.printStackTrace();
				json["result"] = false
			}
		}else{
			json["result"] = "noTurnOn"
		}
		
		render json as JSON
	}
	def getMailBody ={
		def mail = EmailBox.get(params.id)
		render mail.content
	}
	def publishMail = {
		def user = User.get(params.userId)
		def max = 4
		def offset = 0
		
		def c = EmailBox.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			mailUser{
				eq("id",user.id)
			}
//			eq("receiver",user.username)
			eq("boxType",1)
			eq("emailStatus",0)
			order("createdDate", "desc")
		}
		def bbsList = []
		c.list(pa,query).unique().each{
			def smap =[:]
			smap["subject"] = it.subject
			smap["id"] = it.id
			smap["date"] = it.sent
			
			if(it.sendType==0){
				smap["level"] = "【普通】"
			}else{
				smap["level"] = "【紧急】"
			}
			
			bbsList << smap
		}
		render bbsList as JSON
	}
	def downloadFile = {
		def attachmentInstance =  Attachment.get(params.id)
		def filename = attachmentInstance.name
		
		response.setHeader("Content-disposition", "attachment; filename=" + new String(filename.getBytes("GB2312"), "ISO_8859_1"))
		response.contentType = ""
		
		def filepath = new File(attachmentInstance.url, attachmentInstance.realName)
		def out = response.outputStream
		def inputStream = new FileInputStream(filepath)
		byte[] buffer = new byte[1024]
		int i = -1
		while ((i = inputStream.read(buffer)) != -1) {
			out.write(buffer, 0, i)
		}
		out.flush()
		out.close()
		inputStream.close()
	}
	private def getRandName ={name->
		def sdf = new SimpleDateFormat("yyyyMMddHHmmssS")//格式化时间输出
		def rname = sdf.format(new Date())//取得当前时间，Date()是java.util包里的，这作为真实名称
		int i = name.lastIndexOf(".")//原名称里倒数第一个"."在哪里
		def ext = name.substring(i+1)//取得后缀，及"."后面的字符
		return rname+"."+ext//拼凑而成
	}
	def uploadFile = {
		def json=[:]
		SystemUtil sysUtil = new SystemUtil()
		
		def uploadPath = sysUtil.getUploadPath()
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
		def realName = getRandName(name)
		f.transferTo(new File(uploadPath,realName))
		
		def attachment = new Attachment()
		attachment.name = name
		attachment.realName = realName
		attachment.type = "mail"
		attachment.url = uploadPath
		attachment.size = f.size
		attachment.upUser = (User) springSecurityService.getCurrentUser()
		attachment.save(flush:true)
		
		json["result"] = "true"
		json["fileId"] = attachment.id
		json["fileName"] = name
		render json as JSON
	}
	def navigation ={
		def json = [identifier:'id',label:'name',items:[]]

		def inbox = ["type":"folder","id":"inbox","name":"收件箱","icon":"mailIconFolderInbox","action":"mail_showInbox","expand":"yes"]
		def saveBox = ["type":"folder","id":"save","name":"草稿箱","action":"mail_showInbox"]
		def sendBox = ["type":"folder","id":"send","name":"已发送","action":"mail_showInbox"]
		def otherBox = ["type":"folder","id":"other","name":"其他文件夹","children":[]]

		otherBox.children << ["id":"deleted","name":"已删除","action":"mail_showInbox"]
		otherBox.children << ["id":"trash","name":"垃圾邮件","action":"mail_showInbox"]

		json.items << inbox
		json.items << saveBox
		json.items << sendBox
		json.items << otherBox
		render json as JSON
	}
	private def getEmailBoxListLayout={
		def gridUtil = new GridUtil()
		gridUtil.buildLayoutJSON(new EmailBox())
	}
	def inboxGrid ={
		def json=[:]
		if(params.refreshHeader){
			def layout = getEmailBoxListLayout()
			layout.each {item ->
				if(item.field.equals("subject")){
					item.formatter = "formatSubject"
				}else if("emailStatus".equals(item.field)){
					item.formatter = "formatEmailStatus"
				}
			}
			json["gridHeader"] = layout
		}
		def totalNum = 0
		if(params.refreshData){
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			def offset = (nowPage-1) * perPageNum
			def max  = perPageNum
			
			def _json = [identifier:'id',label:'name',items:[]]
			Integer boxType = 0
			
			switch (params.id){
				case [null,"inbox"]://收件箱
					boxType = 1
					break
				case "save"://草稿箱
					boxType = 0
					break
				case "send"://已发送
					boxType = 2
					break
				case "deleted"://已删除
					boxType = 3
					break
				case "trash"://垃圾邮件
					boxType = 4
					break
			}
			
			def user = (User) springSecurityService.getCurrentUser()
			def _dataList = EmailBox.findAllByMailUserAndBoxType(user,boxType,[ sort:"createdDate", order:"desc"])
			totalNum = _dataList.size()
			
			_dataList.eachWithIndex{ item,idx->
				if(idx>=offset && idx <offset+max){
					def sMap =[:]
					sMap["rowIndex"] = idx+1
					sMap["id"] = item.id
					sMap["sender"] = item.sender
					sMap["to"] = item.receiver
					sMap["subject"] = item.subject
					sMap["sent"] = item.getFormattedDate()
					sMap["content"] = item.content
					
					def imgStr = ""
					if(item.emailStatus==0){
						//未读
						imgStr = "images/rosten/actionbar/mail_wd.png"
					}
					
					//判断是否有附件
					def files = Attachment.findAllByBeUseId(item.id)
					if(files && files.size()!=0){
						if("".equals(imgStr)){
							imgStr = "images/rosten/actionbar/attach.png"
						}else{
							imgStr += ",images/rosten/actionbar/attach.png"
						}
						
					}
					
					sMap["emailStatus"] = imgStr
					
					//获取相关附件信息
//					def attachList = []
					def attachList=""
					Attachment.findAllByBeUseId(item.id).each{att->
//						def _smap =[:]
//						_smap["fileId"] = att.id
//						_smap["fileName"] = att.name 
//						attachList << _smap
						if("".equals(attachList)){
							attachList = att.id + "&" + att.name
						}else{
							attachList += "," + att.id + "&" + att.name
						}
						
					}
					
					sMap["attachList"] = attachList
					
					_json.items+=sMap
				}
			}
			json["gridData"] = _json
		}
		if(params.refreshPageControl){
			json["pageControl"] = ["total":totalNum.toString()]
		}
		render json as JSON
	}
	def quick = {
		def model = [:]
		render(view:'/mail/quick',model:model)
	}
	def mailBox = {
		def model = [:]
		render(view:'/mail/mail',model:model)
	}
	def mail_delete = {
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def emailBox = EmailBox.get(it)
				if(emailBox){
					emailBox.boxType = 3
					emailBox.save(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def mail_destroy = {
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def emailBox = EmailBox.get(it)
				if(emailBox){
					emailBox.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def mail_changeEmailStatus = {
		//设置邮件标记为已读状态
		def json =[:]
		def email = EmailBox.get(params.id)
		if(email && email.emailStatus!=1){
			email.emailStatus = 1
			if(email.save(flush:true)){
				json["result"] = "true"
			}else{
				email.errors.each{
					println it
				}
				json["result"] = "false"
			}
		}else{
			json["result"] = "true"
		}
		render json as JSON
	} 
	def mail_save = {
		def json =[:]
		def user = (User) springSecurityService.getCurrentUser()
		
		//保存发件信息
		def sendMail = EmailBox.get(params.id)
		if(!sendMail){
			sendMail = new EmailBox()
		}
		sendMail.sender = user.username
		sendMail.senderCode = user.id
		sendMail.receiver = params.to
		sendMail.receiverCode = params.to
		sendMail.subject = params.subject
		sendMail.content = params.content
		sendMail.sendDate = new Date()
		sendMail.boxType = 0
		sendMail.mailUser = user
		
		if(sendMail.save(flush:true)){
			//增加附件的处理
			if(params.files){
				params.files.split(",").each{
					def attach = Attachment.get(it)
					if(attach.beUseId==null){
						attach.beUseId = sendMail.id
						attach.save(flush:true)
					}
				}
			}
			json["result"] = "true"
		}else{
			sendMail.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	def mail_send = {
		def json =[:]
		
		def oktos=[]
		def tos = params.to.split(",")
		tos.each{to->
			if(to.contains("@") || User.findByUsername(to)!=null){
				oktos<<to
			}
		}
		if(oktos.size==0){
			json["result"] = "noUser"
			render json as JSON
			return
		}
		
		def user = (User) springSecurityService.getCurrentUser()
		
		oktos.each{_to->
			/*
			 * 判断是否为外部邮件
			 */
			
			if(_to.contains("@")){
				//公网邮件
				if(user.isOnMail && user.company.isOnMail){
					request.mailUsername = user.emailConfig.loginName
					request.mailPassword = user.emailConfig.loginPassword
					request.mailHost = user.emailConfig.smtp
					request.mailPort = user.emailConfig.port
					
					sendMail {
//						async true
						to _to
						from user.emailConfig.loginName
						subject params.subject
						body params.content
					 }
				}
			}else{
				//内部邮件
				/*
				 //增加收件人信息
				 if(!Contact.findByMailUserAndName(user,params.to)){
					 def contact = new Contact()
					 contact.mailUser = user
					 contact.name = params.to
					 contact.email = params.to
					 contact.save()
				 }
				 */
				 //创建收件信息
				 def receiveMail = new EmailBox()
				 receiveMail.sender = user.username
				 receiveMail.senderCode = user.id
				 receiveMail.receiver = _to
				 receiveMail.receiverCode = _to
				 receiveMail.subject = params.subject
				 receiveMail.content = params.content
				 receiveMail.sendDate = new Date()
				 receiveMail.boxType = 1
				 receiveMail.mailUser = User.findByUsername(_to)
				 receiveMail.save()
				 
				 //增加附件的处理
				 if(params.files){
					 params.files.split(",").each{
						 def oldAttach = Attachment.get(it)
						 
						 def attach = new Attachment()
						 attach.name = oldAttach.name
						 attach.type = oldAttach.type
						 attach.url = oldAttach.url
						 attach.realName = oldAttach.realName
						 attach.size = oldAttach.size
						 attach.upUser = User.findByUsername(_to)
						 attach.beUseId = receiveMail.id
						 attach.save()
					 }
				 }
			}
			
		}
		
		//保存发件信息
		def sendMail = EmailBox.get(params.id)
		if(!sendMail){
			sendMail = new EmailBox()
		}
		sendMail.sender = user.username
		sendMail.senderCode = user.id
		sendMail.receiver = params.to
		sendMail.receiverCode = params.to
		sendMail.subject = params.subject
		sendMail.content = params.content
		sendMail.sendDate = new Date()
		sendMail.boxType = 2
		sendMail.mailUser = user
		
		if(sendMail.save(flush:true)){
			
			//增加附件的处理
			if(params.files){
				params.files.split(",").each{
					def attach = Attachment.get(it)
					if(attach.beUseId==null){
						attach.beUseId = sendMail.id
						attach.save(flush:true)
					}
				}
			}
			
			json["result"] = "true"
		}else{
			sendMail.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	def contactData ={
		def json = [identifier:'id',label:'name',items:[]]
		def user = (User) springSecurityService.getCurrentUser()
		
		GB2Alpha gb2Alpha = new GB2Alpha();
		
		//获取Contact人员
		Contact.findAllByMailUser(user).each{
			def _data = ["id":it.id,"name":it.name,email:it.email,phone:it.tellCall,type:"contact",first:gb2Alpha.String2Alpha(it.name)]
			json.items << _data
		}
		
		//获取当前单位人员信息
		User.findAllByCompany(user.company).each{
			if(!it.equals(user)){
				def _data = ["id":it.id,"name":it.getFormattedName(),email:it.username,phone:it.telephone,type:"user",,first:gb2Alpha.String2Alpha(it.getFormattedName())]
				json.items << _data
			}
		}
		
		json.items.unique()
		
		render json as JSON
	}
	def getDepart={
		def user = (User) springSecurityService.getCurrentUser()
		def dataList = Depart.findAllByCompanyAndParent(user.company,null)
		def json = [identifier:'id',label:'name',items:[]]
		dataList.each{
			def sMap = ["id":it.id,"name":it.departName,"type":"depart","parentId":it.parent?.id,"children":[]]
			json.items+=sMap
		}
		render json as JSON
	}
	def mail_getDepartChild ={
		def json = []
		
		def depart = Depart.get(params.id)
		if(depart.children && depart.children.size()!=0){
			//获取部门
			depart.children.each{
				def sMap = ["id":it.id,"name":it.departName,"type":"depart","parentId":it.parent?.id,"children":[]]
				json << sMap
			}
		}else{
			//获取用户
			def user = (User) springSecurityService.getCurrentUser()
			depart.getAllUser().each{
				if(!user.equals(it)){
					def sMap = ["id":it.id,"username":it.username,"type":"user","name":it.getFormattedName()]
					json << sMap
				}
			}
		}
		render json as JSON
	}
	def index() {
	}
}
