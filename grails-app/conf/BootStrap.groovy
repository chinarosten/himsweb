import com.rosten.app.system.Resource
import com.rosten.app.system.Role
import com.rosten.app.system.User
import com.rosten.app.system.UserRole
import com.rosten.app.system.Model

class BootStrap {

	def init = { servletContext ->

		def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)
		def adminUser = User.findByUsername('rostenadmin') ?: new User(
				username:'rostenadmin',
				password:'rosten_2012',
				userCode:'rostenadmin',
				enabled: true).save(failOnError: true)

		if (!adminUser.authorities.contains(adminRole)) {
			UserRole.create adminUser, adminRole,true
		}
		
		if(Model.findByCompany(null)==null){
			def model = new Model(modelName:"平台管理",description:"超级管理员系统平台配置文件管理模块")
			model.modelUrl = "/himsweb/system/navigation"
			
			def resource = new Resource()
			resource.resourceName = "组织机构管理"
			resource.url = "companyManage"
			resource.imgUrl = "images/rosten/navigation/Organize.gif"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "应用管理员"
			resource.url = "adminManage"
			resource.imgUrl = "images/rosten/navigation/Administrator.gif"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "组织机构初始化"
			resource.url = "systemToolManage"
			resource.imgUrl = "images/rosten/navigation/OrganizeInit.gif"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "广告管理"
			resource.url = "advertiseManage"
			resource.imgUrl = "images/rosten/navigation/Advertise.gif"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "你问我答"
			resource.url = "questionManage"
			resource.imgUrl = "images/rosten/navigation/Question.gif"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "短信管理"
			resource.url = "smsManage"
			resource.imgUrl = "images/rosten/navigation/Resource.gif"
			model.addToResources(resource)
			
			model.save(failOnError: true)
			
			model = new Model(modelName:"个人办公",description:"个人办公")
			model.modelUrl = "/himsweb/mail/navigation@tree"
			model.save(fialOnError:true)
		}

	}
	def destroy = {
	}
}
