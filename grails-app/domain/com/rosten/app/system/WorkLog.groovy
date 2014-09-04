package com.rosten.app.system

import java.text.SimpleDateFormat
import java.util.Date;

import com.rosten.app.annotation.GridColumn

class WorkLog {
	String id
	
	//创建时间
	Date createDate = new Date()

	@GridColumn(name="日期",width="106px",colIdx=1)
	def getFormattedCreatedDate(){
		if(createDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(createDate)
		}else{
			return ""
		}
	}
	
	//日志内容
	@GridColumn(name="日志内容",colIdx=2,formatter="personWorkLog_formatTopic")
	String content
	
	//日志类型
	@GridColumn(name="日志类型",width="100px" ,colIdx=3)
	String logType = "工作日志"
	
	//所属用户
	static belongsTo = [user:User]
	
    static constraints = {
		content nullable:true,blank:true
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		content sqlType:"text"
		table "ROSTEN_WORKLOG"
	}
}
