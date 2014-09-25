import com.rosten.app.system.SystemService

class BootStrap {
	
	def systemService
	
	def init = { servletContext ->
		systemService.systemInit()
	}
	def destroy = {
	}
}
