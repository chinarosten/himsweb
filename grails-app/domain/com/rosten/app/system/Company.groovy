package com.rosten.app.system

import java.util.Date;

import com.rosten.app.annotation.GridColumn

class Company {
	String id

	//公司名称
	@GridColumn(name="公司名称")
	String companyName

	//简称
	@GridColumn(name="简称")
	String shortName

	//手机号码
	String companyMobile

	//电话号码
	String companyPhone

	//传真号码
	String companyFax

	//公司地址
	@GridColumn(name="公司地址")
	String companyAddress

	//是否启用
	boolean isTurnon = false

	@GridColumn(name="是否启用")
	def getIsTurnonValue(){
		if(isTurnon)return "是"
		else return "否"
	}

	//是否短信开通
	boolean isSmsOn	= false

	@GridColumn(name="是否短信开通")
	def getIsSmsOnValue(){
		if(isSmsOn)return "是"
		else return "否"
	}
	
	//外网是否发送邮件
	boolean isOnMail = true
	@GridColumn(name="外网邮箱")
	def getIsOnMailValue(){
		if(isOnMail)return "是"
		else return "否"
	}

	//内容描述
	String description

	//创建日期
	Date createdDate = new Date()

	def getAllModel(){
		return Model.findAllByCompany(this)
	}
	def getAllResource(){
		def _array = []
		getAllModel().each{ _array += it.resources }
		return _array
	}

	List departs,groups,permissions,userTypes
	static hasMany=[departs:Depart,groups:Group1,permissions:Permission,userTypes:UserType]

	static transients = [
		"isTurnonValue",
		"isSmsOnValue",
		"allModel",
		"allResource"
	]
	static constraints = {
		companyName nullable:false,blank:false,unique: true
		shortName nullable:false,blank:false,unique: true
		companyMobile nullable:true,blank:true
		companyPhone nullable:true,blank:true
		companyFax nullable:true,blank:true
		companyAddress nullable:true,blank:true
		description nullable:true,blank:true
		isOnMail nullable:true,blank:true
	}
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		description sqlType:"text"
		table "ROSTEN_COMPANY"
	}
	def beforeDelete(){
		Company.withNewSession{
			//主要针对超级管理员情况,company允许为空-----------
			User.findAllByCompany(this).each{user->
				user.delete()
			}
			Role.findAllByCompany(this).each{role->
				role.delete()
			}
			Model.findAllByCompany(this).each{model->
				model.delete()
			}
			Question.findAllByCompany(this).each{item->
				item.delete()
			}
			Sms.findAllByCompany(this).each{item->
				item.delete()
			}
			//------------------------------------------
			LogoSet.findAllByCompany(this).each{logoset->
				logoset.delete(flush:true)
			}
		}
	}
}
