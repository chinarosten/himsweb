package com.rosten.app.sendfile

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.util.Util
import com.rosten.app.system.Company
import com.rosten.app.system.User


class SendFileController {
	
	def springSecurityService
	def sendFileService
	
	
	

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
