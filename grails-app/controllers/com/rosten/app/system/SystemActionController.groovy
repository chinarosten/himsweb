package com.rosten.app.system

import grails.converters.JSON

class SystemActionController {
	def systemService
	def imgPath ="images/rosten/actionbar/"
	
	def personWorkLogForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif","workLog_add")
		render actionList as JSON
	}
	def personWorkLogView = {
		def actionList = createCommonAction(null,"personWorkLog",true)
		render actionList as JSON
	}
	def authorizeView ={
		def actionList = createCommonAction(null,"authorize",true)
		actionList << createAction("启用授权",imgPath + "authorize_start.gif","authorize_start")
		actionList << createAction("取消授权",imgPath + "authorize_cancel.gif","authorize_cancel")
		render actionList as JSON
	}
	def authorizeForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif","authorize_add")
		render actionList as JSON
	}
	def systemLogView ={
		def actionList =[]
		def strname ="systemLog"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		render actionList as JSON
	}
	
	def smsGroupView ={
		render createCommonAction(null,"smsGroup",true) as JSON
	}
	def smsGroupForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif","smsGroup_add")
		
		render actionList as JSON
	}
	def skin ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif","user_uiconfig_add")
		render actionList as JSON
	}
	def smsView ={
		def actionList =[]
		def strname ="sms"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("删除",imgPath + "delete.png","delete_" + strname)
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		render actionList as JSON
	}
	def questionView ={
		def actionList =[]
		def strname ="question"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("查看",imgPath + "read.gif","read_" + strname)
		//actionList << createAction("修改",imgPath + "ok.png","change_" + strname)
		actionList << createAction("删除",imgPath + "delete.png","delete_" + strname)
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		render actionList as JSON
	}
	def questionForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
		def user = User.get(params.userid)
		if(!"normal".equals(user.getUserType())){
			actionList << createAction("保存",webPath + imgPath + "Save.gif","question_add")
		}
		render actionList as JSON
	}
	def advertise ={
		def actionList =[]
		actionList << createAction("退出",imgPath + "quit.gif","returnToMain")
		actionList << createAction("保存",imgPath + "Save.gif","saveAdvertise")
		render actionList as JSON
	}
	def systemTool ={
		def actionList =[]
		actionList << createAction("退出",imgPath + "quit.gif","returnToMain")
		actionList << createAction("机构初始化",imgPath + "init.gif","systemtool_init")
		actionList << createAction("拷贝机构初始化",imgPath + "copyInit.gif","systemtool_copyInit")
		actionList << createAction("删除机构数据",imgPath + "delete.png","systemtool_deleteData")
		actionList << createAction("新增功能模块",imgPath + "add.png","systemtool_addResource")
		actionList << createAction("重新登录系统",imgPath + "refreshSystem.gif","refreshSystem")
		render actionList as JSON
	}
	def logoSet ={
		def actionList =[]
		actionList << createAction("退出",imgPath + "quit.gif","returnToMain")
		actionList << createAction("保存",imgPath + "Save.gif","saveLogoSet")
		actionList << createAction("重新登录系统",imgPath + "refreshSystem.gif","refreshSystem")
		render actionList as JSON
	}
	def serviceView ={
		def actionList = createCommonAction(null,"service",true)
		actionList << createAction("刷新首页",imgPath + "refreshSystem.gif","refreshSystem")
		actionList << createAction("开启服务",imgPath + "s_open.png","open_service")
		actionList << createAction("关闭服务",imgPath + "s_close.png","close_service")
		render actionList as JSON
		
	}
	def serviceForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
//		def user = User.get(params.userid)
//		if(!"normal".equals(user.getUserType())){
			actionList << createAction("保存",webPath + imgPath + "Save.gif","service_add")
//		}
		render actionList as JSON
	}
	def modelView ={
		def _list = createCommonAction(null,"model",true)
		_list << createAction("关联流程",imgPath + "flow_start.png","model_addFlow")
		_list << createAction("取消关联流程",imgPath + "flow_stop.png","model_deleteFlow")
		render  _list as JSON
	}
	def modelForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
//		def user = User.get(params.userid)
//		if(!"normal".equals(user.getUserType())){
			actionList << createAction("保存",webPath + imgPath + "Save.gif","model_add")
//		}
		render actionList as JSON
	}
	def departView ={
		render createCommonAction(null,"depart",true) as JSON
	}
	
	def groupView ={
		render createCommonAction(null,"group",true) as JSON
	}
	def groupForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
//		def user = User.get(params.userid)
//		if(!"normal".equals(user.getUserType())){
			actionList << createAction("保存",webPath + imgPath + "Save.gif","group_add")
//		}
		render actionList as JSON
	}
	def userTypeView ={
		render createCommonAction(null,"userType",true) as JSON
	}
	def userTypeForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
//		def user = User.get(params.userid)
//		if(!"normal".equals(user.getUserType())){
			actionList << createAction("保存",webPath + imgPath + "Save.gif","userType_add")
//		}
		render actionList as JSON
	}
	
	def userSimpleView ={
		def actionList =[]
		actionList << createAction("退出", imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("保存",imgPath + "Save.gif","userSimple_save")
		render actionList as JSON
	}
	
	def userView ={
		def actionList = createCommonAction(null,"user",true)
		actionList << createAction("更改密码",imgPath + "changePassword.gif","changePassword")
//		actionList << createAction("搜索",imgPath + "search.gif","searchGrid")
//		actionList << createAction("取消搜索",imgPath + "search_cancel.gif","cancelSearch")
		render actionList as JSON
	}
	
	def roleView ={
		render createCommonAction(null,"role",true) as JSON
	}
	def roleForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
//		def user = User.get(params.userid)
//		if(!"normal".equals(user.getUserType())){
			actionList << createAction("保存",webPath + imgPath + "Save.gif","role_add")
//		}
		render actionList as JSON
	}
	
	def permissionView ={
		render createCommonAction(null,"permission",true) as JSON
	}
	def permissionForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
//		def user = User.get(params.userid)
//		if(!"normal".equals(user.getUserType())){
			actionList << createAction("保存",webPath + imgPath + "Save.gif","permission_add")
//		}
		render actionList as JSON
	}
	
	def resourceView ={
		render createCommonAction(null,"resource",true) as JSON
	}
	def resourceForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
//		def user = User.get(params.userid)
//		if(!"normal".equals(user.getUserType())){
			actionList << createAction("保存",webPath + imgPath + "Save.gif","resource_add")
//		}
		render actionList as JSON
	}
	
	def administratorForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
		def user = User.get(params.userId)
		//if(user && (systemService.checkIsRosten(user.username) || user.sysFlag)){
			actionList << createAction("保存",webPath + imgPath + "Save.gif","user_add")
		//}
		render actionList as JSON
	}
	def administratorView = {
		render createCommonAction(null,"administrator",true) as JSON
	}
	def departForm = {
		def webPath = request.getContextPath() + "/"
		def actionList =[]
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("保存",imgPath + "Save.gif","depart_save")
		render actionList as JSON
	}
	def companyForm={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
		def user = User.get(params.userid)
		if(user && (systemService.checkIsRosten(user.username) || user.sysFlag)){
			actionList << createAction("保存",webPath + imgPath + "Save.gif","company_add")
		}
		render actionList as JSON
	}
	
	def companyView ={
		render createCommonAction(null,"company",true) as JSON
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
}
