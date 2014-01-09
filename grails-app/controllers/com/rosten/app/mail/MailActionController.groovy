package com.rosten.app.mail

import grails.converters.JSON

class MailActionController {
	def imgPath ="images/rosten/actionbar/"
	
	def newMessage = {
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("发送",imgPath + "ok.png","send_mail")
		actionList << createAction("存草稿",imgPath + "save.gif","save_mail")
		actionList << createAction("取消",imgPath + "delete.png","cancel_mail")
		
		render actionList as JSON
	}
	def showMessage = {
		def webPath = request.getContextPath() + "/"
		def actionList = []
		
		actionList << createAction("返回",imgPath + "quit_1.gif","cancel_mail") 
		
		switch (params.id){
			case [null,"inbox"]://收件箱
				actionList << createAction("回复",imgPath + "read.gif","receive_mail")
				actionList << createAction("转发",imgPath + "read.gif","repeat_mail")
				actionList << createAction("删除",imgPath + "delete.png","delete_mail")
				break
			case "send"://已发送
				actionList << createAction("再次编辑发送",imgPath + "read.gif","resend_mail")
				actionList << createAction("回复",imgPath + "read.gif","receive_mail")
				actionList << createAction("转发",imgPath + "read.gif","repeat_mail")
				actionList << createAction("删除",imgPath + "delete.png","delete_mail")
				break
			case "deleted"://已删除
				actionList << createAction("回复",imgPath + "read.gif","receive_mail")
				actionList << createAction("转发",imgPath + "read.gif","repeat_mail")
				break
			case "trash"://垃圾邮件
				actionList << createAction("回复",imgPath + "read.gif","receive_mail")
				actionList << createAction("转发",imgPath + "read.gif","repeat_mail")
				break
		}
		
		render actionList as JSON
	}
	def inbox = {
		def webPath = request.getContextPath() + "/"
		def actionList = []
		switch (params.id){
			case [null,"inbox"]://收件箱
				actionList << createAction("删除",imgPath + "delete.png","delete_mail")
				break
			case "save"://草稿箱
				actionList << createAction("删除",imgPath + "delete.png","delete_mail")
				break
			case "send"://已发送
				actionList << createAction("删除",imgPath + "delete.png","delete_mail")
				break
			case "deleted"://已删除
				actionList << createAction("彻底删除",imgPath + "delete.png","destroy_mail")
				break
			case "trash"://垃圾邮件
				actionList << createAction("彻底删除",imgPath + "delete.png","destroy_mail")
				break
		}
		
		render actionList as JSON
	}
	private def createAction={name,img,action->
		def model =[:]
		model["name"] = name
		model["img"] = img
		model["action"] = action
		return model
	}
    def index() { }
}
