package com.rosten.app.workflow

import java.io.InputStream;
import java.util.Collections;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.cmd.GetBpmnModelCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;

class WorkFlowDiagramCmdService implements Command<InputStream>{

	protected String processDefinitionId

	def ProcessDefinitionDiagramCmd(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	@Override
	public InputStream execute(CommandContext commandContext) {
		ProcessDefinitionEntity processDefinitionEntity = Context.getProcessEngineConfiguration().getProcessDefinitionCache().get(processDefinitionId);

		GetBpmnModelCmd getBpmnModelCmd = new GetBpmnModelCmd(processDefinitionId);
		BpmnModel bpmnModel = getBpmnModelCmd.execute(commandContext);

		InputStream is = ProcessDiagramGenerator.generateDiagram(bpmnModel, "png", Collections.EMPTY_LIST);

		return is;
	}
	def serviceMethod() {
	}
}
