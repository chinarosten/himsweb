package com.rosten.app.account

import grails.converters.JSON
import com.rosten.app.system.User

class AccountActionController {
	def imgPath ="images/rosten/actionbar/"
	
	def accountStatic ={
		def actionList =[]
		def strname = "accountStatic"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		
		render actionList as JSON
	}
	
	def accountForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif","account_add")
		render actionList as JSON
	}
	
	def accountView = {
		render createCommonAction(null,"account",true) as JSON
	}
	
	def categoryForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif","category_add")
		render actionList as JSON
	}
	
	def categoryView = {
		render createCommonAction(null,"category",true) as JSON
	}
	
	def projectForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif","project_add")
		render actionList as JSON
	}
	
	def projectView = {
		render createCommonAction(null,"project",true) as JSON
	}
	private def createCommonAction={actionList,strname,args->
		if(!(actionList && actionList instanceof List)){
			actionList =[]
		}
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新增",imgPath + "add.png","add_" + strname)
		actionList << createAction("查看",imgPath + "read.gif","read_" + strname)
		
		if(args){
			//允许修改，删除操作
			//actionList << createAction("修改",imgPath + "ok.png","change_" + strname)
			actionList << createAction("删除",imgPath + "delete.png","delete_" + strname)
		}
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		return actionList
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
