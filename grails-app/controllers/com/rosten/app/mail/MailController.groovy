package com.rosten.app.mail

import grails.converters.JSON

class MailController {
	
	def navigation ={
		def json = [identifier:'id',label:'name',items:[]]
		
		def inbox = ["type":"folder","id":"inbox","name":"收件箱","icon":"mailIconFolderInbox"]
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

    def index() { }
}
