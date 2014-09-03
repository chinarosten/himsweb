package com.rosten.app.system

import java.util.Date;

import com.rosten.app.annotation.GridColumn

class Permission {
	String id
	
	//权限名称
	@GridColumn(name="权限名称",colIdx=1)
	String permissionName
	
	//操作类型集合
	//@GridColumn(name="操作类型集合")
	String setOperation
	
	//具有资源
	@GridColumn(name="具有资源",colIdx=2)
	def getAllResourcesShow(){
		def _array = this.getAllResources().collect{item->
			item.model.modelName
		}
		return _array.unique().join(",")
	}
	//是否匿名访问
	@GridColumn(name="是否匿名访问",colIdx=3)
	def getIsAnonymousValue(){
		if(isAnonymous)return "是"
		else return "否"
	}
	boolean isAnonymous = false
	
	//描述
	@GridColumn(name="描述",colIdx=4)
	String description
	
	def getAllResources(){
		def _array=[]
		PermissionResource.findAllByPermission(this).each{
			_array << it.resource
		}
		return _array.unique()
	}
	def getAllResourcesByModel(model){
		def _array=[]
		PermissionResource.findAllByPermission(this).each{
			if(it.resource.model.id.equals(model.id)){
				_array << it.resource
			}
		}
		return _array.unique()
	}
	
	//所属单位
	static belongsTo = [company:Company]
	
	//创建日期
	Date createdDate = new Date()
	
	static transients = ["isAnonymousValue","allResources","allResourcesByModel"]
	
    static constraints = {
		permissionName blank: false
		setOperation nullable:true,blank:true
		description nullable:true,blank:true
	}
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		description sqlType:"text"
		table "ROSTEN_PERMISSION"
	}
	def beforeDelete(){
		PermissionResource.removeAll(this)
		RolePermission.removeAll(this)
	}
}
