package com.rosten.app.sendfile

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.User
import com.rosten.app.system.Company
import com.rosten.app.system.Attachment

import java.text.SimpleDateFormat
import java.util.Date
import java.util.List;

class SendFile {
	
	String id
	
	//标题
	String subject
	
	//主题词
	String subjectKey
	
	List attachments
	static hasMany=[attachments:Attachment]
	
	
	//拟稿人
	User drafter
	
	//拟稿时间
	@GridColumn(name="拟稿时间",width="150px")
	Date createDate = new Date()
	
	def getFormattedDate(){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
		return sd.format(createDate)
	}
	
	static belongsTo = [company:Company]
	
    static constraints = {
		subjectKey nullable:true,blank:true
		
		
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_SENDFILE_SENDFILE"
	}
}
