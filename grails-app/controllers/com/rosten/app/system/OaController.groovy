package com.rosten.app.system

import grails.converters.JSON
import com.rosten.app.mail.EmailBox
import com.rosten.app.mail.Contact
import com.rosten.app.mail.ContactGroup
import com.rosten.app.gtask.Gtask
import com.rosten.app.bbs.Bbs

class OaController {
	
	def userDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def user = User.get(it)
				if(user){
					//删除邮箱模块相关信息
					EmailBox.findAllByMailUser(user).each{item ->
						item.delete()
					}
					Contact.findAllByMailUser(user).each{item ->
						item.delete()
					}
					ContactGroup.findAllByMailUser(user).each{item ->
						item.delete()
					}
					
					//删除所有代办事项
//					Gtask.findAllByUser(it).each{item->
//						item.delete()
//					}
					
					//删除公告栏相关信息
				
					user.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			log.debug(e);
			json = [result:'error']
		}
		render json as JSON
	}
    def index() { }
}
