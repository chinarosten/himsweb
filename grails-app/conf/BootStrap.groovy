import com.rosten.app.system.Role
import com.rosten.app.system.User
import com.rosten.app.system.UserRole

class BootStrap {

	def init = { servletContext ->
		def adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true)
		def userRole = new Role(authority: 'ROLE_USER').save(flush: true)

		def admin = new User(username: 'admin', enabled: true, password: 'password') 
		admin.save(flush: true)
		UserRole.create admin, adminRole, true
		
		def client = new User(username: 'client', enabled: true, password: 'password') 
		client.save(flush: true)
		UserRole.create client, userRole, true
	}
	def destroy = {
	}
}
