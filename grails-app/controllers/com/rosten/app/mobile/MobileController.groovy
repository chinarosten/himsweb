package com.rosten.app.mobile
import com.rosten.app.system.Company
import com.rosten.app.system.Depart
import com.rosten.app.system.User
import grails.converters.JSON

class MobileController {
	
	def getUserInfor ={
		def json=[:]
		def user = User.get(params.id)
		
		json["username"] = user.username
		json["chinaName"] = user.getFormattedName()
		json["depart"] = user.getDepartName()
		json["telephone"] = user.telephone
		json["email"] = user.email
		json["address"] = user.address
		
		render json as JSON
	}
	def getUserMobiles ={
		def json = []
		def company = Company.get(params.company)
		def dataList = Depart.findAllByCompany(company)
		dataList.each{
			def userList = it.getAllUser()
			if(userList.size()>0){
				def smap =[:]
				smap["label"] = it.departName
				smap["header"] = true
				smap["children"] =[]
				
				userList.each{item->
					def _smap =[:]
					
					if(item.telephone){
						_smap["label"] = item.getFormattedName() + "    (" + item.telephone + ")"
					}else{
						_smap["label"] = item.getFormattedName()
					}
					
					_smap["moveTo"] = "#"
					_smap["userId"] = item.id
					
					smap["children"] << _smap
				}
				json << smap
			}
		}
		render json as JSON
	}
    def index() { }
}
