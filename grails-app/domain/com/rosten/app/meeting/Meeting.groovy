package com.rosten.app.meeting

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.User
import com.rosten.app.system.Company
import com.rosten.app.system.Attachment

import java.text.SimpleDateFormat
import java.util.Date
import java.util.List

class Meeting {
	String id
	
	@GridColumn(name="流水号",width="60px")
	String serialNo
	
	//类别
	@GridColumn(name="类别",width="80px")
	String category = "部门会议"
	
	//标题
	@GridColumn(name="标题")
	String subject
	
	//开始时间
	Date startDate = new Date()
	
	//结束时间
	Date endDate = new Date()
	
	//会议室
	String address
	
	//责任单位
	String dealDepart
	
	//责任人
	User dealUser
	
	//记录填写人
	User recordUser
	
	//主持人
	User presider
	
	//列席人员、参会人员、读者、附件
	static hasMany=[guesters:User,joiner:User,readers:User,attachments:Attachment]
	
	//会议内容
	String content
	
	//备注
	String description
	
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
	@GridColumn(name="拟稿人",colIdx=4)
	User drafter
	
	//拟稿部门
	String drafterDepart
	
	//当前处理人
	@GridColumn(name="处理者",colIdx=5)
	User currentUser
	
	//当前处理人部门
	String currentDepart
	
	//状态
	@GridColumn(name="状态",colIdx=6)
	String status = "拟稿"
	
	//拟稿时间
	Date createDate = new Date()
	
	@GridColumn(name="开始时间",width="150px")
	def getFormattedStartDate(){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
		return sd.format(startDate)
	}
	
	@GridColumn(name="结束时间",width="150px")
	def getFormattedEndDate(){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
		return sd.format(endDate)
	}
	
	@GridColumn(name="拟稿时间",width="150px")
	def getFormattedDate(){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
		return sd.format(createDate)
	}
	
	def getShowDate(String type){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
		if("start".equals(type)){
			return sd.format(startDate)
		}else{
			return sd.format(endDate)
		}
	}
	
	static belongsTo = [company:Company]
	
	static transients = [
		"formattedDate",
		"addDefaultReader"
	]
	
    static constraints = {
		serialNo nullable:true,blank:true
		address nullable:true,blank:true
		dealDepart nullable:true,blank:true
		dealUser nullable:true,blank:true
		recordUser nullable:true,blank:true
		presider nullable:true,blank:true
		content nullable:true,blank:true
		description nullable:true,blank:true
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_MEETING"
		content sqlType:"longtext"
		description sqlType:"longtext"
	}
	def beforeDelete(){
		Meeting.withNewSession{_session->
			MeetingComment.findAllByMeeting(this).each{item->
				item.delete()
			}
			MeetingLog.findAllByMeeting(this).each{item->
				item.delete()
			}
			_session.flush()
		}
	}
}
