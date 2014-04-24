package com.rosten.app.system

class LoginFilters {

    def filters = {
        all(controller:'j_spring_security_check', action:'*') {
            before = {
				println params.logintype
				if(params.logintype){
					session.setAttribute("logintype",params.logintype)
				}
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }
}
