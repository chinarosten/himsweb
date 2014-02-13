package com.rosten.app.system

import java.text.SimpleDateFormat
import java.util.Date;
import com.rosten.app.annotation.GridColumn

class SystemLog {
	
	String id
	
	User user
	
	@GridColumn(name="用户",width="80px")
	def getUserName(){
		if(user)return user.username
		else return ""
	}
	
	//模块名称
	String modelName
	
	//日志内容
	@GridColumn(name="内容")
	String content
	
	//创建时间
	Date createDate = new Date()
	
	@GridColumn(name="接收时间",width="150px")
	def getFormattedCreatedDate(){
		if(createDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(createDate)
		}else{
			return ""
		}
	}
	
	static belongsTo = [company:Company]
	
    static constraints = {
		modelName nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_LOG"
	}
}
