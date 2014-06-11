// Place your Spring DSL code here
beans = {
  mailSender(com.rosten.app.mail.RequestCredentialsMailSender) {
    def mailConf = application.config.grails.mail
    host = mailConf.host
    port = mailConf.port
    username = mailConf.username // the default, if not set in request
    password = mailConf.password
	defaultEncoding = mailConf.defaultEncoding
//    protocol = mailConf.protocol
    javaMailProperties = mailConf.props
  }
}