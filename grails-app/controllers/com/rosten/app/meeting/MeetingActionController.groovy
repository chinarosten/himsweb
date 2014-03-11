package com.rosten.app.meeting

import grails.converters.JSON
import com.rosten.app.system.User

class MeetingActionController {
	def imgPath ="images/rosten/actionbar/"
	
	def meetingConfigView = {
		def actionList =[]
		def strname = "meetingConfig"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("保存",imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	def meetingForm ={
		def webPath = request.getContextPath() + "/"
		def actionList =[]
		def strname = "meeting"
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
		def user = User.get(params.userid)
		if(params.id){
			def meeting = Meeting.get(params.id)
			if(user.equals(meeting.currentUser)){
				//当前处理人
				switch (meeting.status){
					case "拟稿":
						actionList << createAction("保存",webPath +imgPath + "Save.gif",strname + "_add")
						actionList << createAction("提交",webPath +imgPath + "submit.png",strname + "_submit")
						break;
					case "审核":
						actionList << createAction("保存",webPath +imgPath + "Save.gif",strname + "_add")
						actionList << createAction("填写意见",webPath +imgPath + "sign.png","addComment")
						actionList << createAction("同意",webPath +imgPath + "ok.png",strname + "_agrain")
						actionList << createAction("不同意",webPath +imgPath + "back.png",strname + "_notAgrain")
						break;
					case "已签发":
						actionList << createAction("保存",webPath +imgPath + "Save.gif",strname +"_add")
						actionList << createAction("填写意见",webPath +imgPath + "sign.png","addComment")
						actionList << createAction("归档",webPath +imgPath + "gd.png",strname +"_achive")
						break;
				}
			}
		}else{
			//新建
			actionList << createAction("保存",webPath +imgPath + "Save.gif",strname + "_add")
		}
		
		render actionList as JSON
	}
	
	def allMeetingView ={
		def actionList =[]
		def strname = "meeting"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("查看公告",imgPath + "read.gif","read_" + strname)
		
		def user = User.get(params.userId)
		if("admin".equals(user.getUserType())){
			actionList << createAction("删除公告",imgPath + "delete.png","delete_" + strname)
			actionList << createAction("状态迁移",imgPath + "changeStatus.gif",strname + "_changeStatus")
			actionList << createAction("用户迁移",imgPath + "changeUser.gif",strname + "_changeUser")
		}
		
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	def meetingView = {
		def actionList =[]
		def strname = "meeting"
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新建会议通知",imgPath + "add.png","add_"+ strname)
		actionList << createAction("查看会议通知",imgPath + "read.gif","read_" + strname)
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
