package com.rosten.app.system

import java.text.SimpleDateFormat
import java.util.Date;

class SerialNo {
	String id
	
	String macAddress
	
	String serialNo
	
	Date createdDate = new Date()
	def getFormattedCreateDate(){
		if(createdDate){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			return sd.format(createdDate)
		}else{
			return ""
		}
	}
	
    static constraints = {
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_SERIALNO"
	}
}
