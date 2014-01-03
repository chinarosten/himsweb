package com.rosten.app.mail

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.User

class Contact {
	String id
	
	@GridColumn(name="姓名",width="100px")
	String name
	
	@GridColumn(name="邮箱地址")
	String email
	
	@GridColumn(name="手机号码")
	String tellCall
	
	ContactGroup contactGroup
	
	static belongsTo = [mailUser:User]
	
    static constraints = {
		contactGroup nullable:true,blank:true
		tellCall nullable:true,blank:true
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_EMAIL_CONTACT"
	}
}
