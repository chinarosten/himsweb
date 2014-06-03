package com.rosten.app.workflow

import org.activiti.engine.history.HistoricProcessInstanceQuery
import org.activiti.engine.history.HistoricTaskInstance
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior
import org.activiti.engine.impl.persistence.entity.ExecutionEntity
import org.activiti.engine.impl.pvm.PvmActivity
import org.activiti.engine.impl.pvm.PvmTransition
import org.activiti.engine.impl.pvm.process.ActivityImpl
import org.activiti.engine.impl.task.TaskDefinition
import org.activiti.engine.repository.ProcessDefinition
import org.activiti.engine.runtime.Execution
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
	def historyService
	
	//查询Execution操作列表
	def getExecutionList ={
		List<Execution> _list = runtimeService.createExecutionQuery().list()
		return _list
	}
	//获取执行过的所有任务节点的列表  必须指定流程实例才精确
	def getAllExecuteTaskNodeList={processInstanceId->
		List<HistoricTaskInstance> _list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list()
		return _list
	}
	//获取指定候选人对应的任务  配置为  activiti:candidateGroups="用户组"
	def getCandidateGroupTasks ={groupname->
		List<Task> _list = taskService.createTaskQuery().taskCandidateGroup(groupname).list()
		return _list
		
	}
	//获取指定候选人对应的任务  配置为   activiti:candidateUsers="名称"
	def getCandidateUserTasks={username ->
		List<Task> _list = taskService.createTaskQuery().taskCandidateUser(username).list()
		return _list
	}
	//获取指定人的所有任务
	def getPersonalTasks ={username ->
		List<Task> _list = taskService.createTaskQuery().taskAssignee(username).list()
		return _list
	}
	//通过任务id获取下一节点任务
	def getNextTaskDefinition={taskid ->
		
		//获取任务
		Task task = taskService.createTaskQuery().taskId(taskid).singleResult()
		
		//根据当前任务获取当前流程的流程定义，然后根据流程定义获得所有的节点
		ProcessDefinition processDefinition = repositoryService.getProcessDefinition(task.getProcessDefinitionId())
		def activitiList = processDefinition.getActivities()
		
		//根据任务获取当前流程执行ID，执行实例以及当前流程节点的ID
		String excId = task.getExecutionId()
		ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(excId).singleResult()
		String activitiId = execution.getActivityId()
		
		for(def activitiEntity:activitiList){
			String _id = activitiEntity.getId()
			if(activitiId.equals(_id)){
				return nextTaskDefinition(activitiEntity, _id)
			}
		}
		return null
	}
	
	//迭代循环获取下一节点任务
	private def nextTaskDefinition ={activityEntity ,activityId ->
		if("userTask".equals(activityEntity.getProperty("type")) && !activityId.equals(activityEntity.getId())){
			TaskDefinition taskDefinition = ((UserTaskActivityBehavior)activityEntity.getActivityBehavior()).getTaskDefinition()
			return taskDefinition
		}else{
			def outTransitions = activityEntity.getOutgoingTransitions()
			for(PvmTransition tr:outTransitions){
				PvmActivity ac = tr.getDestination() //获取线路的终点节点
				if("exclusiveGateway".equals(ac.getProperty("type"))){
					def outTransitionsTemp = ac.getOutgoingTransitions();
					if(outTransitionsTemp.size() == 1){
						return nextTaskDefinition((ActivityImpl)outTransitionsTemp.get(0).getDestination(), activityId)
					}else if(outTransitionsTemp.size() > 1){
						for(PvmTransition tr1 : outTransitionsTemp){
							
							//判断当前节点是否已经审核通过，待完善。。。。。。。。
							Object s = tr1.getProperty("conditionText");
							
							return nextTaskDefinition((ActivityImpl)tr1.getDestination(), activityId)
							
						}
					}
				}else if("userTask".equals(ac.getProperty("type"))){
					return ((UserTaskActivityBehavior)((ActivityImpl)ac).getActivityBehavior()).getTaskDefinition();
				}
			}
			return null
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
	//通过流程id获取下一任务，特定串行流程
	def getTaskByFlow ={ flowId ->
		def tasks = getTasksFlow(flowId)
		if(tasks!=null && tasks.size()!=0){
			return tasks[0]
		}else{
			return null
		}
	}
	
	//通过流程id获取流程实例
	def getProcessIntance = {flowId ->
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(flowId).singleResult()
		return processInstance
	}
	
	//通过流程id获取相关任务
	def getTasksByFlow ={flowId ->
		List<Task> tasks = taskService.createTaskQuery().processInstanceId(flowId).active().orderByTaskCreateTime().desc().list()
		return tasks
	}
	
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
