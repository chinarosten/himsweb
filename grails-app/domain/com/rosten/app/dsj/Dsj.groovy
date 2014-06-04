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
	@GridColumn(name="条目",formatter="dsj_formatTitle")
	String subject
	
	//备注
	String description
	
	//时间
	Date time = new Date()
	@GridColumn(name="大事记日期",width="106px",colIdx=2)
	def getFormattedTimeDate(){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
		if(time){
			return sd.format(time)
		}else{
			return ""
		}
	}
	
	def getShowTimeDate(){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
		if(time){
			return sd.format(time)
		}else{
			return ""
		}
	}
	
	//读者、附件
	static hasMany=[readers:User,attachments:Attachment]
	
	//缺省读者；*:允许所有人查看,[角色名称]:允许角色
	String defaultReaders="[大事记普通人员],[大事记管理员]"
	def addDefaultReader(String userRole){
		if(defaultReaders==null || "".equals(defaultReaders)){
			defaultReaders = userRole
		}else{
			defaultReaders += "," + userRole
		}
	}
	
	//拟稿人
	User drafter
	
	@GridColumn(name="拟稿人",width="50px")
	def getDrafterName(){
		if(drafter!=null){
			return drafter.getFormattedName()
		}else{
			return ""
		}
	}
	
	//拟稿部门
	String drafterDepart
	
	//当前处理人
	User currentUser
	
	@GridColumn(name="当前处理人",width="60px",colIdx=5)
	def getCurrentUserName(){
		if(currentUser!=null){
			return currentUser.getFormattedName()
		}else{
			return ""
		}
	}
	//当前处理人部门
	String currentDepart
	
	//处理时间
	Date currentDealDate
	
	//状态
	@GridColumn(name="状态",width="50px")
	String status = "拟稿"
	
	//拟稿时间
	Date createDate = new Date()
	
	@GridColumn(name="拟稿时间",width="106px")
	def getFormattedDate(){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
		return sd.format(createDate)
	}
	
	//流程定义id
	String processDefinitionId
	
	//流程id
	String processInstanceId
	
	//任务id
	String taskId
	
	static belongsTo = [company:Company]
	
	static transients = [
		"formattedDate",
		"addDefaultReader"
	]
	static constraints = {
		serialNo nullable:true,blank:true
		description nullable:true,blank:true
		drafterDepart nullable:true,blank:true
		currentUser nullable:true,blank:true
		currentDepart nullable:true,blank:true
		processInstanceId nullable:true,blank:true
		taskId nullable:true,blank:true
		processDefinitionId nullable:true,blank:true
	}
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_DSJ"
		description sqlType:"longtext"
	}
	def beforeDelete(){
		Dsj.withNewSession{_session->
			DsjComment.findAllByDsj(this).each{item->
				item.delete()
			}
			DsjLog.findAllByDsj(this).each{item->
				item.delete()
			}
			_session.flush()
		}
	}
	
}
