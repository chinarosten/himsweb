package com.rosten.app.system

import java.util.Date;

class EmailConfig {
	String id
	
	//smtp名称,样例:smtp.qq.com
	String smtp
	
	//端口号
	int port
	
	//登录名
	String loginName
	
	//登录密码
	String loginPassword
	
	//创建日期
	Date createdDate = new Date()
	
	static belongsTo = [user:User]
	
    static constraints = {
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_EMAILCONFIG"
	}
}
