package com.rosten.app.mail

import grails.converters.JSON
import com.rosten.app.util.GridUtil
import com.rosten.app.util.Util

class MailController {
	def springSecurityService
	
	def navigation ={
		def json = [identifier:'id',label:'name',items:[]]

		def inbox = ["type":"folder","id":"inbox","name":"收件箱","icon":"mailIconFolderInbox","action":"mail_showInbox","expand":"yes"]
		def saveBox = ["type":"folder","id":"save","name":"草稿箱"]
		def sendBox = ["type":"folder","id":"send","name":"已发送"]
		def otherBox = ["type":"folder","id":"other","name":"其他文件夹","children":[]]

		otherBox.children << ["id":"deleted","name":"已删除"]
		otherBox.children << ["id":"trash","name":"垃圾邮件"]

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
			def _dataList = EmailBox.findAllByMailUser(springSecurityService.getCurrentUser())
			totalNum = _dataList.size()
			
			_dataList.eachWithIndex{ item,idx->
				if(idx>=offset && idx <offset+max){
					def sMap =[:]
					sMap["rowIndex"] = idx+1
					sMap["id"] = item.id
					sMap["sender"] = item.sender
					sMap["subject"] = item.subject
					sMap["sent"] = item.sent
			
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
		def model = []
		render(view:'/mail/quick',model:model)
	}
	def mailBox = {
		def model = []
		render(view:'/mail/mail',model:model)
	}
	def mailData = {
		def json = [identifier:'id',label:'name',items:[]]
		def _data = ["type":"message","id":"node1.1","name":"today's meeting",folder: 'inbox',label: "today's meeting",sender: "Adam Arlen", sent: "2005-12-19",
			text: "Today's meeting is cancelled.<br>Let's do it tomorrow instead.<br><br>Adam"]

		json.items << _data
		render json as JSON
	}
	def contactData ={
		def json = [identifier:'id',label:'name',items:[]]
		def _data = ["type":"message","id":"node1.1","name":"张三",email: "luhangyu@163.com",phone:"13587878785"]

		json.items << _data
		render json as JSON
	}
	def index() {
	}
}
