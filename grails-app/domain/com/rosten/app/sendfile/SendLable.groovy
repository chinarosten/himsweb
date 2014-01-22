package com.rosten.app.sendfile

import com.rosten.app.system.Company
import com.rosten.app.annotation.GridColumn

//发文代字
class SendLable {
	
	String id
	
	//代字类型
	@GridColumn(name="类型",width="60px")
	String category
	
	//二级单位名称
	String org2Name
	
	//发文代字
	@GridColumn(name="发文代字",width="60px")
	String subCategory
	
	//今年年代
	@GridColumn(name="今年",width="60px")
	Integer nowYear
	
	//今年文号
	@GridColumn(name="最新号")
	Integer nowSN = 1
	
	//今年保留号或者废号
	List nowCancel
	
	//去年年代
	@GridColumn(name="去年",width="60px")
	Integer frontYear
	
	//去年文号
	@GridColumn(name="最新号")
	Integer frontSN = 1
	
	//去年保留号或者废号
	List frontCancel
	
	static belongsTo = [company:Company]

    static constraints = {
		nowCancel nullable:true,blank:true
		frontCancel nullable:true,blank:true
		org2Name nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_SENDFILE_LABLE"
	}
}
