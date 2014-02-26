package com.rosten.app.util

import java.text.SimpleDateFormat
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SystemUtil {
	ConfigObject configObject = ConfigurationHolder.getConfig()
	
	def getRandName ={name->
		def sdf = new SimpleDateFormat("yyyyMMddHHmmssS")//格式化时间输出
		def rname = sdf.format(new Date())//取得当前时间，Date()是java.util包里的，这作为真实名称
		int i = name.lastIndexOf(".")//原名称里倒数第一个"."在哪里
		def ext = name.substring(i+1)//取得后缀，及"."后面的字符
		return rname+"."+ext//拼凑而成
	}
	
	def getUploadPath={ type ->
		
		if(type==null) type = "upload"
		
		def userPath
		if(configObject.getProperty("rostenFileConfig")){
			def webRootDir =configObject.getProperty("rostenFileConfig").fileUpload;
			userPath = webRootDir +"/" + type
		}else{
			userPath = "rostenFileUpload/" + type
		}
		def userDir = new File(userPath);
		userDir.mkdirs()
		return userPath
	}
	def getSizeLimit = {
		if(configObject.getProperty("rostenFileConfig")){
			return configObject.getProperty("rostenFileConfig").sizeLimit
		}else{
			return false
		}
	}
	def getUploadSize ={
		if(configObject.getProperty("rostenFileConfig")){
			return configObject.getProperty("rostenFileConfig").fileSize
		}else{
			return null
		}
	}
	
}
