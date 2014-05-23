package com.rosten.app.workflow

import org.activiti.engine.history.HistoricProcessInstanceQuery
import org.activiti.engine.repository.ProcessDefinition
import org.activiti.engine.runtime.ProcessInstance
import org.activiti.engine.runtime.ProcessInstanceQuery
import org.activiti.engine.task.Task
import org.activiti.engine.task.TaskQuery
import org.activiti.engine.ActivitiException

class WorkFlowService {
	def identityService
	def runtimeService
	def taskService
	def repositoryService
	
	
	//通过流程定义id获取流程定义信息，目前只供模块编辑界面使用
	def getProcessDefinition ={flowDefinitionId ->
		ProcessDefinition processDefinition = repositoryService.getProcessDefinition(flowDefinitionId)
		return ["id":processDefinition.id,"version":processDefinition.version,"name":processDefinition.name,"key":processDefinition.key]
	}
	
	//获取结束的流程实例
	def getFinishedProcessInstaces ={flowDefinitionId ->
		HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(flowDefinitionId).finished().orderByProcessInstanceEndTime().desc();
		query.list().each{
			
		}
		
	}
	
	//获取待办任务
	def getTodoTasks ={flowDefinitionId,userId ->
		def tasks =[]
		
		// 根据当前人的ID查询
		TaskQuery todoQuery = taskService.createTaskQuery().processDefinitionKey(flowDefinitionId).taskAssignee(userId).active().orderByTaskId().desc().orderByTaskCreateTime().desc();
		
		// 根据当前人未签收的任务
		TaskQuery claimQuery = taskService.createTaskQuery().processDefinitionKey(flowDefinitionId).taskCandidateUser(userId).active().orderByTaskId().desc()
		
		tasks += todoQuery.list()
		tasks += claimQuery.list()
		
		tasks.unique()
		
		return tasks
	}
	
	//删除相关流程实例通过流程定义
	def deleteAllFlowInstance ={flowDefinitionId,reason ->
		ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().processDefinitionKey(flowDefinitionId).active().orderByProcessInstanceId().desc()
		List<ProcessInstance> _list = query.list()
		for (ProcessInstance processInstance : _list) {
			runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(),reason)
		}
	}
	
	//删除流程实例
	def deleteFlowInstance ={flowId ->
		
	}
	
	//通过流程id获取下一任务，特定串行流程
	def getTaskByFlow ={ flowId ->
		def tasks = getTasksFlow(flowId)
		if(tasks!=null && tasks.size()!=0){
			return tasks[0]
		}else{
			return null
		}
	}
	
	//通过流程id获取相关任务
	def getTasksByFlow ={flowId ->
		List<Task> tasks = taskService.createTaskQuery().processInstanceId(flowId).active().orderByTaskCreateTime().desc().list()
		return tasks
	}
	
	//创建流程实例
	def addFlowInstance ={flowDefinitionId,creater,businessKey,variables ->
		try {
			// 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
			identityService.setAuthenticatedUserId(creater)
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(flowDefinitionId, businessKey, variables);
			return processInstance
		}catch (ActivitiException e) {
			e.printStackTrace()
			return "suspend"
		}
	}
	
    def serviceMethod() {

    }
}
