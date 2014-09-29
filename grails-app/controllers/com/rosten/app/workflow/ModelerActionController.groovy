package com.rosten.app.workflow

import grails.converters.JSON

class ModelerActionController {
	def imgPath ="images/rosten/actionbar/"
	
	def flowBusinessForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif","flowBusiness_add")
		render actionList as JSON
	}
	
	def flowBusinessView ={
		def actionList =[]
		def strname ="flowBusiness"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新增",imgPath + "add.png","add_" + strname)
		actionList << createAction("删除",imgPath + "delete.png","delete_" + strname)
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		actionList << createAction("关联流程",imgPath + "flow_start.png",strname + "_addFlow")
		actionList << createAction("取消关联流程",imgPath + "flow_stop.png",strname + "_deleteFlow")
		render actionList as JSON
	}
	
	def molelerView ={
		def actionList =[]
		def strname ="modeler"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新增",imgPath + "add.png","add_" + strname)
		//actionList << createAction("编辑",imgPath + "ok.png","change_" + strname)
		actionList << createAction("删除",imgPath + "delete.png","delete_" + strname)
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		actionList << createAction("流程部署",imgPath + "deploy.png","deploy_" + strname)
		actionList << createAction("导入xml文件",imgPath + "hf.gif","import_" + strname)
		actionList << createAction("导出xml文件",imgPath + "export.png","export_" + strname)
		
		render actionList as JSON
	}
	
	def flowDefinedView ={
		def actionList =[]
		def strname ="flow"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("删除",imgPath + "delete.png","delete_" + strname)
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		actionList << createAction("查看流程图",imgPath + "yl.gif","read_" + strname)
		actionList << createAction("查看xml文件",imgPath + "export.png","export_" + strname)
		actionList << createAction("挂起流程",imgPath + "flow_stop.png","stop_" + strname)
		actionList << createAction("重启流程",imgPath + "flow_start.png","start_" + strname)
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
