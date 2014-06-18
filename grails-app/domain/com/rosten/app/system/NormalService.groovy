package com.rosten.app.system

import com.rosten.app.annotation.GridColumn

class NormalService {
	String id
	
	//服务名称
//	@GridColumn(name="服务名称",colIdx=2,formatter="service_formatTopic")
	@GridColumn(name="服务名称",colIdx=2)
	String serviceName
	
	//归属模块
	Model model
	
	@GridColumn(name="所属模块",colIdx=1)
	def getModelName(){
		if(model){
			return model.modelName
		}
		return ""
	}
	
	//归属人员
	User user
	
	//归属角色
	Role role
	
	//归属群组
	Group1 group
	
	//图标地址
	@GridColumn(name="图标路径",width="300px")
	String imgUrl = "images/rosten/service/rosten.png"
	
	//方法名称
	@GridColumn(name="方法名称")
	String functionName = "excuteService"
	
	//参数
	@GridColumn(name="参数")
	String functionArgs
	
	//clours参数
	String cloursArgs
	
	//跳转地址
	@GridColumn(name="跳转地址")
	String functionUrl
	
	//所属单位
	static belongsTo = [company:Company]
	
    static constraints = {
		model nullable:true,blank:true
		user nullable:true,blank:true
		role nullable:true,blank:true
		group nullable:true,blank:true
		functionName nullable:true,blank:true
		functionArgs nullable:true,blank:true
		functionUrl nullable:true,blank:true
		cloursArgs nullable:true,blank:true
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_NORMALSERVICE"
	}
}
