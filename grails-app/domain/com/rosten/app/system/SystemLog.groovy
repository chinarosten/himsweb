package com.rosten.app.system

import java.text.SimpleDateFormat
import java.util.Date;
import com.rosten.app.annotation.GridColumn

class SystemLog {
	
	String id
	
	User user
	
	@GridColumn(name="用户",width="80px")
	def getUserName(){
		if(user)return user.getFormattedName()
		else return ""
	}
	
	//ip地址
	@GridColumn(name="ip地址")
	String ipAddress
	
	//mac地址
	@GridColumn(name="mac地址")
	String macAddress
	
	//日志内容
	@GridColumn(name="内容")
	String content
	
	//创建时间
	Date createDate = new Date()
	
	@GridColumn(name="时间",width="150px")
	def getFormattedCreatedDate(){
		if(createDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(createDate)
		}else{
			return ""
		}
	}
	
	@GridColumn(name="单位名称",colIdx=1)
	def getFormattedCompany(){
		if(company) return company.companyName
		else return ""
	}
	
	static belongsTo = [company:Company]
	
    static constraints = {
		content nullable:true,blank:true
		company nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_LOG"
	}
}
