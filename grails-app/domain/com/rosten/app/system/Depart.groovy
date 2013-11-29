package com.rosten.app.system

import com.rosten.app.annotation.GridColumn
import java.util.Date;

class Depart {
	
	String id
	
	//部门名称
	@GridColumn(name="部门名称")
	String departName
	
	//部门电话
	@GridColumn(name="部门电话")
	String departPhone
	
	//部门传真
	String departFax
	
	//部门手机
	String departMobile
	
	//部门地址
	String departAdderess
	
	//内容描述
	@GridColumn(name="描述")
	String description
	
	//创建日期
	Date createdDate = new Date()
	
	List children
	
	//上级部门
	Depart parent
	
	//获取所有用户
	def getAllUser(){
		def users =[]
		UserDepart.findAllByDepart(this).each{
			users << it.user
		}
		return users
	}
	
	//所属单位
	static belongsTo = [company:Company]
	
	static hasMany = [children:Depart]
	
	static transients = ["allUser"]
	
    static constraints = {
		departName blank:false
		departPhone nullable:true,blank:true
		departFax nullable:true,blank:true
		departMobile nullable:true,blank:true
		departAdderess nullable:true,blank:true
		description nullable:true,blank:true
		parent nullable:true
		children(nullable:true)
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		description sqlType:"text"
		table "ROSTEN_DEPART"
	}
	def beforeDelete(){
		ModelDepart.removeAll(this)
		UserDepart.removeAll(this)
	}
}
