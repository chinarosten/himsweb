package com.rosten.app.bbs

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.util.Util
import com.rosten.app.system.User
import com.rosten.app.start.StartService
import com.rosten.app.gtask.Gtask
import com.rosten.app.system.Depart

class BbsController {
	def springSecurityService
	def bbsService
	def startService
	
	def publishBbs ={
		
		def user = User.get(params.userId)
		def company = Company.get(params.companyId)
		
		def max = 18
		def offset = 0
		
		def c = Bbs.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			or{
				//defaultReaders为：all或者【角色】或者readers中包含当前用户的均有权访问
				like("defaultReaders", "%all%")
				readers{
					eq("id",user.id)
				}
			}
			eq("status","已发布")
			order("publishDate", "desc")
		}
		
		//取最前面9条数据
		def bbsList = []
		def cResult =c.list(pa,query).unique()
		if(cResult.size() > 9){
			cResult = cResult[0..8]
		}
		cResult.each{
			def smap =[:]
			smap["topic"] = it.topic
			smap["id"] = it.id
			smap["date"] = it.getFormattedPublishDate("datetime")
			
			if(!it.hasReaders.find{item->
				 item.id == user.id 
			}){
				smap["isnew"] = true
			}
			
			bbsList << smap
		}
		render bbsList as JSON
	}
	
	def bbsFlowDeal = {
		def json=[:]
		def bbs = Bbs.get(params.id)
		
		def frontStatus = bbs.status
		
		switch (params.deal){
			case "submit":
				bbs.status="待发布"
				break;
			case "agrain":
				bbs.status = "已发布"
				bbs.publishDate = new Date()
				bbs.addDefaultReader("all")
				break;
			case "notAgrain":
				bbs.status = "不同意"
				break;
		}
		
		if(params.dealUser){
			//下一步相关信息处理
			def dealUsers = params.dealUser.split(",")
			if(dealUsers.size() >1){
				//并发
			}else{
				//串行
				def _user = User.get(Util.strLeft(params.dealUser,":"))
				def args = [:]
				args["type"] = "【公告】"
				args["content"] = "请您审核名称为  &lt;&lt;" + bbs.topic +  "&gt;&gt; 的公告"
				args["contentStatus"] = bbs.status
				args["contentId"] = bbs.id
				args["user"] = _user
				args["company"] = _user.company
				
				startService.addGtask(args)
				
				bbs.currentUser = _user
				def departEntity = Depart.get(Util.strRight(params.dealUser, ":"))
				bbs.currentDepart = departEntity.departName
				bbs.currentDealDate = new Date()
				
				if(!bbs.readers.find{ item-> 
					item.id.equals(_user.id) 
				}){
					bbs.addToReaders(_user)
				}
			}
			
		}
		
		//处理当前人的待办事项
		def currentUser = springSecurityService.getCurrentUser()
		def gtask = Gtask.findWhere(
			user:currentUser,
			company:currentUser.company,
			contentId:bbs.id,
			contentStatus:frontStatus
		)
		if(gtask!=null){
			gtask.dealDate = new Date()
			gtask.status = "1"
			gtask.save()
		}
		
		
		if(bbs.save(flush:true)){
			json["result"] = true
		}else{
			bbs.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	def bbsDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def bbs = Bbs.get(it)
				if(bbs){
					bbs.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
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
		bbs.publishDate = Util.convertToTimestamp(params.publishDate)
		
		if(!bbs.readers.find{ it.id.equals(user.id) }){
			bbs.addToReaders(user)
		}
		
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
	def hasReadBbs ={
		def json=[:]
		def user = User.get(params.userId)
		def bbs = Bbs.get(params.id)
		
		//增加已读标记
		if(!bbs.hasReaders.find{ it.id == user.id }){
			bbs.addToHasReaders(user)
			if(bbs.save(flush:true)){
				json["result"] = true
			}else{
				bbs.errors.each{
					println it
				}
				json["result"] = false
			}
		}else{
			json["result"] = true
		}
		render json as JSON
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
		}else{
			bbs.currentUser = user
		}
		
		model["bbs"] = bbs
		model["user"]=user
		model["company"] = company
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			if(user.equals(bbs.currentUser)){
				switch(bbs.status){
					case "起草":
						break
					case "待发布":
						break
					case "已发布":
						fa.readOnly = ["level","category","publishDate","topic","content","attach"]
						break;
				}
			}else{
				fa.readOnly = ["level","category","publishDate","topic","content","attach"]
			}
		}
		model["fieldAcl"] = fa
		
		render(view:'/bbs/bbs',model:model)
	}
	def bbsGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		def user = User.get(params.userId)
		def bbsConfig = BbsConfig.findByCompany(company)
		
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
				args["showDays"] = bbsConfig.showDays;
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
				total = bbsService.getBbsCountByNew(company,user,bbsConfig.showDays)
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
			
			Calendar cal = Calendar.getInstance();
			bbsConfig.nowYear = cal.get(Calendar.YEAR)
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
		
		bbsConfig.nowCancel = params.bbsConfig_nowCancel
		bbsConfig.frontCancel = params.bbsConfig_frontCancel
		
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
