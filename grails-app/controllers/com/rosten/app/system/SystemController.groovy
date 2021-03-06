package com.rosten.app.system

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.util.Util
import org.springframework.security.authentication.encoding.PasswordEncoder;
//import com.rosten.app.workflow.WorkFlowService

@SuppressWarnings("deprecation")
class SystemController {
	def springSecurityService
	def systemService
	//def workFlowService
	
	def workLogGetContent ={
		def workLog = WorkLog.get(params.id)
		render workLog.content
	}
	def personWorkLogAdd ={
		redirect(action:"personWorkLogShow",params:params)
	}
	def personWorkLogShow ={
		def model =[:]
		
		if(params.id){
			model["workLog"] = WorkLog.get(params.id)
		}else{
			model["workLog"] = new WorkLog()
		}
		model["user"] = springSecurityService.getCurrentUser()
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		render(view:'/system/workLog',model:model)
	}
	def personWorkLogSave ={
		def model=[:]
		def workLog = new WorkLog()
		if(params.id && !"".equals(params.id)){
			workLog = WorkLog.get(params.id)
		}else{
			workLog.user = springSecurityService.getCurrentUser()
		}
		
		workLog.properties = params
		workLog.clearErrors()
		workLog.createDate = Util.convertToTimestamp(params.date)
		
		if(workLog.save(flush:true)){
			model["result"] = "true"
		}else{
			workLog.errors.each{
				println it
			}
			model["result"] = "false"
		}
		render model as JSON
	}
	def personWorkLogDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def workLog = WorkLog.get(it)
				if(workLog){
					workLog.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def personWorkLogGrid ={
		def model=[:]
		def user = User.get(params.userid)
		if(params.refreshHeader){
			model["gridHeader"] = systemService.getWorkLogListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["user"] = user
			model["gridData"] = systemService.getWorkLogListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getWorkLogCount(user)
			model["pageControl"] = ["total":total.toString()]
		}
		render model as JSON
	}
	
	def systemSearchView ={
		def model =[:]
		render(view:'/system/userSearch',model:model)
	}
	def modelDeleteFlow1 ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def model = Model.get(it)
				if(model){
					model.relationFlow = null
					model.relationFlowName = null
					model.save(flush:true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'false']
		}
		render json as JSON
	}
	def modelAddFlow1 ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def model = Model.get(it)
				if(model){
					model.relationFlow = params.flowId
					model.relationFlowName = params.flowName
					model.save(flush:true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'false']
		}
		render json as JSON
	}
	
	def getContactDepartInfor ={
		def _depart = Depart.get(params.departId)
		render(view:'/system/contact_right',model:[departEntity:_depart])
	}
	def getContactDepart ={
		def company = Company.get(params.companyId)
		def firstDepart = Depart.findWhere(
			company:company,
			parent:null
		)
		def contactShowId
		if(firstDepart.children && firstDepart.children.size()>0){
			contactShowId = firstDepart.children[0].id
		}
		render(view:'/system/contact_left',model:[departsList:firstDepart.children,contactShowId:contactShowId])
	}
	def authorizeCancel ={
		def json=[:]
		def authorize = Authorize.get(params.id)
		authorize.status = "取消"
		if(authorize.save(flush:true)){
			json["result"] = "true"
		}else{
			authorize.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	def authorizeStart ={
		def json=[:]
		def authorize = Authorize.get(params.id)
		authorize.status = "正常"
		if(authorize.save(flush:true)){
			json["result"] = "true"
		}else{
			authorize.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	def authorizeDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def authorize = Authorize.get(it)
				if(authorize){
					authorize.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def authorizeSave ={
		def json=[:]
		def authorize = new Authorize()
		if(params.id && !"".equals(params.id)){
			authorize = Authorize.get(params.id)
			
			authorize.properties = params
			authorize.clearErrors()
			
			authorize.authModels.clear()
		}else{
			authorize.properties = params
			authorize.clearErrors()
			
			authorize.company = Company.get(params.companyId)
			authorize.authorizer = User.get(params.userId)
		}
		
		authorize.beAuthorizer = User.get(params.beAuthorizerId)
		authorize.startDate = Util.convertToTimestamp(params.startDate)
		authorize.endDate = Util.convertToTimestamp(params.endDate)
		
		params.modelId.split(",").each{
			def model = Model.get(it)
			authorize.addToAuthModels(model)
		}
		
		if(authorize.save(flush:true)){
			json["result"] = "true"
		}else{
			authorize.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	def authorizeAdd ={
		redirect(action:"authorizeShow",params:params)
	}
	def authorizeShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def authorize = new Authorize()
		if(params.id){
			authorize = Authorize.get(params.id)
			
			def modelName = []
			def modelId =[]
			authorize.authModels.each{
				modelName << it.modelName
				modelId << it.id
			}
			model["modelName"] = modelName.join(",")
			model["modelId"] = modelId.join(",")
		}else{
			authorize.authorizer = user
			authorize.authorizerDepart = user.getDepartName()
			authorize.endDate = authorize.endDate + 7
		}
		model["user"]=user
		model["company"] = company
		model["authorize"] = authorize
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
//			fa.readOnly += ["authority","description"]
		}
		model["fieldAcl"] = fa
		
		render(view:'/system/authorize',model:model)
	}
	def authorizeGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = systemService.getAuthorizeListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = systemService.getAuthorizeListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getAuthorizeCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def systemLogGrid ={
		def model=[:]
		if(params.refreshHeader){
			model["gridHeader"] = systemService.getSystemLogListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			model["gridData"] = systemService.getSystemLogListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getSystemLogCount()
			model["pageControl"] = ["total":total.toString()]
		}
		render model as JSON
	}
	
	def downloadFile = {
		def attachmentInstance =  Attachment.get(params.id)
		def filename = attachmentInstance.name
		
		response.setHeader("Content-disposition", "attachment; filename=" + new String(filename.getBytes("GB2312"), "ISO_8859_1"))
		response.contentType = ""
		
		def filepath = new File(attachmentInstance.url, attachmentInstance.realName)
		def out = response.outputStream
		def inputStream = new FileInputStream(filepath)
		byte[] buffer = new byte[1024]
		int i = -1
		while ((i = inputStream.read(buffer)) != -1) {
			out.write(buffer, 0, i)
		}
		out.flush()
		out.close()
		inputStream.close()
	}
	
	def serachPerson ={
		def company = Company.get(params.companyId)
		
		def c = User.createCriteria()
		def query = {
			eq("company",company)
			or{
				like("username","%" + params.serchInput +  "%")
				like("chinaName","%" + params.serchInput +  "%")
				like("telephone","%" + params.serchInput +  "%")
			}
		}
		
		def _list = []
		c.list(query).unique().each{
			def smap =[:]
			smap["username"] = it.username
			smap["phone"] = it.telephone
			smap["mobile"] = it.telephone
			smap["email"] = it.email
			
			_list << smap
		}
		render _list as JSON
	}
	def skinSave ={
		def json =[:]
		def user = User.get(params.id)
		user.cssStyle = params.cssStyle
		if(user.save(flush:true)){
			json["result"] = "true"
		}else{
			user.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	def changeSkin ={
		def model = ["user":User.get(params.id)]
		render(view:'/system/skin',model:model)
	}
	def passwordChangeSubmit ={
		def json =[:]
		def user = User.get(params.id)
		
		PasswordEncoder passwordEncoder = springSecurityService.passwordEncoder
		if (passwordEncoder.isPasswordValid(user.password, params.password, null)){
			user.password = params.newpassword
			if(user.save(flush:true)){
				json["result"] = "true"
			}else{
				user.errors.each {
					println it
				}
				json["result"] = "false"
			}
		}else{
			json["result"] = "error"
		}
		render json as JSON
	}
	def passwordChangeShow ={
		def model = [:]
		render(view:'/system/passwordchg',model:model)
	}
	def passwordChangeShow1 ={
		def model = [:]
		model["unid"] = params.id
		render(view:'/system/passwordchg1',model:model)
	}
	def passwordChangeSubmit1 ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def user = User.get(it)
				if(user){
					user.password = params.newpassword
					user.save(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'false']
		}
		render json as JSON
	}
	
	def resourceTreeDataStore ={
		def company = Company.get(params.companyId)
		def dataList = Model.findAllByCompany(company)
		def json = [identifier:'id',label:'name',items:[]]
		dataList.each{
			if(!it.modelCode.equals("sms") && !it.modelCode.equals("question")){
				def sMap = ["id":it.id,"name":it.modelName,"parentId":null,"children":[]]
				def childMap
				it.resources.each{item->
					def resourceMap = ["id":item.id,"name":item.resourceName,"type":"resource"]
					json.items += resourceMap
					
					childMap = ["_reference":item.id,]
					sMap.children += childMap
				}
				json.items+=sMap
			}
		}
		render json as JSON
	}
	def serviceStatus ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def normalService = NormalService.get(it)
				if(normalService){
					normalService.status = params.status
					normalService.save(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def serviceDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def normalService = NormalService.get(it)
				if(normalService){
					normalService.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def serviceSave ={
		def json=[:]
		def normalService = new NormalService()
		if(params.id && !"".equals(params.id)){
			normalService = NormalService.get(params.id)
			normalService.properties = params
			normalService.clearErrors()
			
			normalService.model = Model.get(params.modelId)
			
			if(normalService.save(flush:true)){
				json["result"] = "true"
			}else{
				normalService.errors.each{
					println it
				}
				json["result"] = "false"
			}
			render json as JSON
			
		}else{
			normalService.properties = params
			normalService.clearErrors()
			normalService.company = Company.get(params.companyId)
			normalService.model = Model.get(params.modelId)
			if(normalService.save(flush:true)){
				json["result"] = "true"
			}else{
				normalService.errors.each{
					println it
				}
				json["result"] = "false"
			}
			render json as JSON
		}
		
	}
	def serviceAdd ={
		redirect(action:"serviceShow",params:params)
	}
	def serviceShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def normalService = new NormalService()
		if(params.id){
			normalService = NormalService.get(params.id)
		}
		model["user"]=user
		model["company"] = company
		model["normalService"] = normalService
		
		FieldAcl fa = new FieldAcl()
//		if("normal".equals(user.getUserType())){
//			//普通用户
//			fa.readOnly = ["serviceName","url","imgUrl"]
//		}
		model["fieldAcl"] = fa
		
		render(view:'/system/normalService',model:model)
	}
	def serviceGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = systemService.getNormalServiceListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = systemService.getNormalServiceListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getNormalServiceCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	def resourceSetDefault ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def resource = Resource.get(it)
				if(resource){
					if("true".equals(params.isDefault)){
						resource.isDefault = true
					}else{
						resource.isDefault = false
					}
					resource.save(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def resourceDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def resource = Resource.get(it)
				if(resource){
					resource.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def resourceSave ={
		def json=[:]
		def resource = new Resource()
		if(params.id && !"".equals(params.id)){
			resource = Resource.get(params.id)
			resource.properties = params
			
			if(!resource.model.id.equals(params.modelId)){
				try{
//					def _resource = resource
					
//					def _model = resource.model
//					_model.removeFromResources(resource)
//					_model.save(flush:true)
					
					def model = Model.get(params.modelId)
					model.addToResources(resource)
					model.save(flush:true)
					
					json["result"] = "true"
				}catch(Exception e){
					json["result"] = "error"
				}
				
			}else{
				resource.properties = params
				if(resource.save(flush:true)){
					json["result"] = "true"
				}else{
					resource.errors.each{
						println it
					}
					json["result"] = "false"
				}
			}
			render json as JSON
			
		}else{
			resource.properties = params
			
			def model = Model.get(params.modelId)
			model.addToResources(resource)
			
			if(model.save(flush:true)){
				json["result"] = "true"
			}else{
				model.errors.each{
					println it
				}
				json["result"] = "false"
			}
			render json as JSON
		}
		
	}
	def resourceAdd ={
		redirect(action:"resourceShow",params:params)
	}
	def resourceShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def resource = new Resource()
		if(params.id){
			resource = Resource.get(params.id)
		}else{
			resource.imgUrl = "images/rosten/navigation/rosten.png"
		}
		model["user"]=user
		model["company"] = company
		model["_resource"] = resource
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			//fa.readOnly = ["resourceName","url","imgUrl","description"]
		}
		model["fieldAcl"] = fa
		
		render(view:'/system/resource',model:model)
	}
	def resourceGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = systemService.getResourceListLayout()
		}
		
		def totalNum = 0
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			def offset =  (nowPage-1) * perPageNum
			def max = perPageNum
			
			def resourceList = company.getAllResource()
			totalNum = resourceList.size()
			
			def datajson = [identifier:'id',label:'title',items:[]]
			resourceList.eachWithIndex{ item,idx->
				if(idx>=offset && idx <offset+max){
					def sMap =[:]
					sMap["rowIndex"] = idx+1
					sMap["id"] = item.id
					sMap["resourceName"] = item.resourceName
					sMap["url"] = item.url
					sMap["imgUrl"] = item.imgUrl
					sMap["description"] = item.description
					sMap["getModelName"] = item.getModelName()
					sMap["getTab"] = item.getTab()
					datajson.items+=sMap
				}
			}
			json["gridData"] = datajson
		}
		if(params.refreshPageControl){
			json["pageControl"] = ["total":totalNum.toString()]
		}
		render json as JSON
	}
	def permissionSelect ={
		def permissionList =[]
		def company = Company.get(params.companyId)
		Permission.findAllByCompany(company).each{
			def json=[:]
			json["id"] = it.id
			json["name"] = it.permissionName
			permissionList << json
		}
		render permissionList as JSON
	}
	def permissionDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def permission = Permission.get(it)
				if(permission){
					permission.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def permissionSave ={
		def json=[:]
		def permission
		if(params.id && !"".equals(params.id)){
			permission = Permission.get(params.id)
			permission.properties = params
			
			PermissionResource.removeAll(permission)
			if(params.allowresourcesId){
				params.allowresourcesId.split(",").each{
					def resource = Resource.get(it)
					PermissionResource.create(resource, permission)
				}
			}
			
			if(permission.save(flush:true)){
				json["result"] = "true"
			}else{
				permission.errors.each{
					println it
				}
				json["result"] = "false"
			}
		}else{
			permission = new Permission()
			permission.properties = params
			def company = Company.get(params.companyId)
			company.addToPermissions(permission)
			if(company.save(flush:true)){
				PermissionResource.removeAll(permission)
				if(params.allowresourcesId){
					params.allowresourcesId.split(",").each{
						def resource = Resource.get(it)
						PermissionResource.create(resource, permission)
					}
				}
				json["result"] = "true"
			}else{
				company.errors.each{
					println it
				}
				json["result"] = "false"
			}
		}
		render json as JSON
	}
	def permissionAdd ={
		redirect(action:"permissionShow",params:params)
	}
	def permissionShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def permission = new Permission()
		
		model["setOperation"]=[]
		
		if(params.id){
			permission = Permission.get(params.id)
			
			def allowresourcesName=[]
			def allowresourcesId =[]
			PermissionResource.findAllByPermission(permission).each{
				allowresourcesName << it.resource.resourceName
				allowresourcesId << it.resource.id
			}
			model["allowresourcesName"] = allowresourcesName.join(',')
			model["allowresourcesId"] = allowresourcesId.join(",")
			
			permission.setOperation.split(",").each{
				model["setOperation"] << it
			}
			
		}
		model["user"]=user
		model["company"] = company
		model["permission"] = permission
		
		FieldAcl fa = new FieldAcl()
		fa.readOnly +=["allowresourcesName"]
		if("normal".equals(user.getUserType())){
			//普通用户
			//fa.readOnly += ["permissionName","setOperation","isAnonymous","description"]
		}
		model["fieldAcl"] = fa
		render(view:'/system/permission',model:model)
	}
	def permissionGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = systemService.getPermissionListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = systemService.getPermissionListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getPermissionCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	def roleSelect ={
		def roleList =[]
		def company = Company.get(params.companyId)
		Role.findAllByCompany(company).each{
			def json=[:]
			json["id"] = it.id
			json["name"] = it.authority
			roleList << json
		}
		render roleList as JSON
	}
	def roleDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def role = Role.get(it)
				if(role){
					systemService.deleteRole(role)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def roleSave ={
		def json=[:]
		def role = new Role()
		if(params.id && !"".equals(params.id)){
			role = Role.get(params.id)
		}
		role.properties = params
		if(params.companyId){
			role.company = Company.get(params.companyId)
		}
		if(role.save(flush:true)){
			RolePermission.removeAll(role)
			if(params.allowpermissionsId){
				params.allowpermissionsId.split(",").each{
					def permission = Permission.get(it)
					RolePermission.create(role, permission)
				}
			}
			json["result"] = "true"
		}else{
			role.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	def roleAdd ={
		redirect(action:"roleShow",params:params)
	}
	def roleShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def role = new Role()
		if(params.id){
			role = Role.get(params.id)
			
			def allowpermissionsName=[]
			def allowpermissionsId =[]
			RolePermission.findAllByRole(role).each{
				allowpermissionsName << it.permission.permissionName
				allowpermissionsId << it.permission.id
			}
			model["allowpermissionsName"] = allowpermissionsName.join(',')
			model["allowpermissionsId"] = allowpermissionsId.join(",")
		}
		model["user"]=user
		model["company"] = company
		model["role"] = role
		
		FieldAcl fa = new FieldAcl()
		fa.readOnly +=["allowpermissionsName"]
		if("normal".equals(user.getUserType())){
			//普通用户
			//fa.readOnly += ["authority","description"]
		}
		model["fieldAcl"] = fa
		
		render(view:'/system/role',model:model)
	}
	def roleGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = systemService.getRoleListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = systemService.getRoleListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getRoleCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	def userTypeDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def userType = UserType.get(it)
				if(userType){
					userType.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def userTypeSave ={
		def json=[:]
		def userType
		if(params.id && !"".equals(params.id)){
			userType = UserType.get(params.id)
			userType.properties = params
			if(userType.save(flush:true)){
				json["result"] = "true"
			}else{
				userType.errors.each{
					println it
				}
				json["result"] = "false"
			}
		}else{
			userType = new UserType()
			userType.properties = params
			def company = Company.get(params.companyId)
			company.addToUserTypes(userType)
			if(company.save(flush:true)){
				json["result"] = "true"
			}else{
				company.errors.each{
					println it
				}
				json["result"] = "false"
			}
		}
		render json as JSON
	}
	def userTypeAdd ={
		redirect(action:"userTypeShow",params:params)
	}
	def userTypeShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def userType = new UserType()
		
		if(params.id){
			userType = UserType.get(params.id)
		}
		model["user"]=user
		model["company"] = company
		model["userType"] = userType
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			//fa.readOnly += ["typeName","description"]
		}
		model["fieldAcl"] = fa
		render(view:'/system/userType',model:model)
	}
	def userTypeGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = systemService.getUserTypeListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = systemService.getUserTypeListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getUserTypeCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	def groupSelect ={
		def groupList =[]
		def company = Company.get(params.companyId)
		Group1.findAllByCompany(company).each{
			def json=[:]
			json["id"] = it.id
			json["name"] = it.groupName
			groupList << json
		}
		render groupList as JSON
	}
	def groupDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def group = Group1.get(it)
				if(group){
					group.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def groupSave ={
		def json=[:]
		def group
		if(params.id && !"".equals(params.id)){
			group = Group1.get(params.id)
			group.properties = params
			if(group.save(flush:true)){
				RoleGroup.removeAll(group)
				if(params.allowrolesId){
					params.allowrolesId.split(",").each{
						def role = Role.get(it)
						RoleGroup.create(role, group)
					}
				}
				
				UserGroup.removeAll(group)
				if(params.allowusersId){
					params.allowusersId.split(",").each{
						def user = User.get(it)
						UserGroup.create(user, group)
					}
				}
				
				json["result"] = "true"
			}else{
				group.errors.each{
					println it
				}
				json["result"] = "false"
			}
		}else{
			group = new Group1()
			group.properties = params
			def company = Company.get(params.companyId)
			company.addToGroups(group)
			if(company.save(flush:true)){
				RoleGroup.removeAll(group)
				if(params.allowrolesId){
					params.allowrolesId.split(",").each{
						def role = Role.get(it)
						RoleGroup.create(role, group)
					}
				}
				
				UserGroup.removeAll(group)
				if(params.allowusersId){
					params.allowusersId.split(",").each{
						def user = User.get(it)
						UserGroup.create(user, group)
					}
				}
				
				json["result"] = "true"
			}else{
				company.errors.each{
					println it
				}
				json["result"] = "false"
			}
		}
		render json as JSON
	}
	def groupAdd ={
		redirect(action:"groupShow",params:params)
	}
	def groupShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def group = new Group1()
		if(params.id){
			group = Group1.get(params.id)
			
			def allowusersName=[]
			def allowusersId =[]
			UserGroup.findAllByGroup(group).each{
				allowusersName << it.user.chinaName!=null?it.user.chinaName:it.user.username
				allowusersId << it.user.id
			}
			model["allowusersName"] = allowusersName.join(',')
			model["allowusersId"] = allowusersId.join(",")
			
			def allowrolesName=[]
			def allowrolesId =[]
			RoleGroup.findAllByGroup(group).each{
				allowrolesName << it.role.authority
				allowrolesId << it.role.id
			}
			model["allowrolesName"] = allowrolesName.join(',')
			model["allowrolesId"] = allowrolesId.join(",")
			
		}
		model["user"]=user
		model["company"] = company
		model["group"] = group
		
		FieldAcl fa = new FieldAcl()
		fa.readOnly +=["allowusersName","allowrolesName"]
		if("normal".equals(user.getUserType())){
			//普通用户
			//fa.readOnly += ["groupName","description"]
		}
		model["fieldAcl"] = fa
		
		render(view:'/system/group',model:model)
	}
	def groupGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = systemService.getGroupListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = systemService.getGroupListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getGroupCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def modelSelect ={
		def modelJson = []
		def modelList = []
		
		def user = springSecurityService.getCurrentUser()
		def userType = user.getUserType()
		if("admin".equals(userType)){
			//管理员
			modelList = Model.findAllByCompany(Company.get(params.companyId))
		}else{
			modelList = getPermissionModel(user)
		}
		
		modelList.each{
			def json=[:]
			json["id"] = it.id
			json["name"] = it.modelName
			modelJson << json
		}
		render modelJson as JSON
	}
	def modelDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def model = Model.get(it)
				if(model){
					model.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def modelSave ={
		def json=[:]
		def model = new Model()
		if(params.id && !"".equals(params.id)){
			model = Model.get(params.id)
		}
		model.properties = params
		if(model.modelUrl==null || "".equals(model.modelUrl)){
			model.modelUrl = "/himsweb/system/navigation"
		}
		if(params.companyId && model.company==null){
			model.company = Company.get(params.companyId)
		}
		if(model.save(flush:true)){
			ModelUser.removeAll(model)
			if(params.allowusersId){
				params.allowusersId.split(",").each{
					def user = User.get(it)
					ModelUser.create(model, user)
				}
			}
			
			ModelDepart.removeAll(model)
			if(params.allowdepartsId){
				params.allowdepartsId.split(",").each{
					def depart = Depart.get(it)
					ModelDepart.create(model, depart)
				}
			}
			
			ModelGroup.removeAll(model)
			if(params.allowgroupsId){
				params.allowgroupsId.split(",").each{
					def group = Group1.get(it)
					ModelGroup.create(model, group)
				}
			}
			json["result"] = "true"
		}else{
			model.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	def modelAdd ={
		redirect(action:"modelShow",params:params)
	}
	def modelShow ={
		def json =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def model = new Model()
		if(params.id){
			model = Model.get(params.id)
			
			def allowusersName=[]
			def allowusersId =[]
			ModelUser.findAllByModel(model).each{
				allowusersName << it.user.chinaName!=null?it.user.chinaName:it.user.username
				allowusersId << it.user.id
			}
			json["allowusersName"] = allowusersName.join(',')
			json["allowusersId"] = allowusersId.join(",")
			
			def allowdepartsName=[]
			def allowdepartsId =[]
			ModelDepart.findAllByModel(model).each{
				allowdepartsName << it.depart.departName
				allowdepartsId << it.depart.id
			}
			json["allowdepartsName"] = allowdepartsName.join(',')
			json["allowdepartsId"] = allowdepartsId.join(",")
			
			def allowgroupsName=[]
			def allowgroupsId =[]
			ModelGroup.findAllByModel(model).each{
				allowgroupsName << it.group.groupName
				allowgroupsId << it.group.id
			}
			json["allowgroupsName"] = allowgroupsName.join(',')
			json["allowgroupsId"] = allowgroupsId.join(",")
			
//			def allowRelationFlow = []
//			if(model.relationFlow && !"".equals(model.relationFlow)){
//				model.relationFlow.split(",").each{
//					def _result = workFlowService.getProcessDefinition(it)
//					allowRelationFlow << _result.name + "(" + _result.version + ")"
//				}
//			}
//			json["allowRelationFlow"] = allowRelationFlow.join(',')
		}
		
		json["user"]=user
		json["company"] = company
		json["model"] = model
		
		FieldAcl fa = new FieldAcl()
		fa.readOnly +=["allowusersName","allowdepartsName","allowgroupsName","allowRelationFlow"]
		
		if("normal".equals(user.getUserType())){
			//普通用户
			fa.readOnly += ["modelCode","modelName","modelUrl","description"]
		}else if("admin".equals(user.getUserType())){
			if(params.id){
				fa.readOnly << "modelCode"
			}
		}
		json["fieldAcl"] = fa
		
		render(view:'/system/model',model:json)
	}
	def modelGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = systemService.getModelListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = systemService.getModelListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getModelCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def departSave ={
		def depart
		if(params.id){
			depart = Depart.get(params.id)
			depart.properties = params
			depart.clearErrors()
			
			if(depart.save(flush:true)){
				flash.refreshTree = true;
				flash.message = "'"+depart.departName+"' 已成功保存！"
//				redirect(action:"departShow",id:depart.id)
				render(view:'/system/depart_Edit',model:[depart:depart])
			}else{
				render(view:'/system/depart_Edit',model:[depart:depart])
			}
		}else{
			depart = new Depart()
			depart.properties = params
			depart.clearErrors()
			
			def company = Company.get(params.companyId)
			company.addToDeparts(depart)
			company.save(flush:true)
			
			if(params.parentId){
				def parent = Depart.get(params.parentId)
				parent.addToChildren(depart)
				parent.save(flush:true)
			}
			
			flash.refreshTree = true;
			flash.message = "'"+depart.departName+"' 已成功保存！"
//			redirect(action:"departShow",id:depart.id)
			render(view:'/system/depart_Edit',model:[depart:depart])
		}
	}
	def departDelete ={
		def ids = params.id.split(",")
		def name
		try{
			ids.each{
				def depart = Depart.get(it)
				if(depart){
					name = depart.departName
					systemService.deleteDepart(depart)
				}
			}
		}catch(Exception e){
			println e
		}
		render "<script type='text/javascript'>refreshDepartTree()</script><h3>&nbsp;&nbsp;部门<"+name+">及其下级部门信息已删除！</h3>"
	}
	def departCreate ={
		def model =[:]
		model["parentId"] = params.parentId
		model["companyId"] = params.companyId
		model["depart"] = new Depart()
		
		render(view:'/system/depart_Edit',model:model)
	}
	def departShow ={
		def model =[:]
		model["depart"] = Depart.get(params.id)
		render(view:'/system/depart_Edit',model:model)
	}
	def departTreeDataStore ={
		def company = Company.get(params.companyId)
		def dataList = Depart.findAllByCompany(company)
		def json = [identifier:'id',label:'name',items:[]]
		dataList.each{
			def sMap = ["id":it.id,"name":it.departName,"parentId":it.parent?.id,"children":[]]
			def childMap
			it.children.each{item->
				childMap = ["_reference":item.id]
				sMap.children += childMap
			}
			json.items+=sMap
		}
		render json as JSON
	}
	def depart ={
		def model =[:]
		model["company"] = Company.get(params.companyId)
		render(view:'/system/depart',model:model)
	}
	def smsGroupAdd ={
		redirect(action:"smsGroupShow",params:params)
	}
	def smsGroupShow ={
		def model =[:]
		
		if(params.id){
			model["smsgroup"] = SmsGroup.get(params.id)
		}else{
			model["smsgroup"] = new SmsGroup()
		}
		model["company"] = springSecurityService.getCurrentUser().company
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		render(view:'/system/smsGroup',model:model)
	}
	def smsGroupSave ={
		def model=[:]
		def smsGroup = new SmsGroup()
		if(params.unid && !"".equals(params.unid)){
			smsGroup = SmsGroup.get(params.unid)
		}else{
			smsGroup.user = springSecurityService.getCurrentUser()
		}
		
		smsGroup.properties = params
		
		if(smsGroup.save(flush:true)){
			model["result"] = "true"
		}else{
			smsGroup.errors.each{
				println it
			}
			model["result"] = "false"
		}
		render model as JSON
	}
	def smsGroupDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def smsgroup = SmsGroup.get(it)
				if(smsgroup){
					smsgroup.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def smsGroupGrid ={
		def model=[:]
		def user = User.get(params.userid)
		if(params.refreshHeader){
			model["gridHeader"] = systemService.getSmsGroupListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["user"] = user
			model["gridData"] = systemService.getSmsGroupListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getSmsGroupCount(user)
			model["pageControl"] = ["total":total.toString()]
		}
		render model as JSON
	}
	def sms_group ={
		def groupList =[]
		def user = User.get(params.userid)
		SmsGroup.findAllByUser(user).each{
			def json=[:]
			json["id"] = it.id
			json["name"] = it.groupName
			groupList << json
		}
		render groupList as JSON
	}
	def smsSave ={
		def model=[:]
		def sms = new Sms()
		if(params.id && !"".equals(params.id)){
			sms = Sms.get(params.id)
		}
		User user = springSecurityService.getCurrentUser()
		sms.sender = user.username
		sms.sendto = params.telephone
		sms.content = params.content
		sms.company = user.company
		
		if(sms.save(flush:true)){
			model["result"] = "true"
		}else{
			sms.errors.each{
				println it
			}
			model["result"] = "false"
		}
		render model as JSON
	}
	def smsAdd ={
		redirect(action:"smsShow",params:params)
	}
	def smsShow ={
		def model =[:]
		
		if(params.id){
			model["sms"] = Sms.get(params.id)
		}else{
			model["sms"] = new Sms()
		}
		model["user"] = springSecurityService.getCurrentUser()
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		render(view:'/system/sms',model:model)
	}
	def smsDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def sms = Sms.get(it)
				if(sms){
					sms.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def smsGrid ={
		def model=[:]
		if(params.refreshHeader){
			model["gridHeader"] = systemService.getSmsListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			model["gridData"] = systemService.getSmsListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getSmsCount()
			model["pageControl"] = ["total":total.toString()]
		}
		render model as JSON
	}
	
	def questionSave ={
		def model=[:]
		def question = new Question()
		if(params.unid && !"".equals(params.unid)){
			question = Question.get(params.unid)
			
			if(params.isAnswer){
				if("否".equals(params.isAnswer)){
					params.isAnswer = false
				}else{
					params.isAnswer = true
				}
			}
			
		}else{
			question.company = springSecurityService.getCurrentUser().company
		}
		
		question.properties = params
		
		if(question.save(flush:true)){
			model["result"] = "true"
		}else{
			question.errors.each{
				println it
			}
			model["result"] = "false"
		}
		render model as JSON
	}
	def questionAdd ={
		redirect(action:"questionShow",params:params)
	}
	def questionShow ={
		def model =[:]
		
		if(params.id){
			model["question"] = Question.get(params.id)
		}else{
			model["question"] = new Question()
		}
		
		model["user"] = (User) springSecurityService.getCurrentUser()
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		render(view:'/system/question',model:model)
	}
	def questionDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def question = Question.get(it)
				if(question){
					question.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def questionGrid ={
		def model=[:]
		if(params.refreshHeader){
			model["gridHeader"] = systemService.getQuestionListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			model["gridData"] = systemService.getQuestionListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getQuestionCount()
			model["pageControl"] = ["total":total.toString()]
		}
		render model as JSON
	}
	def advertiseSave ={
		def model=[:]
		def advertise = new Advertise()
		if(params.id && !"".equals(params.id)){
			advertise = Advertise.get(params.id)
		}
		advertise.properties = params
		if(advertise.save(flush:true)){
			model["result"] = "true"
		}else{
			advertise.errors.each{
				println it
			}
			model["result"] = "false"
		}
		render model as JSON
	}
	def advertise ={
		def model = [:]
		model["advertise"] = null
		def _list = Advertise.list()
		if(_list!=null){
			model["advertise"] = _list[0]
		}
		render(view:'/system/advertise',model:model)
	}
	def systemTool ={
		def model = [:]
		model["companyList"] = Company.createCriteria().list(){}
		render(view:'/system/systemTool',model:model)
	}
	def systemTool_Init={
		def json = [:]
		def company = Company.get(params.dealCompany)
		if(company){
			Model.findAllByCompany(company).each{model->
				model.delete()
			}
		}
		
		def result
		if(params.fromCompany){
			def fromCompany = Company.get(params.fromCompany)
			result = systemService.initData_copy(company,fromCompany)
		}else{
			result = systemService.initData_first(request.contextPath,company)
		}
		json["result"] = result
		
		render json as JSON
	}
	def systemTool_DeleteData ={
		def json = [:]
		try{
			def company = Company.get(params.dealCompany)
			if(company){
				company.delete(flush:true)
			}
			json["result"] = true
		}catch(Exception e){
			print e.message
			json["result"] = false
		}
		render json as JSON
	}
	def systemTool_Resource ={
		def model = [:]
		if(params.dealCompanyId){
			def company = Company.get(params.dealCompanyId)
			model["modelList"] = Model.findAllWhere(company:company)
		}else{
			model["modelList"] = Model.createCriteria().list(){}
		}
		
		render(view:'/system/systemTool_Resource',model:model)
	}
	def systemTool_AddResource ={
		def json =[:]
		def model = Model.get(params.modelId)
		def resource = new Resource()
		resource.properties = params
		resource.clearErrors()
		resource.model = model
		
		model.addToResources(resource)
		if(model.save(flush:true)){
			json["result"] = true
		}else{
			model.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	def userGrid ={
		def model=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			model["gridHeader"] = systemService.getUserListLayout()
		}
		//2014-9-1 增加搜索功能
		def searchArgs =[:]
		
		if(params.username && !"".equals(params.username)) searchArgs["username"] = params.username
		if(params.chinaName && !"".equals(params.chinaName)) searchArgs["chinaName"] = params.chinaName
		if(params.telephone && !"".equals(params.telephone)) searchArgs["telephone"] = params.telephone
		
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			model["gridData"] = systemService.getUserListDataStore(args,searchArgs)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getUserCount(company,searchArgs)
			model["pageControl"] = ["total":total.toString()]
		}
		render model as JSON
	}
	private def getFirstDepart ={depart->
		if(depart.parent){
			return getFirstDepart(depart.parent)
		}else{
			return depart
		}
	}
	private def getAllDepart ={departList,depart->
		departList << depart
		if(depart.parent){
			return getAllDepart(departList,depart.parent)
		}else{
			return departList
		}
	}
	private def getAllDepartByChild ={departList,depart->
		departList << depart
		if(depart.children){
			depart.children.each{
				return getAllDepartByChild(departList,it)
			}
		}else{
			return departList
		}
	}
	def userTreeDataStore ={
		def json = [identifier:'id',label:'name',items:[]]
		def company = Company.get(params.companyId)
		/*
		 * 一个用户名只允许存在一个user，但可以允许有多个部门
		 */
		if(params.user && !"".equals(params.user)){
			def user = User.findWhere(company:company,username:params.user)
			if(user){
				def departSmap =[]	//需要显示的所有部门列表
				if(params.depart && !"".equals(params.depart)){
					//查询用户与部门全匹配，默认只允许存在一个
					def depart = Depart.findByCompanyAndDepartName(company,params.depart)
					
					def resultDepart = null
					
					//首先获取所有的相关查询部门
					def searchDepart =[]
					getAllDepartByChild(searchDepart,depart)
					searchDepart.unique()
					for(def index=0;searchDepart.size();index++){
						def _UserDept = UserDepart.findByUserAndDepart(user,searchDepart[index])
						if(_UserDept){
							resultDepart = searchDepart[index]
							break;
						}
					}
					
					if(resultDepart!=null){
						getAllDepart(departSmap,resultDepart)
						getAllDepartByChild(departSmap,resultDepart)
						
						def lastDepartList = departSmap.unique()
						lastDepartList.each{
							def sMap = ["id":it.id,"name":it.departName,"parentId":it.parent?.id,"children":[]]
							def childMap
							it.children.each{item->
								if(lastDepartList.find{_item->
									_item.id.equals(item.id)
								}){
									childMap = ["_reference":item.id]
									sMap.children += childMap
								}
							}
							if(it.id.equals(depart.id)){
								//增加当前用户
								def userMap = ["id":user.id,"name":user.getFormattedName(),"type":"user","departId":it.id,"departName":it.departName]
								json.items+=userMap
								
								def userChildMap = ["_reference":user.id]
								sMap.children += userChildMap
							}
							json.items+=sMap
						}
					}
				}else{
					//只查询用户匹配
					def blongDeparts = user.getAllDepartEntity()
					blongDeparts.each{
						getAllDepart(departSmap,it)
					}
					def lastDepartList = departSmap.unique()
					lastDepartList.each{
						def sMap = ["id":it.id,"name":it.departName,"parentId":it.parent?.id,"children":[]]
						def childMap
						it.children.each{item->
							if(departSmap.find{_item->
								_item.id.equals(item.id)
							}){
								childMap = ["_reference":item.id]
								sMap.children += childMap
							}
						}
						if(blongDeparts.contains(it)){
							//增加当前用户
							def userMap = ["id":user.id,"name":user.getFormattedName(),"type":"user","departId":it.id,"departName":it.departName]
							json.items+=userMap
							
							def userChildMap = ["_reference":user.id]
							sMap.children += userChildMap
						}
						json.items+=sMap
					}
					
				}
			}
		}else if(params.depart && !"".equals(params.depart)){
			//只查询部门匹配,匹配部门只允许存在一个
			def departSmap =[]	//需要显示的所有部门列表
			def departEntity = Depart.findByCompanyAndDepartName(company,params.depart)
			
			getAllDepart(departSmap,departEntity)
			getAllDepartByChild(departSmap,departEntity)
			
			def lastDepartList = departSmap.unique()
			lastDepartList.each{
				def sMap = ["id":it.id,"name":it.departName,"parentId":it.parent?.id,"children":[]]
				def childMap
				it.children.each{item->
					if(lastDepartList.find{_item->
						_item.id.equals(item.id)
					}){
						childMap = ["_reference":item.id]
						sMap.children += childMap
					}
				}
				it.getAllUser().each{user->
					def userMap = ["id":user.id,"name":user.getFormattedName(),"type":"user","departId":it.id,"departName":it.departName]
					json.items+=userMap
					
					def userChildMap = ["_reference":user.id]
					sMap.children += userChildMap
				}
				json.items+=sMap
			}
			
		}else if(params.groupIds && !"".equals(params.groupIds)){
			def allUsers =[]
			def allUsersDepart = []
			
			params.groupIds.split("-").each{
				def _group = Group1.findByGroupName(it)
				
				UserGroup.findAllByGroup(_group).each{item->
					allUsers << item.user
					allUsersDepart += item.user.getAllDepartEntity()
				}
			}
			allUsers.unique()
			allUsersDepart.unique()
			
			def departSmap =[]	//需要显示的所有部门列表
			if(params.limitDepart){
				//严格限制所在部门
				//只查询部门匹配,匹配部门只允许存在一个
				def departEntity = Depart.findByCompanyAndDepartName(company,params.limitDepart)
				
				getAllDepart(departSmap,departEntity)
				getAllDepartByChild(departSmap,departEntity)
				
			}else{
				//无需限制部门,从groupId中获取所有相关部门
				allUsersDepart.each{
					getAllDepart(departSmap,it)
					getAllDepartByChild(departSmap,it)
				}
			}
			
			def lastDepartList = departSmap.unique()
			lastDepartList.each{
				def sMap = ["id":it.id,"name":it.departName,"parentId":it.parent?.id,"children":[]]
				def childMap
				it.children.each{item->
					if(lastDepartList.find{_item->
						_item.id.equals(item.id)
					}){
						childMap = ["_reference":item.id]
						sMap.children += childMap
					}
				}
				it.getAllUser().each{user->
					
					if(allUsers.find{ _item1 ->
						_item1.id.equals(user.id)
					}){
						def userMap = ["id":user.id,"name":user.getFormattedName(),"type":"user","departId":it.id,"departName":it.departName]
						json.items+=userMap
						
						def userChildMap = ["_reference":user.id]
						sMap.children += userChildMap
					}
				}
				json.items+=sMap
			}
		}else{
			//获取所有部门与用户
			def dataList = Depart.findAllByCompany(company)
			dataList.each{
				def sMap = ["id":it.id,"name":it.departName,"parentId":it.parent?.id,"children":[]]
				def childMap
				it.children.each{item->
					childMap = ["_reference":item.id]
					sMap.children += childMap
				}
				it.getAllUser().each{user->
					def userMap = ["id":user.id,"name":user.getFormattedName(),"type":"user","departId":it.id,"departName":it.departName]
					json.items+=userMap
					
					def userChildMap = ["_reference":user.id]
					sMap.children += userChildMap
				}
				
				json.items+=sMap
			}
		}
		render json as JSON
	}
	def userSelectCompany ={
		def companyList =[]
		Company.list().each{
			def json=[:]
			json["id"] = it.id
			json["name"] = it.companyName
			companyList << json
		}
		render companyList as JSON
	}
	def userDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def user = User.get(it)
				if(user){
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
	def userSave ={
		def model=[:]
		
		//如果账号已经存在则登录名不变
		def username
		def user = new User()
		if(params.id && !"".equals(params.id)){
			user = User.get(params.id)
			username = user.username
		}else{
			user.enabled = true
			if(params.userNameFront){
				username = params.userNameFront + params.username
			}
		}
		user.properties = params
		user.username = username
		user.clearErrors()
		
		if(params.userTypeName && !params.userTypeName.equals(user.getUserTypeName())){
			def userType = UserType.findByTypeName(params.userTypeName)
			if(userType){
				user.userTypeEntity = userType
			}
		}
		
		if(params.companyId){
			def company = Company.get(params.companyId)
			user.company = company
		}
		if(user.save(flush:true)){
			
			UserDepart.removeAll(user)
			if(params.allowdepartsId){
				def depart = Depart.get(params.allowdepartsId)
				if(depart){
					UserDepart.create(user, depart)
				}
			}
			
			UserRole.removeAll(user)
			if(params.allowrolesId){
				params.allowrolesId.split(",").each{
					def role = Role.get(it)
					UserRole.create(user, role)
				}
			}
			
			model["result"] = "true"
		}else{
			user.errors.each{
				println it
			}
			model["result"] = "false"
		}
		render model as JSON
	}
	def userAdd ={
		redirect(action:"userShow",params:params)
	}
	def userSimpleSave ={
		def model=[:]
		
		def user = User.get(params.id)
		user.properties = params
		user.clearErrors()
		
		//处理邮箱配置信息
		if(user.isOnMail){
			if(!user.emailConfig){
				user.emailConfig = new EmailConfig()
			}
			user.emailConfig.user = user
			user.emailConfig.loginName = params.loginName
			user.emailConfig.loginPassword = params.loginPassword
			
			//根据用户的登录名，自动匹配smt与pop
			def _smtp,_smtpPort,_popName,_popPort
			def _serverName = Util.strLeft(Util.strRight(params.loginName, "@"),".")
			switch(_serverName){
				case "qq":
					_smtp = "smtp.qq.com"
					_smtpPort = "465"
					_popName = "pop.qq.com"
					_popPort = "995"
					break
				case "163":
					_smtp = "smtp.163.com"
					_smtpPort = "465"
					_popName = "pop.163.com"
					_popPort = "995"
					break
				case "126":
					_smtp = "smtp.126.com"
					_smtpPort = "25"
					_popName = "pop.126.com"
					_popPort = "110"
					break
				case "gmail":
					_smtp = "smtp.gmail.com"
					_smtpPort = "465"
					_popName = "pop.gmail.com"
					_popPort = "995"
					break
				case "hotmail":
					_smtp = "smtp.hotmail.com"
					_smtpPort = "25"
					_popName = "pop.hotmail.com"
					_popPort = "995"
					break
				case "live":
					_smtp = "smtp.live.com"
					_smtpPort = "587"
					_popName = "pop.live.com"
					_popPort = "995"
					break
				default:
					_smtp = "smtp." + _serverName + ".com"
					_smtpPort = "465"
					_popName = "pop。" + _serverName + ".com"
					_popPort = "995"
					break
			}
			
			user.emailConfig.smtp = _smtp
			user.emailConfig.port = _smtpPort.toInteger()
			user.emailConfig.popName = _popName
			user.emailConfig.popPort = _popPort
			
			user.emailConfig.save()
		}
		
		if(user.save(flush:true)){
			model["result"] = "true"
		}else{
			user.errors.each{
				println it
			}
			model["result"] = "false"
		}
		render model as JSON
	}
	def personInformation ={
		def model =[:]
		
		def user = User.get(params.userid)
		model["user"]= user
		
		def allowrolesName=[]
		UserRole.findAllByUser(user).each{
			allowrolesName << it.role.authority
		}
		model["allowrolesName"] = allowrolesName.join(',')
		model["companyId"] = params.companyId
		model["emailConfig"] = user.emailConfig
		
		render(view:'/system/userSimple',model:model)
	}
	def userShow ={
		def model =[:]
		
		def loginUser = User.get(params.userid)
		model["loginUser"]= loginUser
		
		if(params.id){
			def _user = User.get(params.id)
			model["user"] = _user
			
			def allowrolesName=[]
			def allowrolesId =[]
			UserRole.findAllByUser(_user).each{
				allowrolesName << it.role.authority
				allowrolesId << it.role.id
			}
			model["allowrolesName"] = allowrolesName.join(',')
			model["allowrolesId"] = allowrolesId.join(",")
			
			if(_user.company){
				model["userTypeList"] = UserType.findAllByCompany(_user.company)
			}
			
			model["username"] = Util.strRight(_user.username, "-")
			
		}else{
			if(loginUser.company){
				def userTypeList = UserType.findAllByCompany(loginUser.company)
				if(userTypeList && userTypeList.size()>0){
					model["userTypeEntity"] = userTypeList[0]
				}
			}
		}
		if(params.companyId){
			def company = Company.get(params.companyId)
			model["company"] = company
			model["userTypeList"] = UserType.findAllByCompany(company)
		}
		
		
		model["userType"] = "normal"
		
		FieldAcl fa = new FieldAcl()
		fa.readOnly +=["allowdepartsName","allowrolesName"]
		if(loginUser!=null){
			if(systemService.checkIsRosten(loginUser.username)){
				model["userType"] = "super"
			}else if(loginUser.sysFlag){
				model["userType"] = "admin"
			}else{
				model["userType"] = "normal"
			}
		}
		if(loginUser==null || model["userType"].equals("normal")){
			params.each{key,value->
				fa.readOnly << key
			}
		}
		if(params.id){
			//fa.readOnly << "username"
		}
		model["fieldAcl"] = fa
		render(view:'/system/user',model:model)
	}
	def administratorGrid ={
		def model=[:]
		if(params.refreshHeader){
			model["gridHeader"] = systemService.getAdministratorListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			model["gridData"] = systemService.getAdministratorListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getAdministratorCount()
			model["pageControl"] = ["total":total.toString()]
		}
		render model as JSON
	}
	
	def companyDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def company = Company.get(it)
				if(company){
					company.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def companySave ={
		def model=[:]
		
		def company = new Company()
		def oldCompanyName = params.companyName
		
		if(params.id && !"".equals(params.id)){
			company = Company.get(params.id)
			oldCompanyName = company.companyName
		}
		company.properties = params
		
		def depart = Depart.findByDepartName(oldCompanyName)
		if(depart){
			//更新部门信息
			depart.departName = params.companyName
			depart.save(flush:true)
		}else{
			//创建对应的部门
			depart = new Depart(departName:params.companyName)
			company.addToDeparts(depart)
		}
		
		if(company.save(flush:true)){
			model["result"] = "true"
		}else{
			company.errors.each{
				println it
			}
			model["result"] = "false"
		}
		render model as JSON
	}
	def companyAdd ={
		redirect(action:"companyShow",params:params)
	}
	def companyShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		model["user"]=user
		
		if(params.id){
			model["company"] = Company.get(params.id)
		}else{
			model["company"] = new Company()
		}
		
		FieldAcl fa = new FieldAcl()
		if(user==null || (!systemService.checkIsRosten(user.username) && !user.sysFlag)){
			fa.readOnly = ["companyName","shortName","companyMobile","companyPhone","companyFax","companyAddress","isTurnon","isSmsOn","description"]
		}
		
		model["fieldAcl"] = fa
		render(view:'/system/company',model:model)
	}
	def companyGrid ={
		def model=[:]
		if(params.refreshHeader){
			model["gridHeader"] = systemService.getCompanyListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			model["gridData"] = systemService.getCompanyListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = systemService.getCompanyCount()
			model["pageControl"] = ["total":total.toString()]
		}
		render model as JSON
	}
	
	def navigation ={
		def resourceList =[]
		
		def modelEntity = Model.get(params.id)
		def user = springSecurityService.getCurrentUser()
		
		if(user){
			def userType = user.getUserType()
			if("super".equals(userType)){
				//超级管理员
				Resource.findAllByModel(modelEntity).each{
					def model = [:]
					model["name"] = it.resourceName
					model["href"] = "javascript:show_naviEntity(\"" + it.url + "\")"
					model["img"] = it.imgUrl
					model["url"] = it.url
					model["isDefault"] = it.isDefault
					resourceList << model
				}
			}else if("admin".equals(userType)){
				//管理员
				Resource.findAllByModel(modelEntity).each{
					def model = [:]
					model["name"] = it.resourceName
					model["href"] = "javascript:show_naviEntity(\"" + it.url + "\")"
					model["img"] = it.imgUrl
					model["url"] = it.url
					model["isDefault"] = it.isDefault
					resourceList << model
				}
			}else if("normal".equals(userType)){
				//普通用户-------------------------------------
				//获取当前用户的所有角色，并从角色获取权限，从而获取所有相关的资源信息
				def _resourceList = []
				user.getAllRoles().each{
					it.getAllPermissions().each{item->
						_resourceList += item.getAllResourcesByModel(modelEntity)
					}
				}
				_resourceList.unique().each{
					def model = [:]
					model["name"] = it.resourceName
					model["href"] = "javascript:show_naviEntity(\"" + it.url + "\")"
					model["img"] = it.imgUrl
					model["url"] = it.url
					model["isDefault"] = it.isDefault
					resourceList << model
				}
			}
		}
		//systemService.crateDefaultNavigation(resourceList)
		render resourceList as JSON
	}
	private def getPermissionModel={user ->
		def modelList =[]
		//获取缺省允许登录模块
		Model.findAllWhere(company:user.company).each{
			def modelUser = ModelUser.findAllByModel(it)
			def modelDepart = ModelDepart.findAllByModel(it)
			def modelGroup = ModelGroup.findAllByModel(it)
			
			if(modelUser.size==0 && modelDepart.size() ==0 && modelGroup.size()==0){
				modelList << it
			}
		}
		
		//获取用户模块集合
		ModelUser.findAllByUser(user).each{
			modelList << it.model
		}
		//获取用户群组,并通过群组获取群组模块集合
		UserGroup.findAllByUser(user).each{
			ModelGroup.findAllByGroup(it.group).each{item->
				modelList << item.model
			}
		}
		//获取部门，并通过部门获取部门模块集合
		UserDepart.findAllByUser(user).each{
			ModelDepart.findAllByDepart(it.depart).each{item->
				modelList << item.model
			}
		}
		
		def _modelList =[]
		modelList.each{modelEntity->
			//过滤无子菜单的model
			def _resourceList = []
			user.getAllRoles().each{
				it.getAllPermissions().each{item->
					_resourceList += item.getAllResourcesByModel(modelEntity)
				}
			}
			if(_resourceList.size!=0){
				_modelList << modelEntity
			}
		}
		modelList = _modelList
		
		modelList.unique()
		return modelList
	}
	def naviMenu ={
		def model =[:]
		def user = User.get(params.id)
		
		def modelList=[],defaultModel,userType
		
		defaultModel = Model.findByModelCode("start");
		def personModel = Model.findByModelCode("person")
		userType = user.getUserType()
		if("super".equals(userType)){
			//超级管理员
			modelList = Model.findAllWhere(company:null)
		}else if("admin".equals(userType)){
			//管理员
			modelList << defaultModel
			modelList << personModel
			
			//--------------2014-6-17,迁移到常用服务中-------------------
			def _modelList = Model.findAllWhere(company:user.company)
			modelList += _modelList.findAll{item->
				!item.modelCode.equals("sms") && !item.modelCode.equals("question")
			}
			//-----------------------------------------------------
			modelList.unique()
		}else if("normal".equals(userType)){
			//普通用户-----------------------------------------------
			
			//获取缺省允许登录模块
			Model.findAllWhere(company:user.company).each{
				def modelUser = ModelUser.findAllByModel(it)
				def modelDepart = ModelDepart.findAllByModel(it)
				def modelGroup = ModelGroup.findAllByModel(it)
				
				if(modelUser.size==0 && modelDepart.size() ==0 && modelGroup.size()==0){
					modelList << it
				}
			}
			
			//获取用户模块集合
			ModelUser.findAllByUser(user).each{
				modelList << it.model
			}
			//获取用户群组,并通过群组获取群组模块集合
			UserGroup.findAllByUser(user).each{
				ModelGroup.findAllByGroup(it.group).each{item->
					modelList << item.model
				}
			}
			//获取部门，并通过部门获取部门模块集合
			UserDepart.findAllByUser(user).each{
				ModelDepart.findAllByDepart(it.depart).each{item->
					modelList << item.model
				}
			}
			modelList.unique()
			
			def _modelList =[]
			_modelList << defaultModel
			_modelList << personModel
			
			modelList.each{modelEntity->
				//过滤无子菜单的model
				def _resourceList = []
				user.getAllRoles().each{
					it.getAllPermissions().each{item->
						_resourceList += item.getAllResourcesByModel(modelEntity)
					}
				}
				if(_resourceList.size!=0){
					_modelList << modelEntity
				}
			}
			modelList = _modelList
			
//			//增加短信通知以及你问我答--------------2014-6-17,迁移到常用服务中-------------------
//			modelList << Model.findByModelCode("sms")
//			modelList << Model.findByModelCode("question")
			//---------------------------------------------------------------------
		}
		
		def logoset = LogoSet.findWhere(company:user.company)
		if(logoset && modelList.contains(logoset.model)){
			defaultModel = logoset.model
		}
		if(modelList && modelList.size()>0){
			def _indexList =[]
			
			//首先获取serialNo不为空的数据
			def serialNoList = modelList.findAll{elem->
				elem.serialNo!=null
			}
			
			serialNoList.each{item->
				model[item.id + "&" + Util.obj2str(item.serialNo).padLeft(2,"0")] = item.modelCode + ":" + item.modelName + "&" + getLastModelUrl(item.modelUrl)
				_indexList << item.serialNo
			}
			
			//剩余数据
			def _index = 1
			def otherList = modelList - serialNoList
			otherList.each{item->
				def indexValue = getIndexValue(_index,_indexList)
				_indexList << indexValue
				_index = indexValue + 1
				
				model[item.id + "&" + Util.obj2str(indexValue).padLeft(2,"0")] = item.modelCode + ":" + item.modelName + "&" + getLastModelUrl(item.modelUrl)
			}
			
			if(defaultModel){
				model["default"] = defaultModel.id
			}else{
				model["default"] = modelList[0].id
			}
		}
		render model as JSON
	}
	private def getLastModelUrl ={url->
		if(!url.contains(request.contextPath)){
			return request.contextPath + url
		}else{
			return url
		}
	}
	private def getIndexValue = {index,arrayList->
		if(arrayList.contains(index)){
			index +=1 
			getIndexValue(index,arrayList)
		}else{
			return index
		}
	}
	def logoSet = {
		def model = [:]
		def user = springSecurityService.getCurrentUser()
		model.modelList = Model.findAllWhere(company:user.company)
		
		def logoSet = LogoSet.findWhere(company:user.company)
		if(logoSet==null) {
			logoSet = new LogoSet()
			model.company = user.company
			
		}else{
			model.company = logoSet.company
		}
		model.logoSet = logoSet
		render(view:'/system/logoSet',model:model)
	}
	def logoSetSave ={
		def json=[:]
		
		def company = Company.get(params.companyId)
		def oldCompanyName = company.companyName
		//修改单位信息
		company.companyName = params.companyName
		company.shortName = params.shortName
		company.companyPhone = params.companyPhone
		company.companyFax = params.companyFax
		company.companyAddress = params.companyAddress
		company.save()
		
		//修改部门信息
		def depart = Depart.findByDepartNameAndCompany(oldCompanyName,company)
		if(depart){
			//更新部门信息
			depart.departName = params.companyName
			depart.save()
		}
		
		//修改logo信息
		def logoSet = new LogoSet()
		if(params.id && !"".equals(params.id)){
			logoSet = LogoSet.get(params.id)
		}
		logoSet.properties = params
		logoSet.company = company
		
		//默认为首页，不允许修改
//		logoSet.model = Model.get(params.modelId)
		
		logoSet.model = null
		if(logoSet.save(flush:true)){
			json["result"] = true
		}else{
			logoSet.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
}
