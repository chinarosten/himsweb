package com.rosten.app.mail

import grails.converters.JSON

class MailController {
	
	def navigation ={
		def json = [identifier:'id',label:'name',items:[]]
		
		def inbox = ["type":"folder","id":"inbox","name":"收件箱","icon":"mailIconFolderInbox","action":"mail_showInbox"]
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
    def index() { }
}
