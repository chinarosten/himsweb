package com.rosten.app.system

class Role {
	
	String id
	
	String authority

	static mapping = {
		cache true
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_ROLE"
	}

	static constraints = {
		authority blank: false, unique: true
	}
}
