package com.rosten.app.sendfile

import grails.converters.JSON

class SendFileActionController {
	def imgPath ="images/rosten/actionbar/"
	
	def sendFileLabelForm ={
		def webPath = request.getContextPath() + "/"
		def actionList =[]
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath +imgPath + "Save.gif","sendfileLabel_add")
		render actionList as JSON
	}
	
	def sendFileLabelView ={
		def actionList =[]
		def strname = "sendFileLabel"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新建发文代字",imgPath + "add.png","add_"+ strname)
		actionList << createAction("删除发文代字",imgPath + "delete.png","delete_" + strname)
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	def sendFileWord ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("退出",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("新建文档",webPath + imgPath + "word_new.png","weboffice_newDoc")
		actionList << createAction("打开文档",webPath + imgPath + "word_open.png","weboffice_docOpen")
		actionList << createAction("保存文档",webPath + imgPath + "Save.gif","word_save")
		actionList << createAction("打印文档",webPath + imgPath + "word_print.png","weboffice_print")
		actionList << createAction("隐藏/显示菜单",webPath + imgPath + "word_menu.png","word_menu")
		
		render actionList as JSON
	}
	def sendFileForm ={
		def webPath = request.getContextPath() + "/"
		def actionList =[]
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath +imgPath + "Save.gif","sendfile_add")
		actionList << createAction("创建正文",webPath +imgPath + "word_new.png","sendFile_addWord")
		actionList << createAction("提交",webPath +imgPath + "submit.png","sendfile_submit")
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
