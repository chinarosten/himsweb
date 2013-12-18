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
	def initData_first ={path,company->
		try{
			def model = new Model(company:company)
			model.modelName = "系统管理"
			model.modelUrl = path + "/system/navigation"
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

			model = new Model(company:company)
			model.modelName = "库存管理"
			model.modelUrl = path + "/system/navigation"
			model.description ="库存管理模块"

			resource = new Resource()
			resource.resourceName = "库存信息"
			resource.url = "hairdress_stock"
			resource.imgUrl = "images/rosten/navigation/Hairdress_Stock.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "库存记录"
			resource.url = "hairdress_stockUseLog"
			resource.imgUrl = "images/rosten/navigation/Hairdress_StockUseLog.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "库存消耗"
			resource.url = "hairdress_stockUse"
			resource.imgUrl = "images/rosten/navigation/Hairdress_StockUse.gif"
			model.addToResources(resource)
			model.save(flush:true)

			model = new Model(company:company)
			model.modelName = "系统配置"
			model.modelUrl = path + "/system/navigation"
			model.description ="系统配置模块"

			resource = new Resource()
			resource.resourceName = "服务类别"
			resource.url = "hairdress_serviceType"
			resource.imgUrl = "images/rosten/navigation/Hairdress_ServiceType.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "服务项目"
			resource.url = "hairdress_service"
			resource.imgUrl = "images/rosten/navigation/Hairdress_Service.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "促销服务项目"
			resource.url = "hairdress_service_sale"
			resource.imgUrl = "images/rosten/navigation/Hairdress_Service_Sale.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "消费卡种类"
			resource.url = "hairdress_cardType"
			resource.imgUrl = "images/rosten/navigation/Hairdress_ConsumeType.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "疗程卡种类"
			resource.url = "hairdress_periodCard"
			resource.imgUrl = "images/rosten/navigation/Hairdress_ConsumeType.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "折扣信息"
			resource.url = "hairdress_discount"
			resource.imgUrl = "images/rosten/navigation/Discount.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "充卡设置"
			resource.url = "hairdress_fillSet"
			resource.imgUrl = "images/rosten/navigation/Set.gif"
			model.addToResources(resource)
			model.save(flush:true)

			model = new Model(company:company)
			model.modelName = "员工管理"
			model.modelUrl = path + "/system/navigation"
			model.description ="员工管理模块"

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
			model.save(flush:true)

			model = new Model(company:company)
			model.modelName = "会员管理"
			model.modelUrl = path + "/system/navigation"
			model.description ="会员管理模块"

			resource = new Resource()
			resource.resourceName = "卡号管理"
			resource.url = "hairdress_cardCode"
			resource.imgUrl = "images/rosten/navigation/ConsumeType.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "客户管理"
			resource.url = "hairdress_consumer"
			resource.imgUrl = "images/rosten/navigation/Consumer.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "充值记录"
			resource.url = "hairdress_fillLog"
			resource.imgUrl = "images/rosten/navigation/FillLog.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "消费记录"
			resource.url = "hairdress_consumeLog"
			resource.imgUrl = "images/rosten/navigation/ConsumeType.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "添加新客户"
			resource.url = "hairdress_addConsumer"
			resource.imgUrl = "images/rosten/navigation/Consumer_Add.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "客户登录"
			resource.url = "hairdress_login"
			resource.imgUrl = "images/rosten/navigation/Hairdress_Login.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "修改密码"
			resource.url = "hairdress_chgPassword"
			resource.imgUrl = "images/rosten/navigation/Hairdress_ChgPassword.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "账户注销"
			resource.url = "hairdress_logout"
			resource.imgUrl = "images/rosten/navigation/Hairdress_Logout.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "客户补卡"
			resource.url = "hairdress_repair"
			resource.imgUrl = "images/rosten/navigation/Hairdress_OldToNew.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "顾客查询"
			resource.url = "hairdress_consumer_search"
			resource.imgUrl = "images/rosten/navigation/Search.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "日志查询"
			resource.url = "hairdress_search"
			resource.imgUrl = "images/rosten/navigation/Hairdress_Search.gif"
			model.addToResources(resource)

			model.save(flush:true)

			model = new Model(company:company)
			model.modelName = "业绩管理"
			model.modelUrl = path + "/system/navigation"
			model.description ="业绩管理模块"

			resource = new Resource()
			resource.resourceName = "个人业绩统计"
			resource.url = "hairdress_static_staff"
			resource.imgUrl = "images/rosten/navigation/Report.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "公司业绩统计"
			resource.url = "hairdress_static_company"
			resource.imgUrl = "images/rosten/navigation/Report.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "公司项目数统计"
			resource.url = "hairdress_static_comProject"
			resource.imgUrl = "images/rosten/navigation/Report.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "公司客服数统计"
			resource.url = "hairdress_static_comConsumer"
			resource.imgUrl = "images/rosten/navigation/Report.gif"
			model.addToResources(resource)

			resource = new Resource()
			resource.resourceName = "顾客消费业绩统计"
			resource.url = "hairdress_static_consumerConsume"
			resource.imgUrl = "images/rosten/navigation/Report.gif"
			model.addToResources(resource)
			model.save(flush:true)

			model = new Model(company:company)
			model.modelName = "短信发送"
			model.modelUrl = "js:sms"
			model.description ="短信发送模块"
			model.save(flush:true)

			model = new Model(company:company)
			model.modelName = "你问我答"
			model.modelUrl = "js:question"
			model.description ="你问我答模块"
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