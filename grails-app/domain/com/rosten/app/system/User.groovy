package com.rosten.app.system
import com.rosten.app.annotation.GridColumn

class User {

	transient springSecurityService

	String id

	@GridColumn(name="用户名")
	String username
	String password
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	static transients = ['springSecurityService']

	static constraints = {
		username blank: false, unique: true
		password blank: false
	}

	static mapping = { 
		password column: '`password`' 
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_USER"
	}

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role } as Set
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}
}
