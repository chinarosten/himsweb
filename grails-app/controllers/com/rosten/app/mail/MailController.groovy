package com.rosten.app.mail

import grails.converters.JSON
import com.rosten.app.util.Util

class MailController {

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
	def inboxGrid ={
		def json=[:]
		if(params.refreshHeader){
			def layout =[]
			layout<<["name":"序号","width":"60px","colIdx":0,"field":"rowIndex"]
			layout<<["name":"发件人","field":"sender","width":"auto"]
			layout<<["name":"主题","field":"subject","width":"auto"]
			layout<<["name":"发件时间","field":"sent","width":"auto"]

			json["gridHeader"] = layout
		}
		if(params.refreshData){
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			def offset = (nowPage-1) * perPageNum
			def max  = perPageNum
			
			def _json = [identifier:'id',label:'name',items:[]]
//			hairdressService.getAllCardType(offset,max,company,type).eachWithIndex{ item,idx->
//				def sMap = ["rowIndex":idx+1,"id":item.id,"title":item.cardtypeName]
//				_json.items+=sMap
//			}
			
			def _data = ["rowIndex":1,"type":"message","id":"node1.1","sender":"张三","subject":"主题","sent": "2013-12-30"]
	
			_json.items << _data
			
			json["gridData"] = _json
			
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
