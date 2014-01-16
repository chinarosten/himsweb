package com.rosten.app.util

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SystemUtil {
	ConfigObject configObject = ConfigurationHolder.getConfig()
	
	def getUploadPath={
		def userPath
		if(configObject.getProperty("rostenFileConfig")){
			def webRootDir =configObject.getProperty("rostenFileConfig").fileUpload;
			userPath = webRootDir +"/upload";
		}else{
			userPath = "rostenFileUpload/upload"
		}
		def userDir = new File(userPath);
		userDir.mkdirs()
		return userPath
	}
	
	def getUploadSize ={
		if(configObject.getProperty("rostenFileConfig")){
			return configObject.getProperty("rostenFileConfig").fileSize
		}else{
			return null
		}
	}
	
}
