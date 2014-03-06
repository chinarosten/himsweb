package com.rosten.app.bbs

import grails.converters.JSON
import com.rosten.app.system.User

class BbsActionController {
	
	def imgPath ="images/rosten/actionbar/"
	
	def bbsForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
		def user = User.get(params.userid)
		if(params.id){
			def bbs = Bbs.get(params.id)
			if(user.equals(bbs.currentUser)){
				//当前处理人
				if("起草".equals(bbs.status)){
					actionList << createAction("保存",webPath + imgPath + "Save.gif","bbs_add")
					actionList << createAction("提交",webPath + imgPath + "submit.png","bbs_submit")
				}else if("待发布".equals(bbs.status)){
					actionList << createAction("填写意见",webPath + imgPath + "sign.png","bbs_addComment")
					actionList << createAction("发布",webPath + imgPath + "ok.png","bbs_agrain")
					actionList << createAction("不同意",webPath + imgPath + "back.png","bbs_notAgrain")
				}
			}
		}else{
			//新建公告
			actionList << createAction("保存",webPath + imgPath + "Save.gif","bbs_add")
		}
		render actionList as JSON
	}
	
	def bbsConfigView = {
		def actionList =[]
		def strname = "bbsConfig"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("保存",imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	def allbbsView ={
		def actionList =[]
		def strname = "bbs"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("查看公告",imgPath + "read.gif","read_" + strname)
		actionList << createAction("删除公告",imgPath + "delete.png","delete_" + strname)
		actionList << createAction("状态迁移",imgPath + "changeStatus.gif",strname + "_changeStatus")
		actionList << createAction("用户迁移",imgPath + "changeUser.gif",strname + "_changeUser")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	def newbbsView ={
		def actionList =[]
		def strname = "bbs"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("查看公告",imgPath + "read.gif","read_" + strname)
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	def bbsView = {
		def actionList =[]
		def strname = "bbs"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新建公告",imgPath + "add.png","add_"+ strname)
		actionList << createAction("查看公告",imgPath + "read.gif","read_" + strname)
		
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
