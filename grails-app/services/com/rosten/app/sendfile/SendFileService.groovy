package com.rosten.app.sendfile

import com.rosten.app.util.GridUtil

class SendFileService {
	//增加流程日志
	def addFlowLog ={ entity,currentUser,content ->
		def logEntity = new SendFileLog()
		logEntity.user = currentUser
		logEntity.sendFile = entity
		logEntity.content = content
		logEntity.save(flush:true)
	}
	def getSendFileCountByUser ={company,user,searchArgs->
		def c = SendFile.createCriteria()
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
	def getSendFileListDataStoreByUser ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllSendFileByUser(offset,max,params.company,params.user,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getAllSendFileByUser ={offset,max,company,user,searchArgs->
		def c = SendFile.createCriteria()
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
	
	def getSendFileLabelListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new SendLable())
	}
	
	def getSendFileLabelListDataStore ={params,searchArgs->
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
	def getSendFileListDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllSendFile(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getAllSendFile ={offset,max,company,searchArgs->
		def c = SendFile.createCriteria()
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
	def getSendFileCount ={company,searchArgs->
		def c = SendFile.createCriteria()
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
