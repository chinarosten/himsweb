package com.rosten.app.dsj

import grails.converters.JSON
import com.rosten.app.system.User

class DsjActionController {
	def imgPath ="images/rosten/actionbar/"
	
	def allDsjView ={
		def actionList =[]
		def strname = "dsj"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("查看公告",imgPath + "read.gif","read_" + strname)
		actionList << createAction("删除公告",imgPath + "delete.png","delete_" + strname)
		actionList << createAction("状态迁移",imgPath + "changeStatus.gif",strname + "_changeStatus")
		actionList << createAction("用户迁移",imgPath + "changeUser.gif",strname + "_changeUser")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	def dsjConfigView = {
		def actionList =[]
		def strname = "dsjConfig"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("保存",imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	def dsjForm ={
		def webPath = request.getContextPath() + "/"
		def actionList =[]
		def strname = "dsj"
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
		def user = User.get(params.userid)
		if(params.id){
			def dsj = Dsj.get(params.id)
			if(user.equals(dsj.currentUser)){
				//当前处理人
				switch (dsj.status){
					case "起草":
						actionList << createAction("保存",webPath +imgPath + "Save.gif",strname + "_add")
						actionList << createAction("提交",webPath +imgPath + "submit.png",strname + "_submit")
						break;
					case "审核":
						actionList << createAction("保存",webPath +imgPath + "Save.gif",strname + "_add")
						actionList << createAction("填写意见",webPath +imgPath + "sign.png","addComment")
						actionList << createAction("提交",webPath +imgPath + "submit.png",strname + "_submit")
						break;
				}
			}
		}else{
			//新建
			actionList << createAction("保存",webPath +imgPath + "Save.gif",strname + "_add")
		}
		
		render actionList as JSON
	}
	
	def dsjView = {
		def actionList =[]
		def strname = "dsj"
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新建大事记",imgPath + "add.png","add_"+ strname)
		actionList << createAction("查看大事记",imgPath + "read.gif","read_" + strname)
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
