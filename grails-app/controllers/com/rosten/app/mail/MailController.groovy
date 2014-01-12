package com.rosten.app.mail

import grails.converters.JSON

import com.rosten.app.util.GB2Alpha;
import com.rosten.app.util.GridUtil
import com.rosten.app.util.Util
import com.rosten.app.system.User
import com.rosten.app.system.Depart
import com.rosten.app.util.GB2Alpha

class MailController {
	def springSecurityService
	
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
			def _dataList = EmailBox.findAllByMailUserAndBoxType(user,boxType)
			totalNum = _dataList.size()
			
			_dataList.eachWithIndex{ item,idx->
				if(idx>=offset && idx <offset+max){
					def sMap =[:]
					sMap["rowIndex"] = idx+1
					sMap["id"] = item.id
					sMap["sender"] = item.sender
					sMap["to"] = item.receiver
					sMap["subject"] = item.subject
					sMap["sent"] = item.sent
					sMap["content"] = item.content
			
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
			if(User.findByUsername(to)!=null){
				oktos<<to
			}
		}
		if(oktos.size==0){
			json["result"] = "noUser"
			render json as JSON
			return
		}
		
		def user = (User) springSecurityService.getCurrentUser()
		
		oktos.each{to->
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
			receiveMail.receiver = to
			receiveMail.receiverCode = to
			receiveMail.subject = params.subject
			receiveMail.content = params.content
			receiveMail.sendDate = new Date()
			receiveMail.boxType = 1
			receiveMail.mailUser = User.findByUsername(to)
			receiveMail.save()
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
				def _data = ["id":it.id,"name":it.username,email:it.username,phone:it.telephone,type:"user",,first:gb2Alpha.String2Alpha(it.username)]
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
					def sMap = ["id":it.id,"name":it.username,"type":"user"]
					json << sMap
				}
			}
		}
		render json as JSON
	}
	def index() {
	}
}
