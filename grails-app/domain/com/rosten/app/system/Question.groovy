package com.rosten.app.system

import java.text.SimpleDateFormat
import java.util.Date

import com.rosten.app.annotation.GridColumn

class Question {
	String id
	
	//提问人名称
	@GridColumn(name="提问人名称")
	String username
	
	//问题标题
	@GridColumn(name="标题")
	String questionTitle
	
	//问题内容
	@GridColumn(name="内容")
	String question
	
	//是否解答
	boolean isAnswer = false
	
	@GridColumn(name="是否解答")
	def getIsAnswerValue(){
		if(isAnswer)return "是"
		else return "否"
	}
	
	//解答
	String questionAnswer
	
	//创建日期
	Date createdDate = new Date()
	
	@GridColumn(name="提问时间")
	def getFormattedCreateDate(){
		if(createdDate){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(createdDate)
		}else{
			return ""
		}
	}
	
	static transients = ["isAnswerValue","formattedCreateDate"]
	
	//所属单位
	static belongsTo = [company:Company]
	
    static constraints = {
		question nullable:true,blank:true
		questionAnswer nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		question sqlType:"text"
		questionAnswer sqlType:"text"
		table "ROSTEN_QUESTION"
	}
}
