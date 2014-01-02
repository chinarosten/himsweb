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
	
	static belongsTo = [mailUser:User,contactGroup:ContactGroup]
	
    static constraints = {
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_EMAIL_CONTACT"
	}
}
