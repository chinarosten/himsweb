package com.rosten.app.meeting

import grails.converters.JSON
import com.rosten.app.system.User

class MeetingActionController {
	def imgPath ="images/rosten/actionbar/"
	
	
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
