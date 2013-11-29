package com.rosten.app.system

import java.util.Date;
import com.rosten.app.annotation.GridColumn

class Model {
	String id
	
	//模块名称
	@GridColumn(name="模块名称")
	String modelName
	
	//模块链接
	@GridColumn(name="模块链接")
	String modelUrl
	
	//描述
	@GridColumn(name="描述")
	String description
	
	//创建日期
	Date createdDate = new Date()
	
	List resources
	static hasMany=[resources:Resource]
	
	static belongsTo = [company:Company]
	
    static constraints = {
		modelName blank: false
		company nullable:true,blank:true
		modelUrl nullable:true,blank:true
		description nullable:true,blank:true
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		description sqlType:"text"
		table "ROSTEN_MODEL"
	}
	def beforeDelete(){
		ModelUser.removeAll(this)
		ModelDepart.removeAll(this)
		ModelGroup.removeAll(this)
		LogoSet.findAllByModel(this).each{logoset->
			logoset.model = null
			logoset.save(flush:true)
		}
	}
}
