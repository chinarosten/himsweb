package com.rosten.app.dsj

import java.text.SimpleDateFormat
import java.util.Date;

import com.rosten.app.system.User;

class DsjComment {
	String id
	
	User user
	
	String status
	
	String content
	
	//创建时间
	Date createDate = new Date()
	
	def getFormattedCreatedDate(){
		if(createDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(createDate)
		}else{
			return ""
		}
	}
	static belongsTo = [dsj:Dsj]
	
	static constraints = {
	}
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_DSJ_COMMENT"
		content sqlType:"longtext"
	}
}
