package com.rosten.app.mail

import java.util.List;

import com.rosten.app.system.User

class ContactGroup {
	
	String id
	
	String groupName
	
	static belongsTo = [mailUser:User]
	
	List contacts
	static hasMany=[contacts:Contact]
	
    static constraints = {
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_EMAIL_CONTACTGROUP"
	}
}
