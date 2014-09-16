package com.rosten.app.account

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company
import com.rosten.app.system.User
import java.text.SimpleDateFormat

class Account {
	String id
	
	//添加时间
	Date date = new Date()
	@GridColumn(name="日期",width="106px",colIdx=1)
	def getFormattedDate(){
		if(date!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(date)
		}else{
			return ""
		}
	}
	
	//类型：支出、收入
	@GridColumn(name="类型",width="50px",colIdx=2)
	String purpose = "支出"
	
	//项目名称
	@GridColumn(name="项目名称",colIdx=3)
	def getProjectName(){
		return project?project.name:""
	}
	
	//用途
	Category category
	@GridColumn(name="用途",colIdx=4)
	def getCategoryName(){
		return category?category.name:""
	}
	
	//金额
	@GridColumn(name="金额",colIdx=5,formatter="account_formatTitle")
	int money
	
	//添加人
	User user
	@GridColumn(name="添加人",colIdx=6)
	def getUserName(){
		if(user!=null){
			return user.getFormattedName()
		}else{
			return ""
		}
	}
	
	//备注
	@GridColumn(name="备注",colIdx=7)
	String remark
	
	static belongsTo = [company:Company,project:Project]
	
    static constraints = {
		remark nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_ACCOUNT_ACCOUNT"
		remark sqlType:"longtext"
	}
}
