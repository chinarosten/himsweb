package com.rosten.app.util

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SystemUtil {
	ConfigObject configObject = ConfigurationHolder.getConfig()
	
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
