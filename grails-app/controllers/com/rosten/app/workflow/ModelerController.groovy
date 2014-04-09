package com.rosten.app.workflow

import org.activiti.engine.repository.Model
import grails.converters.JSON
import com.rosten.app.util.Util

class ModelerController {
	def repositoryService
	
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
			_gridHeader << ["name":"流程名称","width":"100px","colIdx":1,"field":"key"]
			_gridHeader << ["name":"流程名称","width":"auto","colIdx":2,"field":"name"]
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
    def index() { }
}
