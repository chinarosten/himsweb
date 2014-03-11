package com.rosten.app.meeting

import com.rosten.app.util.GridUtil

class MeetingService {
	
	def getMeetingListDataStoreByUser ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllMeetingByUser(offset,max,params.company,params.user)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	private def getAllMeetingByUser={offset,max,company,user->
		def c = Meeting.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			eq("currentUser",user)
			order("createDate", "desc")
		}
		return c.list(pa,query)
	}
	def getMeetingCountByUser ={company,user->
		def c = Meeting.createCriteria()
		def query = {
			eq("company",company)
			eq("currentUser",user)
			order("createDate", "desc")
		}
		return c.count(query)
	}
	
	def getMeetingListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Meeting())
	}
	def getMeetingListDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllMeeting(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getAllMeeting ={offset,max,company->
		def c = Meeting.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			
			}
		return c.list(pa,query)
	}
	def getMeetingCount ={company->
		def c = Meeting.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	
    def serviceMethod() {

    }
}
