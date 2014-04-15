package com.rosten.app.system

import com.rosten.app.annotation.GridColumn
import java.text.SimpleDateFormat

class DownLoadFile {
	String id
	
	//标题
	String subject
	
	//发布人
	User publisher
	
	//发布时间
	Date publishDate = new Date()
	
	@GridColumn(name="拟稿时间",width="106px")
	def getFormattedPublishDate(){
		if(publishDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(publishDate)
		}else{
			return ""
		}
	}
	
	//描述
	String description
	
	//附件
	Attachment attachment
	
	static belongsTo = [company:Company]
	
    static constraints = {
		attachment nullable:true,blank:true
		description nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_DOWNLOADFILE"
		description sqlType:"longtext"
	}
}
