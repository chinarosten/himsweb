package com.rosten.app.dsj

import com.rosten.app.util.GridUtil

class DsjService {
	
	def getDsjListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Dsj())
	}
	def getDsjListDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllDsj(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getAllDsj ={offset,max,company->
		def c = Dsj.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			
			}
		return c.list(pa,query)
	}
	def getDsjCount ={company->
		def c = Dsj.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	
    def serviceMethod() {

    }
}
