package com.rosten.app.system

import java.util.Date;

import com.rosten.app.annotation.GridColumn

class Group1 {
	String id
	
	//群组名称
	@GridColumn(name="群组名称",colIdx=1,formatter="group_formatTopic")
	String groupName
	
	//人员名称
	@GridColumn(name="人员名称",colIdx=2)
	def getAllUsers(){
		def _array = []
		UserGroup.findAllByGroup(this).each{
			_array << it.user.getFormattedName()
		}
		return _array.join(",")
	}
	
	//具有角色
	@GridColumn(name="具有角色",colIdx=3)
	def getAllRoles(){
		def _array = []
		RoleGroup.findAllByGroup(this).each{
			_array << it.role.authority
		}
		return _array.join(",")
	}
		
    //描述
	@GridColumn(name="描述",colIdx=4)
	String description
	
	//创建日期
	Date createdDate = new Date()
	
	static belongsTo = [company:Company]
	
    static constraints = {
		groupName blank: false
		description nullable:true,blank:true
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		description sqlType:"text"
		table "ROSTEN_GROUP"
	}
	def beforeDelete(){
		RoleGroup.removeAll(this)
		ModelGroup.removeAll(this)
		UserGroup.removeAll(this)
	}
}
