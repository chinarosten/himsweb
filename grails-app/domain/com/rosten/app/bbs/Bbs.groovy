package com.rosten.app.bbs

import java.util.List;

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.system.Attachment
import java.text.SimpleDateFormat

class Bbs {
	String id

	//紧急层度:普通,紧急,特急
	@GridColumn(name="紧急程度",width="50px",formatter="formatBbsLevel")
	String level = "普通"

	//类别
	@GridColumn(name="类别",width="40px")
	String category = "公告"

	//主题
	@GridColumn(name="主题",formatter="bbs_formatTopic")
	String topic

	//内容
	String content

	//是否置顶
	boolean isTop = false
	
	//是否已发布
	boolean isPublish = false
	
	//发布人
	User publisher
	
	//发布人部门
	String publisherDepart

	//发布时间
	Date publishDate

	def getFormattedPublishDate(){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
		if(publishDate){
			return sd.format(publishDate)
		}else{
			return sd.format(new Date())
		}
	}

	//过期时间
	Date dueDate

	//当前处理人
	User currentUser

	@GridColumn(name="当前处理人",width="60px",colIdx=5)
	def getCurrentUserName(){
		if(currentUser!=null){
			return currentUser.username
		}else{
			return ""
		}
	}

	//当前处理部门
	@GridColumn(name="处理人部门",width="80px",colIdx=6)
	String currentDepart

	//当前处理时间
	Date currentDealDate
	
	//前处理人
	User frontUser
	
	//前处理部门
	String frontDepart
	
	//前处理时间
	Date frontDealDate
	
	//起草人
	User drafter

	@GridColumn(name="拟稿人",width="60px",colIdx=7)
	def getFormattedDrafter(){
		if(drafter!=null){
			return drafter.username
		}else{
			return ""
		}	
	}

	//起草部门
	@GridColumn(name="拟稿部门",width="80px",colIdx=8)
	String drafterDepart

	//创建时间
	Date createDate = new Date()

	@GridColumn(name="拟稿时间",width="150px")
	def getFormattedCreatedDate(){
		if(createDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(createDate)
		}else{
			return ""
		}
	}

	//状态：起草,待发布,已发布,退回
	@GridColumn(name="状态",width="60px",colIdx=4)
	String status = "起草"

	//附件
	Attachment attachment

	//缺省读者；*:允许所有人查看,[角色名称]:允许角色,user:普通人员查看
	String defaultReaders="[公告普通人员],[公告管理员]"
	def addDefaultReader(userRole){
		if(defaultReaders==null || "".equals(defaultReaders)){
			defaultReaders = userRole
		}else{
			defaultReaders += "," + userRole 
		}
	}
	
	//已阅读人员,读者
	static hasMany=[hasReaders:User,readers:User]

	static belongsTo = [company:Company]

	static transients = [
		"formattedCreatedDate",
		"formattedDrafter",
		"currentUserName",
		"formattedPublishDate",
		"addDefaultReader"
	]

	static constraints = {
		publisher nullable:true,blank:true
		publisherDepart nullable:true,blank:true
		publishDate nullable:true,blank:true
		dueDate nullable:true,blank:true
		attachment nullable:true,blank:true
		content nullable:true,blank:true
		frontUser nullable:true,blank:true
		frontDepart nullable:true,blank:true
		frontDealDate nullable:true,blank:true
		content nullable:true,blank:true
		attachment nullable:true,blank:true
		defaultReaders nullable:true,blank:true
	}
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_BBS_BBS"
		content sqlType:"text"
	}
}
