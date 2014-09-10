package com.rosten.app.system

import com.rosten.app.annotation.GridColumn

class SmsGroup {
	String id
	
	//群组名称
	@GridColumn(name="群组名称" ,width="150px",formatter="smsGroup_formatTopic")
	String groupName
	
	//群组成员
	@GridColumn(name="群组成员")
	String members
	
	//描述
	@GridColumn(name="描述")
	String description
	
	//所属用户
	static belongsTo = [user:User]
	
    static constraints = {
		members nullable:true,blank:true
		description nullable:true,blank:true
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		members sqlType:"text"
		description sqlType:"text"
		table "ROSTEN_SMSGROUP"
	}
}
