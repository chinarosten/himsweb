package com.rosten.app.system

class SerialNo {
	String id
	
	String macAddress
	
	String serialNo
	
    static constraints = {
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_SERIALNO"
	}
}
