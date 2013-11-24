package com.rosten.app.system

import grails.plugin.springsecurity.annotation.Secured

class SecureController {
	
	@Secured(['ROLE_ADMIN'])
	def index() {
		render 'Secure access only'
	}
}
