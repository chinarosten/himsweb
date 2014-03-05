package com.rosten.app.meeting

class Meeting {
	String id

    static constraints = {
		
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_MEETING"
	}
}
