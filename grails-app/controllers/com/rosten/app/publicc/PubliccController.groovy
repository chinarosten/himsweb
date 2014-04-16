package com.rosten.app.publicc

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.system.Attachment
import com.rosten.app.util.Util

class PubliccController {
	def publiccService
	
	def downloadFileGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		def user = User.get(params.userId)
		
		if(params.refreshHeader){
			def layout = publiccService.getDownloadFileListLayout()
			json["gridHeader"] = layout
		}
		
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			
			def gridData = publiccService.getDownloadFileListDataStore(args)
			json["gridData"] = gridData
			
		}
		if(params.refreshPageControl){
			def total = publiccService.getDownloadFileCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
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
