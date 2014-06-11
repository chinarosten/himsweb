package com.rosten.app.mail

import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.web.context.request.RequestContextHolder

class RequestCredentialsMailSender extends JavaMailSenderImpl {
  public String getUsername() {
    return RequestContextHolder.requestAttributes?.currentRequest?.mailUsername ?: super.getUsername()
  }
  public String getPassword() {
    return RequestContextHolder.requestAttributes?.currentRequest?.mailPassword ?: super.getPassword()
  }
  public String getHost() {
	return RequestContextHolder.requestAttributes?.currentRequest?.mailHost ?: super.getHost()
  }
  public int getPort() {
	return RequestContextHolder.requestAttributes?.currentRequest?.mailPort ?: super.getPort()
  }
}