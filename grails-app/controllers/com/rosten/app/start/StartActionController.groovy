package com.rosten.app.start

import grails.converters.JSON

class StartActionController {
	
	def imgPath ="images/rosten/actionbar/"
	
	def gtaskView = {
		def actionList =[]
		def strname = "gtask"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("删除",imgPath + "delete.png","delete_" + strname)
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
