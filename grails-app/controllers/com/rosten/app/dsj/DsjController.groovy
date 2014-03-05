package com.rosten.app.dsj

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.util.SystemUtil
import com.rosten.app.util.Util
import com.rosten.app.system.Attachment
import com.rosten.app.system.Company
import com.rosten.app.system.User

class DsjController {
	def springSecurityService
	def dsjService
		
	def getFileUpload ={
		def model =[:]
		model["docEntity"] = "dsj"
		model["isShowFile"] = false
		if(params.id){
			//已经保存过
			def dsj = Dsj.get(params.id)
			model["dsj"] = dsj
			//获取附件信息
			model["attachFiles"] = Attachment.findAllByBeUseId(params.id)
		}else{
			//尚未保存
			model["newDoc"] = true
		}
		render(view:'/share/fileUpload',model:model)
	}
	
	def uploadFile = {
		def json=[:]
		SystemUtil sysUtil = new SystemUtil()
		
		def uploadPath = sysUtil.getUploadPath("dsj")
		def f = request.getFile("uploadedfile")
		if (f.empty) {
			json["result"] = "blank"
			render json as JSON
			return
		}
		
		def uploadSize = sysUtil.getUploadSize()
		if(uploadSize!=null){
			//控制附件上传大小
			def maxSize = uploadSize * 1024 * 1024
			if(f.size>=maxSize){
				json["result"] = "big"
				render json as JSON
				return
			}
		}
		String name = f.getOriginalFilename()//获得文件原始的名称
		def realName = sysUtil.getRandName(name)
		f.transferTo(new File(uploadPath,realName))
		
		def attachment = new Attachment()
		attachment.name = name
		attachment.realName = realName
		attachment.type = "dsj"
		attachment.url = uploadPath
		attachment.size = f.size
		attachment.beUseId = params.id
		attachment.upUser = (User) springSecurityService.getCurrentUser()
		attachment.save(flush:true)
		
		json["result"] = "true"
		json["fileId"] = attachment.id
		json["fileName"] = name
		render json as JSON
	}
	
	def addComment ={
		def json=[:]
		def dsj = Dsj.get(params.id)
		def user = User.get(params.userId)
		if(dsj){
			def comment = new DsjComment()
			comment.user = user
			comment.status = dsj.status
			comment.content = params.dataStr
			comment.dsj = dsj
			
			if(comment.save(flush:true)){
				json["result"] = true
			}else{
				comment.errors.each{
					println it
				}
				json["result"] = false
			}
			
		}else{
			json["result"] = false
		}
		
		render json as JSON
	}
	
	def getCommentLog ={
		def model =[:]
		def dsj = Dsj.get(params.id)
		if(dsj){
			def logs = DsjComment.findAllByDsj(dsj,[ sort: "createDate", order: "desc"])
			model["log"] = logs
		}
		
		render(view:'/share/commentLog',model:model)
	}
	def getFlowLog={
		def model =[:]
		def dsj = Dsj.get(params.id)
		if(dsj){
			def logs = DsjLog.findAllByDsj(dsj,[ sort: "createDate", order: "asc"])
			model["log"] = logs
		}
		
		render(view:'/share/flowLog',model:model)
	}
	def dsjDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def dsj = Dsj.get(it)
				if(dsj){
					dsj.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def dsjAdd ={
		redirect(action:"dsjShow",params:params)
	}
	def dsjShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def dsj = new Dsj()
		if(params.id){
			dsj = Dsj.get(params.id)
		}else{
			model.companyId = params.companyId
		}
		model["user"]=user
		model["company"] = company
		model["dsj"] = dsj
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			fa.readOnly = []
		}
		model["fieldAcl"] = fa
		
		render(view:'/dsj/dsj',model:model)
	}

	def dsjGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = dsjService.getDsjListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = dsjService.getDsjListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = dsjService.getDsjCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	def dsjConfig = {
		def model = [:]
		def user = springSecurityService.getCurrentUser()
		
		def dsjConfig = DsjConfig.findWhere(company:user.company)
		if(dsjConfig==null) {
			dsjConfig = new DsjConfig()
			
			Calendar cal = Calendar.getInstance();
			dsjConfig.nowYear = cal.get(Calendar.YEAR)
			dsjConfig.frontYear = dsjConfig.nowYear -1
			
			model.companyId = user.company.id
		}else{
			model.companyId = dsjConfig.company.id
		}
		model.dsjConfig = dsjConfig
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
			fa.readOnly = ["nowYear","nowSN","nowCancel","frontYear","frontSN","frontCancel"]
		}else{
//			fa.readOnly = ["nowCancel","frontCancel"]
		}
		model["fieldAcl"] = fa
		
		render(view:'/dsj/dsjConfig',model:model)
	}
	def dsjConfigSave ={
		def json=[:]
		def dsjConfig = new DsjConfig()
		if(params.id && !"".equals(params.id)){
			dsjConfig = DsjConfig.get(params.id)
		}
		dsjConfig.properties = params
		dsjConfig.clearErrors()
		dsjConfig.company = Company.get(params.companyId)
		
		dsjConfig.nowCancel = params.dsjConfig_nowCancel
		dsjConfig.frontCancel = params.dsjConfig_frontCancel
		
		if(dsjConfig.save(flush:true)){
			json["result"] = true
			json["dsjConfigId"] = dsjConfig.id
			json["companyId"] = dsjConfig.company.id
		}else{
			dsjConfig.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
    def index() { }
}
