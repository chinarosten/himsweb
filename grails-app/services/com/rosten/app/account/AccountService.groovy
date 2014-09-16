package com.rosten.app.account

import com.rosten.app.util.GridUtil

class AccountService {
	
	def getAccountListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Account())
	}
	def getAccountListDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllAccount(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	private def getAllAccount={offset,max,company,searchArgs->
		def c = Account.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			
			searchArgs.each{k,v->
				eq(k,v)
			}
			
			order("date", "desc")
		}
		return c.list(pa,query)
	}
	def getAccountCount ={company,searchArgs->
		def c = Account.createCriteria()
		def query = {
			eq("company",company)
			
			searchArgs.each{k,v->
				eq(k,v)
			}
			
			order("date", "desc")
		}
		return c.count(query)
	}
	
	def getCategoryListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Category())
	}
	def getCategoryListDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllCategory(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	private def getAllCategory={offset,max,company,searchArgs->
		def c = Category.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			
			searchArgs.each{k,v->
				like(k,"%" + v + "%")
			}
			
			order("name", "desc")
		}
		return c.list(pa,query)
	}
	def getCategoryCount ={company,searchArgs->
		def c = Category.createCriteria()
		def query = {
			eq("company",company)
			
			searchArgs.each{k,v->
				like(k,"%" + v + "%")
			}
			
			order("name", "desc")
		}
		return c.count(query)
	}
	
    def getProjectListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Project())
	}
	def getProjectListDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllProject(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	private def getAllProject={offset,max,company,searchArgs->
		def c = Project.createCriteria()
		def pa=[max:max,offset:offset]
		def query = { 
			eq("company",company) 
			
			searchArgs.each{k,v->
				like(k,"%" + v + "%")
			}
			
			order("name", "desc")
		}
		return c.list(pa,query)
	}
	def getProjectCount ={company,searchArgs->
		def c = Project.createCriteria()
		def query = { 
			eq("company",company) 
			
			searchArgs.each{k,v->
				like(k,"%" + v + "%")
			}
			
			order("name", "desc")
		}
		return c.count(query)
	}
}
