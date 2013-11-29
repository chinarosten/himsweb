package com.rosten.app.system

import java.util.Date;
import com.rosten.app.annotation.GridColumn

class Resource {
	String id
	
	//资源名称
	@GridColumn(name="资源名称")
	String resourceName
	
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
