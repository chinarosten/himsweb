package com.rosten.app.system

class Advertise {

	String id

	String url

	String title

	boolean isUsed = true

	String content

	static constraints = {
	}
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		content sqlType:"text"
		table "ROSTEN_ADVERTISE"
	}
}
