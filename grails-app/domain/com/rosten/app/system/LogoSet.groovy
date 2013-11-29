package com.rosten.app.system

import java.util.Date;

class LogoSet {
	String id
	
	//logo名称
	String logoName
	
	String imgFileName
	
	String imgFilePath
	
	//初始化模块信息
	Model model
	
	//缺省的css样式
	String cssStyle
	
	//描述
	String description
	
	//创建日期
	Date createdDate = new Date()
	
	static belongsTo = [company:Company]
	
    static constraints = {
		logoName nullable:true,blank:true
		imgFileName nullable:true,blank:true
		imgFilePath nullable:true,blank:true
		model nullable:true,blank:true
		cssStyle nullable:true,blank:true
		description nullable:true,blank:true
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		description sqlType:"text"
		table "ROSTEN_LOGOSET"
	}
}
