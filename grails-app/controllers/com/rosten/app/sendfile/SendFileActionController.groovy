package com.rosten.app.sendfile

import grails.converters.JSON

class SendFileActionController {
	def imgPath ="images/rosten/actionbar/"
	
	def sendFileForm ={
		def actionList =[]
		actionList << createAction("返回",imgPath + "quit_1.gif","returnToView")
		actionList << createAction("创建正文",imgPath + "add.png","sendFile_addWord")
		render actionList as JSON
	}
	
	def sendFileView = {
		def actionList =[]
		def strname = "sendFile"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新建发文",imgPath + "add.png","add_"+ strname)
		actionList << createAction("查看发文",imgPath + "read.gif","read_" + strname)
		
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
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
