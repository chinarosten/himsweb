package com.rosten.app.system
import com.rosten.app.annotation.GridColumn

class User {

	transient springSecurityService

	String id

	@GridColumn(name="登录名",formatter="user_formatTopic")
	String username
	String password
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired
	
	//所属公司
	//@GridColumn(name="所属公司")
	def getCompanyName(){
		if(company) return company.companyName
		else return null
	}
	
	//中文名
	@GridColumn(name="中文名")
	String chinaName
	
	def getFormattedName(){
		return chinaName?chinaName:username
	}
	
	//用户类型
	UserType userTypeEntity
	
	@GridColumn(name="用户类型")
	def getUserTypeName(){
		if(userTypeEntity) return userTypeEntity.typeName
		else return ""
	}
	
	//所属部门
	@GridColumn(name="所属部门")
	def getDepartName(){
		def depart = getDepartEntity()
		if(depart) return depart.departName
		else return ""
	}
	def getDepartEntity(){
		def entitys = UserDepart.findByUser(this,[sort:"user"])
		if(entitys) return entitys.depart
		else return null
	}
	def getAllDepartEntity(){
		def departsEntity =[]
		UserDepart.findAllByUser(this,[sort:"user"]).each{
			departsEntity << it.depart
		}
		return departsEntity
	}
	
	def getDepartEntityTrueName(){
		def entitys = UserDepart.findByUser(this,[sort:"user"])
		if(entitys) return entitys.depart.departName
		else return getCompanyName()
	}
	
	//具有角色
	@GridColumn(name="具有角色")
	def getAllRolesValue(){
		def _array = []
		getAllRoles().each{
			_array << it.authority
		}
		return _array.join(",")
	}
	
	def getAllRoles(){
		def _array = []
		//用户自身拥有角色
		UserRole.findAllByUser(this).each{
			_array << it.role
		}
		//用户所属群组拥有角色
		UserGroup.findAllByUser(this).each{
			RoleGroup.findAllByGroup(it.group).each{item->
				_array << item.role
			}
		}
		return _array.unique()
	}
	
	//是否领导
	boolean leaderFlag = false
	//@GridColumn(name="领导",width="40px",colIdx=6)
	def getleaderFlagValue(){
		if(leaderFlag)return "是"
		else return "否"
	}
	
	//是否管理员
	boolean sysFlag = false
	//@GridColumn(name="是否管理员")
	def getSysFlagValue(){
		if(sysFlag)return "是"
		else return "否"
	}
	
	//样式表
	String cssStyle
	
	//手机号码
	@GridColumn(name="手机号码")
	String telephone
	
	//身份证号码
	String idCard
	
	//用户地址
	@GridColumn(name="用户地址")
	String address
	
	//email
	@GridColumn(name="邮箱")
	String email
	
	//是否开启外网邮箱
	boolean isOnMail = false
	
	//描述
	//@GridColumn(name="描述")
	String description
	
	//邮箱配置
	EmailConfig emailConfig
	
	//创建日期
	Date createdDate = new Date()
	
	//所属单位
	static belongsTo = [company:Company]

	static transients = ['springSecurityService',"sysFlagValue","companyName","departEntity","departName","userType","userTypeName","allRoles","allRolesValue","encodedPassword"]

	static constraints = {
		username blank: false, unique: true
		password blank: false
		
		company nullable:true,blank:true
		cssStyle nullable:true,blank:true
		telephone nullable:true,blank:true
		chinaName nullable:true,blank:true
		idCard nullable:true,blank:true
		address nullable:true,blank:true
		email nullable:true,blank:true
		description nullable:true,blank:true
		userTypeEntity nullable:true,blank:true
		isOnMail nullable:true,blank:true
		emailConfig nullable:true,blank:true
	}

	static mapping = { 
		password column: '`password`' 
		id generator:'uuid.hex',params:[separator:'-']
		description sqlType:"text"
		table "ROSTEN_USER"
	}
	
	def getEncodedPassword(String passwordstr){
		return springSecurityService.encodePassword(passwordstr)
	}
	def getUserType(){
		if ("rostenadmin".equals(username)){
			return "super"
		}else if(sysFlag){
			return "admin"
		}else {
			return "normal"
		}
	}
	
	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role } as Set
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}
	
	def beforeDelete(){
		UserDepart.removeAll(this)
		UserGroup.removeAll(this)
		UserRole.removeAll(this)
		ModelUser.removeAll(this)
		
		User.withNewSession{session ->
			SmsGroup.findAllByUser(this).each{item->
				item.delete()
			}
			Authorize.findAllByAuthorizer(this).each{item ->
				item.delete()
			}
			SystemLog.findAllByUser(this).each{item->
				item.delete()
			}
			Attachment.findAllByUpUser(this).each{item->
				item.delete()
			}
			WorkLog.findAllByUser(this).each{item->
				item.delete()
			}
			session.flush()
		}
		
	}
}
