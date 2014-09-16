package com.rosten.app.account

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company

class Project {
	String id
	
	//项目名称
	@GridColumn(name="项目名称",formatter="project_formatTitle")
	String name
	
	static belongsTo = [company:Company]
	
    static constraints = {
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_ACCOUNT_PROJECT"
	}
}
