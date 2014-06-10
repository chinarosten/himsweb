package com.rosten.app.system

import java.util.Date

import com.rosten.app.annotation.GridColumn

import java.text.SimpleDateFormat

class Authorize {
	String id
	
	//授权信息
	@GridColumn(name="授权信息",colIdx=1,formatter="authorize_formatTopic")
	String authorizeInfor
	
	//授权人
	User authorizer
	
	@GridColumn(name="授权人",width="60px",colIdx=2)
	def getFormattedAuthorizer(){
		if(authorizer){
			return authorizer.getFormattedName()
		}else{
			return ""
		}
	}
	
	//授权人部门
	String authorizerDepart
	
	//被授权人
	User beAuthorizer
	
	@GridColumn(name="被授权人",width="60px",colIdx=3)
	def getFormattedBeAuthorizer(){
		if(beAuthorizer){
			return beAuthorizer.getFormattedName()
		}else{
			return ""
		}
	}
	
	//被授权人部门
	String beAuthorizerDepart
	
	//授权开始时间
	Date startDate = new Date()
	
	@GridColumn(name="开始时间",width="106px",colIdx=4)
	def getFormattedStartDate(){
		if(startDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(startDate)
		}else{
			return ""
		}
	}
	
	//授权结束时间
	Date endDate = new Date()
	
	@GridColumn(name="结束时间",width="106px",colIdx=5)
	def getFormattedEndDate(){
		if(endDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(endDate)
		}else{
			return ""
		}
	}
	
	def getShowDate(String type){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
		if("start".equals(type)){
			return sd.format(startDate)
		}else{
			return sd.format(endDate)
		}
	}
	
	//授权模块
	static hasMany=[authModels:Model]
	
	//状态
	@GridColumn(name="授权状态",width="60px",colIdx=6)
	String status = "正常"
	
	//创建日期
	Date createdDate = new Date()
	
	static belongsTo = [company:Company]
	
    static constraints = {
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_AUTHORIZE"
	}
}
