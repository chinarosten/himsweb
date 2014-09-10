package com.rosten.app.system
import com.rosten.app.annotation.GridColumn
class Role {
	
	String id
	//角色名称
	@GridColumn(name="角色名称",formatter="role_formatTopic")
	String authority
	
	//具有权限
	@GridColumn(name="具有权限",colIdx=2)
	def getAllPermissionsValue(){
		def _array =[]
		getAllPermissions().each{
			_array << it.permissionName
		}
		return _array.join(",")
	}
	
	def getAllPermissions(){
		def _permissions = []
		RolePermission.findAllByRole(this).each{
			_permissions << it.permission
		}
		return _permissions
	}
	
	//描述
	@GridColumn(name="描述")
	String description
	
	//上级角色
	Role parent
	
	//所属单位
	static belongsTo = [company:Company]
	
	//创建日期
	Date createdDate = new Date()
	
	List children
	
	static hasMany = [children:Role]
	
	static transients = ["allRermissions","allPermissionsValue"]
	
	static mapping = {
		cache true
		id generator:'uuid.hex',params:[separator:'-']
		description sqlType:"text"
		table "ROSTEN_ROLE"
	}

	static constraints = {
		authority blank: false, unique: true
		company nullable:true,blank:true
		parent nullable:true,blank:true
		description nullable:true,blank:true
	}
	def beforeDelete(){
		RoleGroup.removeAll(this)
		UserRole.removeAll(this)
		RolePermission.removeAll(this)
	}
}
