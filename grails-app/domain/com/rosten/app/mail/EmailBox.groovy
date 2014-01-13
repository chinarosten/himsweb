package com.rosten.app.mail

import java.text.SimpleDateFormat
import java.util.Date;
import java.util.List;

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company
import com.rosten.app.system.User

class EmailBox {
	
	String id
	
	@GridColumn(name="发送人",width="100px")
	String sender	//发送人
	
	String senderCode	//发送人编号/邮箱地址
	
	String receiver	//收件人
	
	String receiverCode	//收件人编号/邮箱地址
	
	String copyer	//抄送人
	
	String copyerCode	//抄送人编号/邮箱地址
	
	@GridColumn(name="主题")
	String	subject	//主题
	
	String content	//内容
	
	Integer sendType=0	//发送类型：0 普通件 ; 1 急件
	
	Date sendDate	//发送日期
	
	Date readDate	//读取日期
	
	Integer boxType=0	//邮箱类型：0  草稿箱;1 收件箱;2 发件箱;3已删除;4垃圾邮件
	
	@GridColumn(name="标记",width="30px",colIdx=1)
	Integer emailStatus=0	//邮件类型：0 未读;1 已读;2 回复 ; 3 转发 ; 4 全部转发
	
	static belongsTo = [mailUser:User]
	
	List attachments
	static hasMany=[attachments:Attachments]
	
	//创建日期
	Date createdDate = new Date()
	
	@GridColumn(name="时间",width="150px")
	String sent = getFormattedDate()
	
	def getFormattedDate(){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
		def showDate
		if(boxType==0){
			showDate = createdDate
		}else if(boxType==1){
			showDate = readDate
		}else if(boxType==2){
			showDate = sendDate
		}else if(boxType==3){
			showDate = sendDate || readDate || createdDate
		}
		return sd.format(showDate)
	}
	static transients = ["formattedDate","sent"]
    static constraints = {
		copyer nullable:true,blank:true
		copyerCode nullable:true,blank:true
		content nullable:true,blank:true
		sendDate nullable:true,blank:true
		readDate nullable:true,blank:true
    }
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_EMAIL_BOX"
		content sqlType:"text"
	}
}
