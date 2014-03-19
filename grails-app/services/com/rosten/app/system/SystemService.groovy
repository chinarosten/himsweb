package com.rosten.app.system

import grails.converters.JSON
import com.rosten.app.util.GridUtil

class SystemService {
	static transactional = true
	def deleteRole ={role->
		for(def index=0;role.children.size();index++){
			deleteRole(role.children[index])
		}
		def p = role.parent
		if(p){
			p.removeFromChildren(role)
			p.save(flush:true)
		}
		role.delete(flush:true)
	}
	def deleteDepart ={depart->
		for(def index=0;depart.children.size();index++){
			deleteDepart(depart.children[index])
		}
		def company = depart.company
		if(company){
			company.removeFromDeparts(depart)
			company.save(flush:true)
		}
		def p = depart.parent
		if(p){
			p.removeFromChildren(depart)
			p.save(flush:true)
		}
		depart.refresh()
		depart.delete(flush:true)
	}
	def getResourceListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Resource())
	}
	def getUserTypeListDataStore = {params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllUserType(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getUserTypeListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new UserType())
	}
	def getAllUserType={offset,max,company->
		def c = UserType.createCriteria()
		def pa=[max:max,offset:offset]
		def query = { eq("company",company) }
		return c.list(pa,query)
	}
	def getUserTypeCount={company->
		def c = UserType.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	def getPermissionListDataStore = {params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllPermission(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getPermissionListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Permission())
	}
	def getAllPermission={offset,max,company->
		def c = Permission.createCriteria()
		def pa=[max:max,offset:offset]
		def query = { eq("company",company) }
		return c.list(pa,query)
	}
	def getPermissionCount={company->
		def c = Permission.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	def getRoleListDataStore = {params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllRole(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getRoleListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Role())
	}
	def getAllRole={offset,max,company->
		def c = Role.createCriteria()
		def pa=[max:max,offset:offset]
		def query = { eq("company",company) }
		return c.list(pa,query)
	}
	def getRoleCount={company->
		def c = Role.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}

	def getGroupListDataStore = {params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllGroup(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getGroupListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Group1())
	}
	def getAllGroup={offset,max,company->
		def c = Group1.createCriteria()
		def pa=[max:max,offset:offset]
		def query = { eq("company",company) }
		return c.list(pa,query)
	}
	def getGroupCount={company->
		def c = Group1.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}

	def getModelListDataStore = {params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllModel(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getModelListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Model())
	}
	def getAllModel={offset,max,company->
		def c = Model.createCriteria()
		def pa=[max:max,offset:offset]
		def query = { eq("company",company) }
		return c.list(pa,query)
	}
	def getModelCount={company->
		def c = Model.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	def getSmsGroupListDataStore = {params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllSmsGroup(offset,max,params.user)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getSmsGroupListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new SmsGroup())
	}
	def getAllSmsGroup={offset,max,user->
		def c = SmsGroup.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("user",user)
		}
		return c.list(pa,query)
	}
	def getSmsGroupCount={user->
		def c = SmsGroup.createCriteria()
		def query = {
			eq("user",user)
		}
		return c.count(query)
	}
	
	def getSmsListDataStore = {params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllSms(offset,max)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getSmsListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Sms())
	}
	def getAllSms={offset,max->
		return Sms.createCriteria().list(max:max,offset:offset){}
	}
	def getSmsCount={
		return Sms.createCriteria().count(){}
	}
	
	def getSystemLogListDataStore = {params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllSystemLog(offset,max)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getSystemLogListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new SystemLog())
	}
	def getAllSystemLog={offset,max->
		return SystemLog.createCriteria().list(max:max,offset:offset){
			order("company", "desc")
			order("createDate", "desc")
		}
	}
	def getSystemLogCount={
		return SystemLog.createCriteria().count(){
			order("company", "desc")
			order("createDate", "desc")
		}
	}

	def getQuestionListDataStore = {params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllQuestion(offset,max)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getQuestionListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Question())
	}
	def getAllQuestion={offset,max->
		return Question.createCriteria().list(max:max,offset:offset){}
	}
	def getQuestionCount={
		return Question.createCriteria().count(){}
	}
	def getUserListDataStore = {params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllUser(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getUserListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new User())
	}
	def getAllUser={offset,max,company->
		def c = User.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("sysFlag",false)
			eq("company",company)
		}
		return c.list(pa,query)
	}
	def getUserCount={company->
		def c = User.createCriteria()
		def query = {
			eq("sysFlag",false)
			eq("company",company)
		}
		return c.count(query)
	}
	def getAdministratorListDataStore = {params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllAdministrator(offset,max)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getAdministratorListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new User())
	}
	def getAllAdministrator={offset,max->
		def c = User.createCriteria()
		def pa=[max:max,offset:offset]
		def query = { eq("sysFlag",true) }
		return c.list(pa,query)
	}
	def getAdministratorCount={
		def c = User.createCriteria()
		def query = { eq("sysFlag",true) }
		return c.count(query)
	}

	def getCompanyListDataStore = {params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllCompany(offset,max)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getCompanyListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Company())
	}
	//获取所有company
	def getAllCompany={offset,max->
		return Company.createCriteria().list(max:max,offset:offset){}
	}
	//获取所有company数量
	def getCompanyCount={
		return Company.createCriteria().count(){}
	}
	def crateDefaultNavigation={navilist->
		def model = [:]
		model["name"] = "切换用户"
		model["href"] = "javascript:logout()"
		model["img"] = "images/rosten/navigation/logout.gif"
		navilist << model
	}
	def initData_copy ={company,fromCompany->
		try{
			Model.findAllWhere(company:fromCompany).each{
				def model = new Model()
				model.company = company
				model.modelName = it.modelName
				model.modelUrl = it.modelUrl
				model.description = it.description
				it.resources.each{rs->
					def resource = new Resource()
					resource.resourceName = rs.resourceName
					resource.url = rs.url
					resource.imgUrl = rs.imgUrl
					resource.description = rs.description
					model.addToResources(resource)
				}
				model.save(flush:true)
			}
		}catch(Exception e){
			print e.message
			return false
		}
		return true
	}
	
	private def initData_bbs ={ path,company ->
		def model = new Model(company:company)
		model.modelName = "公告栏"
		model.modelCode = "bbs"
		model.modelUrl = path + "/system/navigation"
		model.description ="公告栏模块"

		def resource = new Resource()
		resource.resourceName = "配置文档"
		resource.url = "bbsConfigManage"
		resource.imgUrl = "images/rosten/navigation/config.png"
		model.addToResources(resource)
		
		resource = new Resource()
		resource.resourceName = "各人待办"
		resource.url = "mybbsManage"
		resource.imgUrl = "images/rosten/navigation/bbs_my.gif"
		model.addToResources(resource)
		
		resource = new Resource()
		resource.resourceName = "最新公告"
		resource.url = "newbbsManage"
		resource.imgUrl = "images/rosten/navigation/bbs_new.gif"
		model.addToResources(resource)
		
		resource = new Resource()
		resource.resourceName = "所有公告"
		resource.url = "allbbsManage"
		resource.imgUrl = "images/rosten/navigation/bbs_all.gif"
		model.addToResources(resource)
		
		model.save(flush:true)
	}
	private def initData_sendfile ={path,company ->
		def model = new Model(company:company)
		model.modelName = "发文管理"
		model.modelCode = "sendfile"
		model.modelUrl = path + "/system/navigation"
		model.description ="发文管理模块"
		
		def resource = new Resource()
		resource.resourceName = "配置文档"
		resource.url = "sendfileLabelManage"
		resource.imgUrl = "images/rosten/navigation/config.png"
		model.addToResources(resource)

		resource = new Resource()
		resource.resourceName = "各人待办"
		resource.url = "mySendfileManage"
		resource.imgUrl = "images/rosten/navigation/bbs_my.gif"
		model.addToResources(resource)
		
		resource = new Resource()
		resource.resourceName = "所有发文"
		resource.url = "allSendFileManage"
		resource.imgUrl = "images/rosten/navigation/bbs_all.gif"
		model.addToResources(resource)
		
		model.save(flush:true)
	}
	private def initData_receivefile ={path,company ->
		def model = new Model(company:company)
		model.modelName = "收文管理"
		model.modelCode = "receivefile"
		model.modelUrl = path + "/system/navigation"
		model.description ="收文管理模块"
		
		def resource = new Resource()
		resource.resourceName = "配置文档"
		resource.url = "receivefileConfigManage"
		resource.imgUrl = "images/rosten/navigation/config.png"
		model.addToResources(resource)
		
		resource = new Resource()
		resource.resourceName = "各人待办"
		resource.url = "myReceivefileManage"
		resource.imgUrl = "images/rosten/navigation/bbs_my.gif"
		model.addToResources(resource)
		
		resource = new Resource()
		resource.resourceName = "所有收文"
		resource.url = "allReceivefileManage"
		resource.imgUrl = "images/rosten/navigation/bbs_all.gif"
		model.addToResources(resource)
		
		model.save(flush:true)

	}
	private def initData_meeting ={path,company ->
		def model = new Model(company:company)
		model.modelName = "会议通知"
		model.modelCode = "meeting"
		model.modelUrl = path + "/system/navigation"
		model.description ="会议通知模块"
		
		def resource = new Resource()
		resource.resourceName = "配置文档"
		resource.url = "meetingConfigManage"
		resource.imgUrl = "images/rosten/navigation/config.png"
		model.addToResources(resource)
		
		resource = new Resource()
		resource.resourceName = "各人待办"
		resource.url = "myMeetingManage"
		resource.imgUrl = "images/rosten/navigation/bbs_my.gif"
		model.addToResources(resource)
		
		resource = new Resource()
		resource.resourceName = "所有会议"
		resource.url = "allMeetingManage"
		resource.imgUrl = "images/rosten/navigation/bbs_all.gif"
		model.addToResources(resource)
		
		model.save(flush:true)

	}
	private def initData_dsj ={path,company ->
		def model = new Model(company:company)
		model.modelName = "大事记"
		model.modelCode = "dsj"
		model.modelUrl = path + "/system/navigation"
		model.description ="大事记模块"
		
		def resource = new Resource()
		resource.resourceName = "配置文档"
		resource.url = "dsjConfigManage"
		resource.imgUrl = "images/rosten/navigation/config.png"
		model.addToResources(resource)
		
		resource = new Resource()
		resource.resourceName = "各人待办"
		resource.url = "myDsjManage"
		resource.imgUrl = "images/rosten/navigation/bbs_my.gif"
		model.addToResources(resource)
		
		resource = new Resource()
		resource.resourceName = "所有大事记"
		resource.url = "allDsjManage"
		resource.imgUrl = "images/rosten/navigation/bbs_all.gif"
		model.addToResources(resource)
		
		model.save(flush:true)

	}
	def initData_first ={path,company->
		try{
			
			def model = new Model(company:company)
			model.modelName = "系统管理"
			model.modelUrl = path + "/system/navigation"
			model.modelCode = "system"
			model.description ="系统配置文件管理模块"

			def resource = new Resource()
			resource.resourceName = "Logo设置"
			resource.url = "logSet"
			resource.imgUrl = "images/rosten/navigation/Logo.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "模块管理"
			resource.url = "modelManage"
			resource.imgUrl = "images/rosten/navigation/Model.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "部门管理"
			resource.url = "departManage"
			resource.imgUrl = "images/rosten/navigation/Depart.gif"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "员工类型"
			resource.url = "userTypeManage"
			resource.imgUrl = "images/rosten/navigation/UserType.gif"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "员工信息"
			resource.url = "userManage"
			resource.imgUrl = "images/rosten/navigation/User.gif"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "群组管理"
			resource.url = "groupManage"
			resource.imgUrl = "images/rosten/navigation/Group.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "角色管理"
			resource.url = "roleManage"
			resource.imgUrl = "images/rosten/navigation/Role.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "权限管理"
			resource.url = "permissionManage"
			resource.imgUrl = "images/rosten/navigation/Permission.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "资源管理"
			resource.url = "resourceManage"
			resource.imgUrl = "images/rosten/navigation/Resource.gif"
			model.addToResources(resource)
			model.save(flush:true)
			
			//公告栏
			initData_bbs(path,company)
			
			//发文管理
			initData_sendfile(path,company)
			
			//收文管理
			initData_receivefile(path,company)
			
			//会议通知
			initData_meeting(path,company)
			
			//大事记
			initData_dsj(path,company)

			model = new Model(company:company)
			model.modelName = "短信发送"
			model.modelCode = "sms"
			model.modelUrl = "js:sms"
			model.description ="短信发送模块"
			model.save(flush:true)

			model = new Model(company:company)
			model.modelName = "你问我答"
			model.modelCode = "question"
			model.modelUrl = "js:question"
			model.description ="你问我答模块"
			model.save(flush:true)
			
			model = new Model(company:company)
			model.modelName = "个人配置"
			model.modelCode = "personconfig"
			model.modelUrl = path + "/system/navigation"
			model.description ="个人配置模块"
			
			resource = new Resource()
			resource.resourceName = "个人资料"
			resource.url = "personInformation"
			resource.imgUrl = "images/rosten/navigation/User.gif"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "短信群组"
			resource.url = "smsgroup"
			resource.imgUrl = "images/rosten/navigation/Group.gif"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "待办工作"
			resource.url = "gtaskManage"
			resource.imgUrl = "images/rosten/navigation/gtask.png"
			model.addToResources(resource)
			
			model.save(flush:true)
			
		}catch(Exception e){
			print e.message
			return false
		}
		return true
	}
	def checkIsRosten(String usercode) {
		if ("rostenadmin".equals(usercode))
			return true;
		return false;
	}
	def serviceMethod() {

	}
}
