package com.rosten.app.sendfile

import com.rosten.app.util.GridUtil

class SendFileService {
	
	def getSendFileLabelListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new SendLable())
	}
	
	def getSendFileLabelListDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllSendFileLabel(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllSendFileLabel ={offset,max,company->
		def c = SendLable.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			
			}
		return c.list(pa,query)
	}
	def getSendFileLabelCount ={company->
		def c = SendLable.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	def getSendFileListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new SendFile())
	}
	def getSendFileListDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllSendFile(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getAllSendFile ={offset,max,company->
		def c = SendFile.createCriteria()
		def pa=[max:max,offset:offset]
		def query = { 
			eq("company",company) 
			
			}
		return c.list(pa,query)
	}
	def getSendFileCount ={company->
		def c = SendFile.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	
    def serviceMethod() {

    }
}
