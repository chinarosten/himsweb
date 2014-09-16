package com.rosten.app.account

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company

class Category {
	String id
	
	//所属项目
	Project project
	
	@GridColumn(name="所属项目")
	def getProjectName(){
		return project?project.name:""
	}
		
	//类型名称
	@GridColumn(name="用途名称",formatter="category_formatTitle")
	String name
	
	static belongsTo = [company:Company]
	
    static constraints = {
		project nullable:true,blank:true
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_ACCOUNT_CATEGORY"
	}
}
