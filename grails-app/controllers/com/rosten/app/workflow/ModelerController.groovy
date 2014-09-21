package com.rosten.app.workflow

import org.activiti.bpmn.converter.BpmnXMLConverter
import org.activiti.bpmn.model.BpmnModel
import org.activiti.editor.language.json.converter.BpmnJsonConverter
import org.activiti.engine.repository.Deployment
import org.activiti.engine.repository.DeploymentQuery
import org.activiti.engine.repository.Model
import org.activiti.engine.repository.ProcessDefinition
import org.activiti.engine.repository.ProcessDefinitionQuery
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.node.ObjectNode
import grails.converters.JSON
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader
import com.rosten.app.util.Util
import com.rosten.app.system.Company
import org.activiti.engine.ActivitiException
import com.rosten.app.util.SystemUtil

class ModelerController {
	def repositoryService
	
	def addModelUpload ={
		def model =[:]
		model["companyId"] = params.companyId
		render(view:'/modeler/fileUpload',model:model)
	}
	def uploadModel = {
		def ostr
		try {
			SystemUtil sysUtil = new SystemUtil()
			def company = Company.get(params.companyId)
			
			//获取附件信息
			def f = request.getFile("inputFile")
			if (f.empty) {
				json["result"] = "blank"
				render json as JSON
				return
			}
			
			//转换xml
			InputStreamReader inStream = new InputStreamReader(f.getInputStream(), "UTF-8");
			XMLInputFactory xif = XMLInputFactory.newInstance();
			XMLStreamReader xtr = xif.createXMLStreamReader(inStream);
			BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
			
			//创建model
			Model model = repositoryService.newModel();
			
			def processName = bpmnModel.getMainProcess().getName();
			def processKey = company.shortName + "_" + bpmnModel.getMainProcess().getId();
			
			model.setName(processName);
			model.setKey(processKey);
			model.setCategory(params.companyId)

			def modelObjectNode = [:]
			modelObjectNode["name"] = processName
			modelObjectNode["revision"] = 1
			modelObjectNode["key"] = processKey
			model.setMetaInfo(modelObjectNode.toString());

			repositoryService.saveModel(model);
			
			BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
			ObjectNode editorNode = jsonConverter.convertToJson(bpmnModel);
			repositoryService.addModelEditorSource(model.getId(), editorNode.toString().getBytes("utf-8"));
			
			ostr ="<script>var _parent = window.parent;_parent.rosten.alert('导入成功').queryDlgClose=function(){_parent.rosten.kernel.hideRostenShowDialog();_parent.rosten.kernel.refreshGrid();}</script>"
		}catch (Exception e) {
			ostr ="<script>window.parent.rosten.alert('导入失败');</script>"
		}
		
		render ostr
	}
	def flowSelect ={
		def flowList =[]
		def deploymentList = repositoryService.createDeploymentQuery().deploymentCategory(params.companyId).orderByDeploymenTime().desc().list()
		
		deploymentList.each{
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(it.getId()).singleResult()
			def json=[:]
			json["id"] = processDefinition.getId()
			json["name"] = it.name + "(" + processDefinition.version + ")"
			flowList << json
		}
		render flowList as JSON
	}
	def flowUpdateState ={
		def json=[:]
		params.id.split(",").each{
			try{
				if ("active".equals(params.status)) {
					repositoryService.activateProcessDefinitionById(it, true, null);
				}else{
					repositoryService.suspendProcessDefinitionById(it, true, null);
				}
			}catch(ActivitiException e){
				println "rosten....截获流程错误信息......"
			}
		}
		json["result"] = "true"
		render json as JSON
	}
	def flowExport ={
		ProcessDefinition processDefinition = repositoryService.getProcessDefinition(params.id)
		String deploymentId = processDefinition.getDeploymentId();
		
		def resourceName = processDefinition.diagramResourceName
		if(!"image".equals(params.type)){
			resourceName = processDefinition.resourceName
		}
		
		InputStream resourceAsStream = repositoryService.getResourceAsStream(deploymentId, resourceName);
		
		byte[] b = new byte[1024];
		int len = -1;
		while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
		  response.outputStream.write(b, 0, len);
		}
		response.outputStream.flush()
		response.outputStream.close()
	}
	
	def flowDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				ProcessDefinition processDefinition = repositoryService.getProcessDefinition(it)
				String deploymentId = processDefinition.getDeploymentId();
				repositoryService.deleteDeployment(deploymentId, true);
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def flowDefinedGrid ={
		def company = Company.get(params.companyId)
		def json=[:]
		if(params.refreshHeader){
			def _gridHeader =[]

			_gridHeader << ["name":"序号","width":"26px","colIdx":0,"field":"rowIndex"]
			_gridHeader << ["name":"流程id","width":"120px","colIdx":1,"field":"id"]
			_gridHeader << ["name":"部署id","width":"60px","colIdx":2,"field":"deploymentId"]
			_gridHeader << ["name":"流程名称","width":"auto","colIdx":3,"field":"name"]
			_gridHeader << ["name":"版本号","width":"40px","colIdx":4,"field":"version"]
			_gridHeader << ["name":"状态","width":"40px","colIdx":5,"field":"status"]
			_gridHeader << ["name":"部署时间","width":"130px","colIdx":6,"field":"deploymentTime"]

			json["gridHeader"] = _gridHeader
		}
		def totalNum = 0
		if(params.refreshData){
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)

			def offset = (nowPage-1) * perPageNum
			def max  = perPageNum

			def _json = [identifier:'id',label:'name',items:[]]
			
			def deploymentList = repositoryService.createDeploymentQuery().deploymentCategory(params.companyId).orderByDeploymenTime().desc().listPage(offset, offset+max)
			totalNum = deploymentList.size()
			
			def idx = 0
			deploymentList.each{
				ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(it.getId()).singleResult()
				
				def sMap =[:]
				sMap["rowIndex"] = idx+1
				sMap["id"] = processDefinition.id
				sMap["deploymentId"] = it.id
				sMap["name"] = it.name
				sMap["version"] = processDefinition.version
				
				if(processDefinition.isSuspended()){
					sMap["status"] = "已挂起"
				}else{
					sMap["status"] = "正常"
				}
				
				sMap["deploymentTime"] = it.deploymentTime
				
				_json.items+=sMap
				
				idx += 1
			}

			json["gridData"] = _json
		}
		
		
		
		if(params.refreshPageControl){
			json["pageControl"] = ["total":totalNum.toString()]
		}
		render json as JSON
	}
	
	def create ={
		def json=[:]
		try{
			def company = Company.get(params.companyId)
			
			Model model = repositoryService.newModel();
			model.setName(params.name);
			model.setKey(company.shortName + "_" + params.key);
			model.setCategory(params.companyId)

			def modelObjectNode = [:]
			modelObjectNode["name"] = params.name
			modelObjectNode["revision"] = 1
			modelObjectNode["key"] = company.shortName + "_" + params.key
			modelObjectNode["description"] = params.description

			model.setMetaInfo(modelObjectNode.toString());

			repositoryService.saveModel(model);

			json["result"] = "true"
			json["modelId"] = model.getId()
		}catch (Exception e) {
			json["result"] = "false"
		}

		render json as JSON
	}

	def open ={
		def root=[:]
		Model model = repositoryService.getModel(params.id);
		
		root["modelId"] = model.getId()
		root["name"] = model.getName()
		root["revision"] = model.getVersion()
		root["key"] = model.getKey()
		
		def _description = Util.strLeft(Util.strRight(model.getMetaInfo(), "description"), "]")
		root["description"] = Util.strRight(_description,":")

		byte[] bytes = repositoryService.getModelEditorSource(model.getId());

		if (bytes != null) {
			String modelEditorSource = new String(bytes, "utf-8");
			root["model"] = JSON.parse(modelEditorSource)
		} else {
			def modelNode = [:]
			modelNode["id"] = "canvas"
			modelNode["resourceId"] = "canvas"

			def stencilSetNode =[:]
			stencilSetNode["namespace"] = "http://b3mn.org/stencilset/bpmn2.0#";
			modelNode["stencilset"] = stencilSetNode

			root["model"] = modelNode
		}

		render root as JSON
	}

	def add ={
		def model = [:]
		render(view:'/modeler/modelAdd',model:model)
	}
	def save ={
		def json=[:]
		Model model = repositoryService.getModel(params.id);
		model.setName(params.name);
		
		repositoryService.saveModel(model);
		repositoryService.addModelEditorSource(model.getId(),params.json_xml.getBytes("utf-8"));

		json["modelId"] = model.getId()
		render json as JSON
	}
	def modelerGrid ={
		def json=[:]
		if(params.refreshHeader){
			def _gridHeader =[]

			_gridHeader << ["name":"序号","width":"26px","colIdx":0,"field":"rowIndex"]
			_gridHeader << ["name":"流程id","width":"120px","colIdx":1,"field":"key"]
			_gridHeader << ["name":"流程名称","width":"auto","colIdx":2,"field":"name","formatter":"modeler_formatTopic"]
			_gridHeader << ["name":"版本号","width":"60px","colIdx":3,"field":"version"]
			_gridHeader << ["name":"创建时间","width":"130px","colIdx":4,"field":"createTime"]
			_gridHeader << ["name":"更新时间","width":"130px","colIdx":5,"field":"lastUpdateTime"]

			json["gridHeader"] = _gridHeader
		}
		def totalNum = 0
		if(params.refreshData){
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)

			def offset = (nowPage-1) * perPageNum
			def max  = perPageNum

			def _json = [identifier:'id',label:'name',items:[]]

			def _dataList = repositoryService.createModelQuery().modelCategory(params.companyId).list();
			totalNum = _dataList.size()
			_dataList.eachWithIndex{ item,idx->
				if(idx>=offset && idx <offset+max){
					def sMap =[:]
					sMap["rowIndex"] = idx+1
					sMap["id"] = item.id
					sMap["key"] = item.key
					sMap["name"] = item.name
					sMap["version"] = item.version
					sMap["createTime"] = item.createTime
					sMap["lastUpdateTime"] = item.lastUpdateTime

					_json.items+=sMap
				}
			}

			json["gridData"] = _json
		}
		if(params.refreshPageControl){
			json["pageControl"] = ["total":totalNum.toString()]
		}
		render json as JSON
	}
	
	def deploy ={
		def json=[:]
		try {
			Model modelData = repositoryService.getModel(params.id);
			ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
			byte[] bpmnBytes = null;
			
			BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
			bpmnBytes = new BpmnXMLConverter().convertToXML(model);
	
			String processName = modelData.getName() + ".bpmn20.xml";
			Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).category(params.companyId).addString(processName, new String(bpmnBytes)).deploy();
			
			json["result"] = true
			json["deployId"] = deployment.getId()
		}catch (Exception e) {
			json["result"] = false
		}
		
		render json as JSON
	}
	def export ={
		try {
			Model modelData = repositoryService.getModel(params.id);
			BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
			JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
			BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
			BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
			byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
			
			String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
			ByteArrayInputStream inputStream = new ByteArrayInputStream(bpmnBytes);
			
			response.setHeader("Content-disposition", "attachment; filename=" + filename)
			response.contentType = ""
			
			def out = response.outputStream
			byte[] buffer = new byte[1024]
			int i = -1
			while ((i = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, i)
			}
			out.flush()
			out.close()
			inputStream.close()
			
		}catch (Exception e) {
			println "error......"
		}
	}
	def modelerDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				repositoryService.deleteModel(it);
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def index() {
	}
}
