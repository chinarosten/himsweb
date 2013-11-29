package com.rosten.app.system

import com.rosten.app.annotation.GridColumn

class UserType {
	String id
	
	@GridColumn(name="用户类型")
	String typeName
	
	//描述
	@GridColumn(name="描述")
	String description
	
	static belongsTo = [company:Company]
	
    static constraints = {
		company nullable:true,blank:true
		description nullable:true,blank:true
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_USER_TYPE"
	}
}
