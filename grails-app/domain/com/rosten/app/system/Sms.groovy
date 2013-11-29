package com.rosten.app.system

import java.text.SimpleDateFormat
import java.util.Date

import com.rosten.app.annotation.GridColumn

class Sms {
	
	String id
	
	//发送者
	@GridColumn(name="发送者")
	String sender
	
	//接收人
	@GridColumn(name="接收人")
	String sendto
	
	//短信内容
	@GridColumn(name="短信内容")
	String content
	
	//是否成功
	boolean isSucessful = true
	
	@GridColumn(name="是否成功")
	def getIsSucessfulValue(){
		if(isSucessful)return "是"
		else return "否"
	}
	
	//发送时间
	Date createdDate = new Date()
	
	@GridColumn(name="短信时间")
	def getFormattedCreateDate(){
		if(createdDate){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			return sd.format(createdDate)
		}else{
			return ""
		}
	}
	
	static transients = ["isSucessfulValue","formattedCreateDate"]
	
	//所属单位
	static belongsTo = [company:Company]
	
	static constraints = {
//		question nullable:true,blank:true
//		questionAnswer nullable:true,blank:true
	}
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		content sqlType:"text"
		table "ROSTEN_SMS"
	}
}
