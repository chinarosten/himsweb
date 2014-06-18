package com.rosten.app.system

class ButtonAction {
	String id
	
	//所属模块
	Model model
	
	//所属资源
	Resource resource
	
	//action类型;类型分为:view与form,默认为view
	String type = "view"
	
	//action名称
	String name
	
	//图标地址
	String url = "images/rosten/actionBar/rosten.png"
	
	//方法名称
	String actionName
	
	//方法url
	String actionUrl
	
	
    static constraints = {
		actionName nullable:true,blank:true
		actionUrl nullable:true,blank:true
		resource nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_BUTTONACTION"
	}
}
