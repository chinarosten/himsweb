package com.rosten.app.bbs

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.util.Util
import com.rosten.app.system.User

class BbsController {
	def springSecurityService
	def bbsService
	
	def bbsSave = {
		def json=[:]
		def bbs = new Bbs()
		if(params.id && !"".equals(params.id)){
			bbs = Bbs.get(params.id)
		}
		bbs.properties = params
		bbs.clearErrors()
		
		def user = springSecurityService.getCurrentUser()
		
		bbs.company = Company.get(params.companyId)
		bbs.currentUser = user
		bbs.currentDepart = user.getDepartName()
		bbs.currentDealDate = new Date()
		bbs.drafter = user
		bbs.drafterDepart = user.getDepartName()
		bbs.addToReaders(user)
		
		if(bbs.save(flush:true)){
			json["result"] = true
			json["id"] = bbs.id
			json["companyId"] = bbs.company.id
		}else{
			bbs.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	def uploadFile ={
		
	}
	def bbsAdd ={
		redirect(action:"bbsShow",params:params)
	}
	def bbsShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def bbs = new Bbs()
		if(params.id){
			bbs = Bbs.get(params.id)
		}
		
		model["bbs"] = bbs
		model["user"]=user
		model["company"] = company
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			fa.readOnly = ["resourceName","url","imgUrl","description"]
		}
		model["fieldAcl"] = fa
		
		render(view:'/bbs/bbs',model:model)
	}
	def bbsGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		def user = User.get(params.userId)
		
		if(params.refreshHeader){
			json["gridHeader"] = bbsService.getBbsListLayout()
		}
		
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			
			def gridData
			if("person".equals(params.type)){
				//个人待办
				args["user"] = user
				gridData = bbsService.getBbsListDataStoreByUser(args)
			}else if("all".equals(params.type)){
				//所有文档
				gridData = bbsService.getBbsListDataStore(args)
			}else if("new".equals(params.type)){
				//最新文档
				args["user"] = user
				gridData = bbsService.getBbsListDataStoreByNew(args)
			}
			
			//处理format中的内容
			gridData.items.each{
				switch(it.level){
				case "普通":
					it.level = ""
					break
				case "紧急":
					it.level = "images/rosten/share/alert_1.gif"
					break
				case "特急":
					it.level = "images/rosten/share/alert_1.gif"
					break
				}
			}
			
			json["gridData"] = gridData
			
		}
		if(params.refreshPageControl){
			
			def total
			if("person".equals(params.type)){
				//个人待办
				total = bbsService.getBbsCountByUser(company,user)
			}else if("all".equals(params.type)){
				//所有文档
				total = bbsService.getBbsCount(company)
			}else if("new".equals(params.type)){
				//最新文档
				total = bbsService.getBbsCountByNew(company,user)
			}
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	def bbsConfig = {
		def model = [:]
		def user = springSecurityService.getCurrentUser()
		
		def bbsConfig = BbsConfig.findWhere(company:user.company)
		if(bbsConfig==null) {
			bbsConfig = new BbsConfig()
			bbsConfig.nowYear = 2014
			bbsConfig.frontYear = bbsConfig.nowYear -1
			
			model.companyId = user.company.id
		}else{
			model.companyId = bbsConfig.company.id
		}
		model.bbsConfig = bbsConfig
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			fa.readOnly = ["nowYear","nowSN","nowCancel","frontYear","frontSN","frontCancel"]
		}else{
//			fa.readOnly = ["nowCancel","frontCancel"]
		}
		model["fieldAcl"] = fa
		
		render(view:'/bbs/bbsConfig',model:model)
	}
	def bbsConfigSave ={
		def json=[:]
		def bbsConfig = new BbsConfig()
		if(params.id && !"".equals(params.id)){
			bbsConfig = BbsConfig.get(params.id)
		}
		bbsConfig.properties = params
		bbsConfig.clearErrors()
		bbsConfig.company = Company.get(params.companyId)
		
		//处理废弃号
		def nowCancel = params.bbsConfig_nowCancel.split(",")
		def frontCancel = params.bbsConfig_frontCancel.split(",")
		
		bbsConfig.nowCancel.removeAll()
		bbsConfig.frontCancel.removeAll()
		
		nowCancel.each{
			bbsConfig.nowCancel << it
		}
		frontCancel.each{
			bbsConfig.frontCancel << it
		}
		
		if(bbsConfig.save(flush:true)){
			json["result"] = true
			json["bbsConfigId"] = bbsConfig.id
			json["companyId"] = bbsConfig.company.id
		}else{
			bbsConfig.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
    def index() { }
}
