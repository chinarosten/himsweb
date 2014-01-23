package com.rosten.app.sendfile

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.User
import com.rosten.app.system.Company
import com.rosten.app.system.Attachment

import java.text.SimpleDateFormat
import java.util.Date
import java.util.List;

class SendFile {
	
	String id
	
	//发文流水号
	@GridColumn(name="流水号",width="60px")
	String serialNo
	
	//成文日期
	@GridColumn(name="成文日期")
	Date fileDate
	
	//文件编号
	@GridColumn(name="文件编号")
	String fileNo
	
	//文件种类
	SendLable fileType 
	
	//文件题名
	@GridColumn(name="文件题名")
	String title
	
	//主题词
	String subject
	
	//主送单位
	String mainSend
	
	//抄送单位
	String copyTo
	
	//内部抄送
	String insideCopy
	
	//缓急
	String emergency
	
	//印发纷数
	Integer printCopy
	
	//档案种类
	String archiveType
	
	//归档到
	String archiveDbName
	
	//份数
	Integer copys
	
	//页数
	Integer pages
	
	//密级
	String secretLevel
	
	//期限
	String term
	
	//类目号
	String archiveCategory
	
	//相关号
	String archiveNo
	
	//相关卷
	String referrenceNo
	
	//全宗号
	String fonds
	
	//卷号
	String volumnNo
	
	//件号
	String unitNo
	
	//附件
	List attachments
	static hasMany=[attachments:Attachment]
	
	//缺省读者，读者
	List defaultReaders,readers
	
	//拟稿人
	@GridColumn(name="拟稿人",colIdx=7)
	User drafter
	
	//当前处理人
	@GridColumn(name="处理者",colIdx=6)
	User currentUser
	
	//状态
	@GridColumn(name="状态",colIdx=5)
	String status
	
	//拟稿时间
	@GridColumn(name="拟稿时间",width="150px")
	Date createDate = new Date()
	
	def getFormattedDate(){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
		return sd.format(createDate)
	}
	
	static transients = [
		"formattedDate"
	]
	
	static belongsTo = [company:Company]
	
    static constraints = {
		fileDate nullable:true,blank:true
		fileNo nullable:true,blank:true
		subject nullable:true,blank:true
		mainSend nullable:true,blank:true
		copyTo nullable:true,blank:true
		insideCopy nullable:true,blank:true
		emergency nullable:true,blank:true
		printCopy nullable:true,blank:true
		archiveType nullable:true,blank:true
		archiveDbName nullable:true,blank:true
		copys nullable:true,blank:true
		pages nullable:true,blank:true
		secretLevel nullable:true,blank:true
		term nullable:true,blank:true
		archiveCategory nullable:true,blank:true
		archiveNo nullable:true,blank:true
		referrenceNo nullable:true,blank:true
		fonds nullable:true,blank:true
		volumnNo nullable:true,blank:true
		unitNo nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_SENDFILE_SENDFILE"
	}
}