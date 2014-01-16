package com.rosten.app.system

import java.util.Date;

class Attachment {
	String id
	
	//附件名称
	String name
	
	//附件类型：nomal:普通附件;mail:邮件附件;logo:logo附件
	String type = "normal"
	
	//附件存放地址
	String url
	
	//附件存放的实际名称
	String realName
	
	//关联数据
	String beUseId
	
	//附件大小
	Double size
	
	//上传时间
	Date createdDate = new Date()
	
	//上传人
	static belongsTo = [upUser:User]
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_ATTACHMENT"
	}
    static constraints = {
		beUseId nullable:true,blank:true
    }
}
