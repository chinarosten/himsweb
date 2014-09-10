package com.rosten.app.system

import java.util.Date;

import com.rosten.app.annotation.GridColumn

class Resource {
	String id
	
	@GridColumn(name="标记",width="30px",colIdx=1,formatter="formatResourceTab")
	def getTab(){
		if(isDefault){
			return "images/rosten/navigation/Logo.gif"
		}else{
			return ""
		}
	}
	
	//模块名称
	@GridColumn(name="模块名称",colIdx=2)
	def getModelName(){
		if(model){
			return model.modelName
		}
		return "无模块"
	}
	
	//资源名称
	@GridColumn(name="资源名称",formatter="resource_formatTopic")
	String resourceName
	
	//显示顺序
	Integer serialNo
	
	//是否缺省资源
	boolean isDefault = false
	
	//资源路径
	@GridColumn(name="资源路径")
	String url
	
	//图标路径
	@GridColumn(name="图标路径",width="300px")
	String imgUrl
	
	//描述
	@GridColumn(name="描述")
	String description
	
	//所属单位
	static belongsTo = [model:Model]
	
	//创建日期
	Date createdDate = new Date()
	
    static constraints = {
		resourceName blank: false
		url nullable:true,blank:true
		imgUrl nullable:true,blank:true
		serialNo nullable:true,blank:true
		isDefault nullable:true,blank:true
		description nullable:true,blank:true
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		description sqlType:"text"
		table "ROSTEN_RESOURCE"
	}
	def beforeDelete(){
		PermissionResource.removeAll(this)
	}
}
