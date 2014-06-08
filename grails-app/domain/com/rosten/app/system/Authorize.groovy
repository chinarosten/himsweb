package com.rosten.app.system

import java.util.Date
import com.rosten.app.annotation.GridColumn
import java.text.SimpleDateFormat

class Authorize {
	String id
	
	//授权人
	User authorizer
	
	@GridColumn(name="授权人",colIdx=1)
	def getFormattedAuthorizer(){
		return authorizer.getFormattedName()
	}
	
	//被授权人
	User beAuthorizer
	
	@GridColumn(name="被授权人",colIdx=2)
	def getFormattedBeAuthorizer(){
		return beAuthorizer.getFormattedName()
	}
	
	//授权开始时间
	Date startDate
	
	@GridColumn(name="开始时间",width="106px",colIdx=3)
	def getFormattedStartDate(){
		if(startDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(startDate)
		}else{
			return ""
		}
	}
	
	//授权结束时间
	Date endDate
	
	@GridColumn(name="结束时间",width="106px",colIdx=4)
	def getFormattedEndDate(){
		if(endDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(endDate)
		}else{
			return ""
		}
	}
	
	//授权模块
	static hasMany=[authModels:Model]
	
	//状态
	@GridColumn(name="授权状态",colIdx=5)
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
