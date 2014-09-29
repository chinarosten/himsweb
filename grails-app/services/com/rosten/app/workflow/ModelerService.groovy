package com.rosten.app.workflow

import com.rosten.app.util.GridUtil
import com.rosten.app.system.Model

class ModelerService {

    def getFlowBusinessListDataStore = {params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllFlowBusiness(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getFlowBusinessListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new FlowBusiness())
	}
	def getAllFlowBusiness={offset,max,company->
		def c = FlowBusiness.createCriteria()
		def pa=[max:max,offset:offset]
		def query = { 
			eq("company",company)
			order("model", "desc")
		}
		return c.list(pa,query)
	}
	def getFlowBusinessCount={company->
		def c = FlowBusiness.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	def getModelById ={id->
		return Model.get(id)
	}
}
