package com.rosten.app.util

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SystemUtil {
	ConfigObject configObject = ConfigurationHolder.getConfig()
	
	def getUploadPath={
		def userDir
		if(configObject.getProperty("rostenFileConfig")){
			def webRootDir =configObject.getProperty("rostenFileConfig").fileUpload;
			userDir = new File(webRootDir, "/upload");
		}else{
			userDir = new File(webRootDir, "rostenFileUpload/upload");
		}
		userDir.mkdirs()
		return userDir
	}
	
	def getUploadSize ={
		if(configObject.getProperty("rostenFileConfig")){
			return configObject.getProperty("rostenFileConfig").fileSize
		}else{
			return null
		}
	}
	
}
