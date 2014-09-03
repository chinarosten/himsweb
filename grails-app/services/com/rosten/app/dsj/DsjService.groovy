package com.rosten.app.dsj

import com.rosten.app.util.GridUtil

class DsjService {
	
	//增加流程日志
	def addFlowLog ={ entity,currentUser,content ->
		def dsjLog = new DsjLog()
		dsjLog.user = currentUser
		dsjLog.dsj = entity
		dsjLog.content = content
		dsjLog.save(flush:true)
	}
	
	def getDsjCountByUser ={company,user,searchArgs->
		def c = Dsj.createCriteria()
		def query = {
			eq("company",company)
			eq("currentUser",user)
			order("createDate", "desc")
			
			searchArgs.each{k,v->
				like(k,"%" + v + "%")
			}
			
		}
		return c.count(query)
	}
	def getDsjListDataStoreByUser ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllDsjByUser(offset,max,params.company,params.user,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getAllDsjByUser ={offset,max,company,user,searchArgs->
		def c = Dsj.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			eq("currentUser",user)
			order("createDate", "desc")
			
			searchArgs.each{k,v->
				like(k,"%" + v + "%")
			}
		}
		return c.list(pa,query)
	}
	
	def getDsjListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Dsj())
	}
	def getDsjListDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllDsj(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getAllDsj ={offset,max,company,searchArgs->
		def c = Dsj.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			order("createDate", "desc")
			
			searchArgs.each{k,v->
				like(k,"%" + v + "%")
			}
		}
		return c.list(pa,query)
	}
	def getDsjCount ={company,searchArgs->
		def c = Dsj.createCriteria()
		def query = { 
			eq("company",company) 
			
			searchArgs.each{k,v->
				like(k,"%" + v + "%")
			}
			
		}
		return c.count(query)
	}
	
    def serviceMethod() {

    }
}
