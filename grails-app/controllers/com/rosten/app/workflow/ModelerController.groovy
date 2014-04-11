package com.rosten.app.workflow

import org.activiti.bpmn.converter.BpmnXMLConverter
import org.activiti.bpmn.model.BpmnModel
import org.activiti.editor.language.json.converter.BpmnJsonConverter
import org.activiti.engine.repository.Deployment
import org.activiti.engine.repository.Model
import org.activiti.engine.repository.ProcessDefinition
import org.activiti.engine.repository.ProcessDefinitionQuery
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.node.ObjectNode
import grails.converters.JSON
import com.rosten.app.util.Util

class ModelerController {
	def repositoryService
	
	
	
	
	def flowDefinedGrid ={
		def json=[:]
		if(params.refreshHeader){
			def _gridHeader =[]

			_gridHeader << ["name":"序号","width":"26px","colIdx":0,"field":"rowIndex"]
			_gridHeader << ["name":"流程id","width":"100px","colIdx":1,"field":"id"]
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
			
			ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().orderByDeploymentId().desc();
			totalNum = processDefinitionQuery.count()
			
			List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(offset, offset+max);
			
			def idx = 0
			for (ProcessDefinition processDefinition : processDefinitionList) {
				String deploymentId = processDefinition.getDeploymentId();
				Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
				
				def sMap =[:]
				sMap["rowIndex"] = idx+1
				sMap["id"] = processDefinition.id
				sMap["deploymentId"] = deployment.id
				sMap["name"] = deployment.name
				sMap["version"] = processDefinition.version
				
				if(processDefinition.isSuspended()){
					sMap["status"] = "已挂起"
				}else{
					sMap["status"] = "正常"
				}
				
				sMap["deploymentTime"] = deployment.deploymentTime
				
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
			Model model = repositoryService.newModel();
			model.setName(params.name);
			model.setKey(params.key);

			def modelObjectNode = [:]
			modelObjectNode["name"] = params.name
			modelObjectNode["revision"] = 1
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
			_gridHeader << ["name":"流程idkey","width":"100px","colIdx":1,"field":"key"]
			_gridHeader << ["name":"流程名称","width":"auto","colIdx":2,"field":"name"]
			_gridHeader << ["name":"版本号","width":"60px","colIdx":3,"field":"version"]
			_gridHeader << ["name":"状态","width":"40px","colIdx":4,"field":"status"]
			_gridHeader << ["name":"创建时间","width":"130px","colIdx":5,"field":"createTime"]
			_gridHeader << ["name":"更新时间","width":"130px","colIdx":6,"field":"lastUpdateTime"]

			json["gridHeader"] = _gridHeader
		}
		def totalNum = 0
		if(params.refreshData){
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)

			def offset = (nowPage-1) * perPageNum
			def max  = perPageNum

			def _json = [identifier:'id',label:'name',items:[]]

			def _dataList = repositoryService.createModelQuery().list();
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
					
					if(item.getDeploymentId()){
						sMap["status"] = "已部署"
					}else{
						sMap["status"] = "未部署"
					}

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
			Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).addString(processName, new String(bpmnBytes)).deploy();
			
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
	def index() {
	}
}
