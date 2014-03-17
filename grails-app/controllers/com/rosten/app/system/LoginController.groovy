/* Copyright 2013 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rosten.app.system

import grails.converters.JSON

import javax.servlet.http.HttpServletResponse

import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.web.WebAttributes

import grails.plugin.springsecurity.SpringSecurityUtils
import com.rosten.app.util.Util
import com.rosten.app.system.SystemLog

@Secured('permitAll')
class LoginController {
	
	private def addLoginInformation = {user->
		if(!"rostenadmin".equals(user.username)){
			def macAddress = Util.getMacAddress()
			def ipAddress = Util.getIpAddress(request)
			
			def systemLog = new SystemLog()
			systemLog.user = user
			systemLog.ipAddress= ipAddress
			systemLog.macAddress= macAddress
			systemLog.content= "登录系统"
			systemLog.company= user.company
			
			systemLog.save(flush:true)
		}
		
	}
	/**
	 * Dependency injection for the authenticationTrustResolver.
	 */
	def authenticationTrustResolver

	/**
	 * Dependency injection for the springSecurityService.
	 */
	def springSecurityService

	/**
	 * Default action; redirects to 'defaultTargetUrl' if logged in, /login/auth otherwise.
	 */
	def index() {
		//判断是否是授权用户
		
		if (springSecurityService.isLoggedIn()) {
			
			def user = springSecurityService.getCurrentUser()
			
			//添加用户登录记录信息
			addLoginInformation(user)
			
			def model =[:]
			model["user"] = user
			
			def userinfor = [:]
			userinfor["username"] = user.username
			userinfor["chinaname"] = user.chinaName
			userinfor["idnumber"] = user.id
			userinfor["type"] = "true"
			userinfor["logoname"] = "企业信息管理平台"
			
			model["logoname"] = "企业信息管理平台"
			
			if(user.company){
				def logoset = LogoSet.findByCompany(user.company)
				if(logoset){
					if(logoset.logoName && !"".equals(logoset.logoName)){
						model["logoname"] = logoset.logoName
						userinfor["logoname"] = logoset.logoName
					}
					if(logoset.cssStyle && !"".equals(logoset.cssStyle)){
						userinfor["cssStyle"] = logoset.cssStyle
					}
				}
				userinfor["companyid"] = user.company.id
			}
			def userDepartName = user.getDepartName()
			if(userDepartName && !"".equals(userDepartName)){
				userinfor["departName"] = userDepartName
			}else{
				userinfor["departName"] = user.getCompanyName()
			}
			
			if(user.cssStyle && !"".equals(user.cssStyle)){
				userinfor["individuationcss"] = user.cssStyle
			}
			if("rostenadmin".equals(user.username)){
				//超级管理员
				model["usertype"] = "[超级管理员]"
			}else{
				model["normal"] = true
				//普通人员
				if(user.sysFlag){
					model["usertype"] = "[管理员]"
				}else{
					model["usertype"] = "[普通人员]"
				}
				//获取所有角色
			}
			model["userinfor"] = userinfor as JSON
			render (view:SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl + "index",model:model)
//			redirect uri: SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl
		}
		else {
			redirect action: 'auth', params: params
		}
	}
	def dlgauth = {
		def config = SpringSecurityUtils.securityConfig
		
		if (springSecurityService.isLoggedIn()) {
			redirect uri: config.successHandler.defaultTargetUrl
			return
		}
		String view = "dlgauth"
		render view:view
	}

	/**
	 * Show the login page.
	 */
	def auth() {
		def config = SpringSecurityUtils.securityConfig

		if (springSecurityService.isLoggedIn()) {
			redirect uri: config.successHandler.defaultTargetUrl
			return
		}

		String view = 'auth'
		String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"
		render view: view, model: [postUrl: postUrl,
		                           rememberMeParameter: config.rememberMe.parameter]
	}

	/**
	 * The redirect action for Ajax requests.
	 */
	def authAjax() {
		response.setHeader 'Location', SpringSecurityUtils.securityConfig.auth.ajaxLoginFormUrl
		response.sendError HttpServletResponse.SC_UNAUTHORIZED
	}

	/**
	 * Show denied page.
	 */
	def denied() {
		if (springSecurityService.isLoggedIn() &&
				authenticationTrustResolver.isRememberMe(SCH.context?.authentication)) {
			// have cookie but the page is guarded with IS_AUTHENTICATED_FULLY
			redirect action: 'full', params: params
		}
	}

	/**
	 * Login page for users with a remember-me cookie but accessing a IS_AUTHENTICATED_FULLY page.
	 */
	def full() {
		def config = SpringSecurityUtils.securityConfig
		render view: 'auth', params: params,
			model: [hasCookie: authenticationTrustResolver.isRememberMe(SCH.context?.authentication),
			        postUrl: "${request.contextPath}${config.apf.filterProcessesUrl}"]
	}

	/**
	 * Callback after a failed login. Redirects to the auth page with a warning message.
	 */
	def authfail() {

		String msg = ''
		def exception = session[WebAttributes.AUTHENTICATION_EXCEPTION]
		if (exception) {
			if (exception instanceof AccountExpiredException) {
				msg = g.message(code: "springSecurity.errors.login.expired")
			}
			else if (exception instanceof CredentialsExpiredException) {
				msg = g.message(code: "springSecurity.errors.login.passwordExpired")
			}
			else if (exception instanceof DisabledException) {
				msg = g.message(code: "springSecurity.errors.login.disabled")
			}
			else if (exception instanceof LockedException) {
				msg = g.message(code: "springSecurity.errors.login.locked")
			}
			else {
				msg = g.message(code: "springSecurity.errors.login.fail")
			}
		}

		if (springSecurityService.isAjax(request)) {
			render([error: msg] as JSON)
		}
		else {
			flash.message = msg
			redirect action: 'auth', params: params
		}
	}

	/**
	 * The Ajax success redirect url.
	 */
	def ajaxSuccess() {
		render([success: true, username: springSecurityService.authentication.name] as JSON)
	}

	/**
	 * The Ajax denied redirect url.
	 */
	def ajaxDenied() {
		render([error: 'access denied'] as JSON)
	}
}
