package com.rosten.app.mail

import com.rosten.app.system.User

class Attachments {
	String id
	
	String attachmentUrl
	
	String filename
	
	String filesize
	
	static belongsTo = [mailUser:User,email:EmailBox]
	
    static constraints = {
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_EMAIL_ATTACHMENTS"
	}
}
