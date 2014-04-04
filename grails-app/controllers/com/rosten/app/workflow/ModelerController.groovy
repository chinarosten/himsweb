package com.rosten.app.workflow

import org.activiti.engine.repository.Model
import grails.converters.JSON

class ModelerController {
	def repositoryService
	
	def open ={
		
		def modelId = params.id
		
		println repositoryService
		
		Model model = repositoryService.getModel(modelId);
		
		if (model == null) {
			model = repositoryService.newModel();
			repositoryService.saveModel(model);
			modelId = model.getId();
		}
		
		def root=[:]
		
		root["modelId"] = model.getId()
		root["name"] = "hahaname"
		root["revision"] = 1
		root["description"] = "hahadescription"
		
		byte[] bytes = repositoryService.getModelEditorSource(model.getId());
		
		if (bytes != null) {
			String modelEditorSource = new String(bytes, "utf-8");
	
			Map modelNode = jsonMapper.fromJson(modelEditorSource, Map.class);
			
			root["model"] = [:]
			
		} else {
		
			def modeNode = [:]
			
			modeNode["id"] = "canvas"
			modeNode["resourceId"] = "canvas"
			
			Map modelNode = new HashMap();
			modelNode.put("id", "canvas");
			modelNode.put("resourceId", "canvas");
	
			Map stencilSetNode = new HashMap();
			stencilSetNode.put("namespace",
					"http://b3mn.org/stencilset/bpmn2.0#");
			modelNode.put("stencilset", stencilSetNode);
			
			def _json = root as JSON
			
			model.setMetaInfo("test");
			model.setName("name");
			model.setKey("key");
	
			root["model"] = modelNode
			
		}
	
		render root as JSON
		
		
		
	}
	
	def save ={
		
		def json=[:]
		
		Model model = repositoryService.getModel(params.id);
		model.setName(params.name);
		repositoryService.saveModel(model);
		repositoryService.addModelEditorSource(model.getId(),params.json_xml.getBytes("utf-8"));
	
		render json as JSON
		
	}
	
    def index() { }
}
