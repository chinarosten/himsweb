package com.rosten.app.mail

import grails.converters.JSON

class MailActionController {
	def imgPath ="images/rosten/actionbar/"
	
	def inbox = {
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("查看",imgPath + "read.gif","read_mail")
		actionList << createAction("删除",imgPath + "delete.png","delete_mail")
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
