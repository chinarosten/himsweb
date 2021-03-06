package com.rosten.app.meeting

import com.rosten.app.util.GridUtil

class MeetingService {
	//增加流程日志
	def addFlowLog ={ entity,currentUser,content ->
		def logEntity = new MeetingLog()
		logEntity.user = currentUser
		logEntity.meeting = entity
		logEntity.content = content
		logEntity.save(flush:true)
	}
	def getMeetingListDataStoreByUser ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllMeetingByUser(offset,max,params.company,params.user,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	private def getAllMeetingByUser={offset,max,company,user,searchArgs->
		def c = Meeting.createCriteria()
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
	def getMeetingCountByUser ={company,user,searchArgs->
		def c = Meeting.createCriteria()
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
	
	def getMeetingListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Meeting())
	}
	def getMeetingListDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllMeeting(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getAllMeeting ={offset,max,company,searchArgs->
		def c = Meeting.createCriteria()
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
	def getMeetingCount ={company,searchArgs->
		def c = Meeting.createCriteria()
		def query = { 
			eq("company",company)
			order("createDate", "desc")
			
			searchArgs.each{k,v->
				like(k,"%" + v + "%")
			}
		}
		return c.count(query)
	}
	
    def serviceMethod() {

    }
}
