package com.rosten.app.workflow

import com.rosten.app.system.Company
import com.rosten.app.system.Model
import com.rosten.app.annotation.GridColumn

class FlowBusiness {
	String id
	
	//所属模块
	Model model
	
	@GridColumn(name="模块名称",colIdx=1)
	def getModelName(){
		return model?model.modelName:"无模块"
	}
	
	//流程代码
	@GridColumn(name="业务流程代码",colIdx=2,formatter="flowBusiness_formatTopic")
	String flowCode
	
	//流程名称
	@GridColumn(name="业务流程名称",colIdx=3)
	String flowName
	
	//关联流程
	String relationFlow
	
	//模块关联流程名称
	@GridColumn(name="关联流程",colIdx=4)
	String relationFlowName
	
	//描述
	@GridColumn(name="描述")
	String description
	
	static belongsTo = [company:Company]
	
    static constraints = {
		model nullable:true,blank:true
		description nullable:true,blank:true
		flowName nullable:true,blank:true
		relationFlow nullable:true,blank:true
		relationFlowName nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_FLOWBUSINESS"
		description sqlType:"text"
	}
}
