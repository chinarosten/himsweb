package com.rosten.app.account

import grails.converters.JSON
import com.rosten.app.util.Util
import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User

class AccountController {
	def springSecurityService
	def accountService
	
	
	def searchView ={
		def model =[:]
		def company = Company.get(params.companyId)
		
		model["projectList"] = Project.findAllByCompany(company)
		model["cagetoryList"] = Category.findAllByCompany(company)
		render(view:'/account/search',model:model)
	}
	def accountAdd ={
		redirect(action:"accountShow",params:params)
	}
	def accountShow ={
		def model =[:]
		def company = Company.get(params.companyId)
		def user = springSecurityService.getCurrentUser()
		
		
		if(params.id){
			model["account"] = Account.get(params.id)
		}else{
			model["account"] = new Account(user:user)
			
		}
		model["company"] = company
		model["projectList"] = Project.findAllByCompany(company)
		model["cagetoryList"] = Category.findAllByCompany(company)
		
		model["user"] = user
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		render(view:'/account/account',model:model)
	}
	def accountSave ={
		def model=[:]
		def account = new Account()
		if(params.id && !"".equals(params.id)){
			account = Account.get(params.id)
		}else{
			account.company = Company.get(params.companyId)
			account.user = springSecurityService.getCurrentUser()
		}
		
		account.properties = params
		account.clearErrors()
		
		account.date = Util.convertToTimestamp(params.date)
		
		if(params.project){
			def project = Project.findByName(params.project)
			if(project){
				account.project = project
			}
		}
		
		if(params.category){
			def category = Category.findByName(params.category)
			if(category){
				account.category = category
			}
		}
		
		if(account.save(flush:true)){
			model["result"] = "true"
		}else{
			account.errors.each{
				println it
			}
			model["result"] = "false"
		}
		render model as JSON
	}
	def accountDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def account = Account.get(it)
				if(account){
					account.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def accountGrid ={
		def model=[:]
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		//2014-9-1 增加搜索功能
		def searchArgs =[:]
		
		if(params.purpose && !"".equals(params.purpose)) searchArgs["purpose"] = params.purpose
		if(params.project && !"".equals(params.project)){
			 def _project = Project.findByCompanyAndName(company,params.project)
			 if(_project){
				 searchArgs["project"] = _project
			 }
		}
		if(params.category && !"".equals(params.category)){
			def _category = Category.findByCompanyAndName(company,params.category)
			if(_category){
				searchArgs["category"] = _category
			}
		}	
		if(params.refreshHeader){
			model["gridHeader"] = accountService.getAccountListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			model["gridData"] = accountService.getAccountListDataStore(args,searchArgs)
			
		}
		if(params.refreshPageControl){
			def total = accountService.getAccountCount(company,searchArgs)
			model["pageControl"] = ["total":total.toString()]
		}
		render model as JSON
	}
	
	def categoryAdd ={
		redirect(action:"categoryShow",params:params)
	}
	def categoryShow ={
		def model =[:]
		def company = Company.get(params.companyId)
		
		if(params.id){
			model["category"] = Category.get(params.id)
		}else{
			model["category"] = new Category()
		}
		model["company"] = company
		
		
		model["projectNameList"] = Project.findAllByCompany(company)
		
		model["user"] = springSecurityService.getCurrentUser()
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		render(view:'/account/category',model:model)
	}
	def categorySave ={
		def model=[:]
		def category = new Category()
		if(params.id && !"".equals(params.id)){
			category = Category.get(params.id)
		}else{
			category.company = Company.get(params.companyId)
		}
		
		category.properties = params
		category.clearErrors()
		
		if(params.projectName){
			def project = Project.findByName(params.projectName)
			if(project){
				category.project = project
			}
		}
		
		
		if(category.save(flush:true)){
			model["result"] = "true"
		}else{
			category.errors.each{
				println it
			}
			model["result"] = "false"
		}
		render model as JSON
	}
	def categoryDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def category = Category.get(it)
				if(category){
					category.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def categoryGrid ={
		def model=[:]
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		//2014-9-1 增加搜索功能
		def searchArgs =[:]
		
		if(params.refreshHeader){
			model["gridHeader"] = accountService.getCategoryListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			model["gridData"] = accountService.getCategoryListDataStore(args,searchArgs)
			
		}
		if(params.refreshPageControl){
			def total = accountService.getCategoryCount(company,searchArgs)
			model["pageControl"] = ["total":total.toString()]
		}
		render model as JSON
	}
	
	def projectAdd ={
		redirect(action:"projectShow",params:params)
	}
	def projectShow ={
		def model =[:]
		
		if(params.id){
			model["project"] = Project.get(params.id)
		}else{
			model["project"] = new Project()
		}
		model["company"] = Company.get(params.companyId)
		
		model["user"] = springSecurityService.getCurrentUser()
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		render(view:'/account/project',model:model)
	}
	def projectSave ={
		def model=[:]
		def project = new Project()
		if(params.id && !"".equals(params.id)){
			project = project.get(params.id)
		}else{
			project.company = Company.get(params.companyId)
		}
		
		project.properties = params
		project.clearErrors()
		
		if(project.save(flush:true)){
			model["result"] = "true"
		}else{
			project.errors.each{
				println it
			}
			model["result"] = "false"
		}
		render model as JSON
	}
	def projectDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def project = Project.get(it)
				if(project){
					project.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def projectGrid ={
		def model=[:]
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		//2014-9-1 增加搜索功能
		def searchArgs =[:]
		
		if(params.refreshHeader){
			model["gridHeader"] = accountService.getProjectListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			model["gridData"] = accountService.getProjectListDataStore(args,searchArgs)
			
		}
		if(params.refreshPageControl){
			def total = accountService.getProjectCount(company,searchArgs)
			model["pageControl"] = ["total":total.toString()]
		}
		render model as JSON
	}
    def index() { }
}
