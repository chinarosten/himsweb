package com.rosten.app.sendfile

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.util.Util
import com.rosten.app.system.Company
import com.rosten.app.system.User


class SendFileController {
	
	def springSecurityService
	def sendFileService
	
	
	def sendFileLabelGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = sendFileService.getSendFileLabelListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = sendFileService.getSendFileLabelListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = sendFileService.getSendFileLabelCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def addWord = {
		def model =[:]
		render(view:'/sendFile/word',model:model)
	}
	
	def sendFileAdd ={
		redirect(action:"sendFileShow",params:params)
	}
	def sendFileShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def sendFile = new SendFile()
		if(params.id){
			sendFile = SendFile.get(params.id)
		}
		model["user"]=user
		model["company"] = company
		model["sendFile"] = sendFile
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			fa.readOnly = []
		}
		model["fieldAcl"] = fa
		
		render(view:'/sendFile/sendFile',model:model)
	}

	def sendFileGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = sendFileService.getSendFileListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = sendFileService.getSendFileListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = sendFileService.getSendFileCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	def index() {
	}
}
