package com.rosten.app.sendfile

import grails.converters.JSON
import com.rosten.app.system.User

class SendFileActionController {
	def imgPath ="images/rosten/actionbar/"
	
	def allSendFileView ={
		def actionList =[]
		def strname = "sendFile"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("查看发文",imgPath + "read.gif","read_" + strname)
		
		def user = User.get(params.userId)
		if("admin".equals(user.getUserType())){
			actionList << createAction("删除发文",imgPath + "delete.png","delete_" + strname)
			actionList << createAction("状态迁移",imgPath + "changeStatus.gif",strname + "_changeStatus")
			actionList << createAction("用户迁移",imgPath + "changeUser.gif",strname + "_changeUser")
		}
		
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
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
		
		def user = User.get(params.userid)
		if(params.id){
			def sendFile = SendFile.get(params.id)
			if(user.equals(sendFile.currentUser)){
				//当前处理人
				switch (sendFile.status){
					case "拟稿":
						actionList << createAction("保存",webPath +imgPath + "Save.gif","sendfile_add")
						actionList << createAction("创建正文",webPath +imgPath + "word_new.png","sendFile_addWord")
						actionList << createAction("提交",webPath +imgPath + "submit.png","sendfile_submit")
						break;
					case "审核":
						actionList << createAction("保存",webPath +imgPath + "Save.gif","sendfile_add")
						actionList << createAction("填写意见",webPath +imgPath + "sign.png","addComment")
						actionList << createAction("签发",webPath +imgPath + "ok.png","sendfile_agrain")
						actionList << createAction("不同意",webPath +imgPath + "back.png","sendfile_notAgrain")
						break;
					case "已签发":
						actionList << createAction("保存",webPath +imgPath + "Save.gif","sendfile_add")
						actionList << createAction("填写意见",webPath +imgPath + "sign.png","addComment")
						actionList << createAction("分发",webPath +imgPath + "send.png","sendfile_send")
						actionList << createAction("归档",webPath +imgPath + "gd.png","sendfile_achive")
						break;
					case "已归档":
						if("admin".equals(user.getUserType())){
							actionList << createAction("重新分发",webPath +imgPath + "send.png","sendfile_send")
						}
						break;
				}
			}
		}else{
			//新建
			actionList << createAction("保存",webPath +imgPath + "Save.gif","sendfile_add")
		}
		
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
	def sendFileConfigView ={
		def actionList =[]
		def strname = "sendFileConfig"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("保存",imgPath + "Save.gif",strname + "_save")
		
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
