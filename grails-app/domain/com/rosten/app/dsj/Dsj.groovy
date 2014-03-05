package com.rosten.app.dsj

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.User
import com.rosten.app.system.Company
import com.rosten.app.system.Attachment

import java.text.SimpleDateFormat
import java.util.Date
import java.util.List

class Dsj {
	String id
	
	@GridColumn(name="流水号",width="60px")
	String serialNo
	
	//条目
	@GridColumn(name="条目")
	String subject
	
	//备注
	String description
	
	//时间
	Date time
	@GridColumn(name="日期")
	def getFormattedTimeDate(){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
		if(time){
			return sd.format(time)
		}else{
			return ""
		}
	}
	
	//读者、附件
	static hasMany=[readers:User,attachments:Attachment]
	
	//缺省读者；*:允许所有人查看,[角色名称]:允许角色
	String defaultReaders="[会议通知普通人员],[会议通知管理员]"
	def addDefaultReader(String userRole){
		if(defaultReaders==null || "".equals(defaultReaders)){
			defaultReaders = userRole
		}else{
			defaultReaders += "," + userRole
		}
	}
	
	//拟稿人
	@GridColumn(name="拟稿人")
	User drafter
	
	//拟稿部门
	String drafterDepart
	
	//当前处理人
	@GridColumn(name="处理者")
	User currentUser
	
	//当前处理人部门
	String currentDepart
	
	//状态
	@GridColumn(name="状态")
	String status = "拟稿"
	
	//拟稿时间
	Date createDate = new Date()
	
	@GridColumn(name="拟稿时间",width="150px")
	def getFormattedDate(){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
		return sd.format(createDate)
	}
	static belongsTo = [company:Company]
	
	static transients = [
		"formattedDate",
		"addDefaultReader"
	]
	static constraints = {
		serialNo nullable:true,blank:true
		description nullable:true,blank:true
	}
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_DSJ"
	}
	def beforeDelete(){
		Dsj.withNewSession{_session->
			DsjComment.findAllByDsj(this).each{item->
				item.delete()
			}
			DsjingLog.findAllByDsj(this).each{item->
				item.delete()
			}
			_session.flush()
		}
	}
	
}
