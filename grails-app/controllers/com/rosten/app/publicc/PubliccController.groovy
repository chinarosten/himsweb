package com.rosten.app.publicc

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.system.Attachment

class PubliccController {
	
	def downloadFileAdd ={
		redirect(action:"downloadFileShow",params:params)
	}
	
	def downloadFileShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def downloadFile = new DownLoadFile()
		if(params.id){
			downloadFile = DownLoadFile.get(params.id)
		}
		
		if(!downloadFile){
			render '<h2 style="color:red;width:500px;margin:0 auto">此文件已过期或删除，请联系管理员！</h2>'
			return
		}
		
		model["downloadFile"] = downloadFile
		model["user"]=user
		model["company"] = company
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			if(user.equals(downloadFile.currentUser)){
				
			}else{
				
			}
		}
		model["fieldAcl"] = fa
		
		render(view:'/publicc/downloadFile',model:model)
	}
	def downloadFileDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def downloadFile = DownLoadFile.get(it)
				if(downloadFile){
					downloadFile.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def downloadFileSave = {
		def json=[:]
		
		def user = springSecurityService.getCurrentUser()
		
		def downloadFile
		if(params.id && !"".equals(params.id)){
			downloadFile = DownLoadFile.get(params.id)
			downloadFile.properties = params
			downloadFile.clearErrors()
		}else{
			downloadFile = new DownLoadFile()
			downloadFile.properties = params
			downloadFile.clearErrors()
			
			downloadFile.company = Company.get(params.companyId)
		}
		
		if(downloadFile.save(flush:true)){
			json["result"] = true
			json["id"] = downloadFile.id
			json["companyId"] = downloadFile.company.id
			
		}else{
			downloadFile.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	
    def index() { }
}
