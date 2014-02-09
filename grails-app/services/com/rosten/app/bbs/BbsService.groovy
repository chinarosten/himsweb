package com.rosten.app.bbs

import com.rosten.app.util.GridUtil

class BbsService {
	
	def getBbsListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Bbs())
	}
	def getBbsListDataStoreByUser ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllBbsByUser(offset,max,params.company,params.user)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	private def getAllBbsByUser={offset,max,company,user->
		def c = Bbs.createCriteria()
		def pa=[max:max,offset:offset]
		def query = { 
			eq("company",company) 
			eq("currentUser",user)
		}
		return c.list(pa,query)
	}
	def getBbsCountByUser ={company,user->
		def c = Bbs.createCriteria()
		def query = { 
			eq("company",company) 
			eq("currentUser",user)
		}
		return c.count(query)
	}
	def getBbsListDataStoreByNew ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllBbsByNew(offset,max,params.company,params.user,params.showDays)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	private def getAllBbsByNew={offset,max,company,user,showDays->
		def c = Bbs.createCriteria()
		def pa=[max:max,offset:offset]
		def now = new Date()
		def query = {
			eq("company",company)
			eq("currentUser",user)
			between("publishDate",now-showDays,now)
		}
		return c.list(pa,query)
	}
	def getBbsCountByNew ={company,user,showDays->
		def c = Bbs.createCriteria()
		def now = new Date()
		def query = {
			eq("company",company)
			eq("currentUser",user)
			between("publishDate",now-showDays,now)
		}
		return c.count(query)
	}
	def getBbsListDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllBbs(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	private def getAllBbs={offset,max,company->
		def c = Bbs.createCriteria()
		def pa=[max:max,offset:offset]
		def query = { eq("company",company) }
		return c.list(pa,query)
	}
	def getBbsCount ={company->
		def c = Bbs.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
    def serviceMethod() {

    }
}
