package com.rosten.app.meeting

import java.text.SimpleDateFormat
import java.util.Date;

import com.rosten.app.system.User;

class MeetingLog {
	String id
	
	User user
	
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
	
	static belongsTo = [meeting:Meeting]
    static constraints = {
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_MEETING_LOG"
	}
}
