package com.rosten.app.start

import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.gtask.Gtask
import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.util.Util

class StartController {
	def startService
	
	def gtaskDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def gtask = Gtask.get(it)
				if(gtask){
					gtask.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def gtaskGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		def user = User.get(params.userid)
		
		if(params.refreshHeader){
			json["gridHeader"] = startService.getGtaskListLayout()
		}
		
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			args["user"] = user
			
			def gridData =  startService.getGtaskListDataStore(args)
			json["gridData"] = gridData
			
		}
		if(params.refreshPageControl){
			def total = startService.getGtaskCount(company,user)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	def getGtask ={
		def user = User.get(params.userId)
		def company = Company.get(params.companyId)
		
		def max = 4
		def offset = 0
		
		def c = Gtask.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			eq("user",user)
			eq("status","0")
		}
		
		def _list = []
		c.list(pa,query).unique().each{
			def smap =[:]
			smap["type"] = it.type
			smap["id"] = it.contentId
			smap["content"] = it.content
			smap["date"] = it.getFormattedCreatedDate()
			
			_list << smap
		}
		render _list as JSON
	}
    def index() { }
}
