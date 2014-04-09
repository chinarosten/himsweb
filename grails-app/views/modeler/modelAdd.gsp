<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>流程管理</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>
  
<body>
	<div style="text-Align:center">
        <div class="rosten_form" style="width:400px;text-align:left">
            <fieldset class="fieldset-form">
                <legend class="tableHeader">添加流程</legend>
                <table class="tableData">
                    <tbody>
						<tr>
                            <td width="80">
                                <div align="right">流程名称：</div>
                            </td>
                           <td  width="220">
                                <input id="workFlowName" data-dojo-type="dijit/form/ValidationTextBox"
                                	data-dojo-props='name:"workFlowName",
                                		"class":"input",
                                		trim:true,
                                		required:true
                                '/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div align="right">流程idkey：</div>
                            </td>
                           <td>
                                <input id="workFlowKey" data-dojo-type="dijit/form/ValidationTextBox"
                                	data-dojo-props='name:"workFlowKey",
                                		"class":"input",
                                		trim:true,
                                		required:true
                                '/>
                            </td>
                        </tr>
                        <tr>
                           <td>
                                <div align="right" >描述：</div>
                            </td>
                            <td>
                            	<textarea id="description" data-dojo-type="dijit/form/SimpleTextarea"
                             		data-dojo-props='name:"description",
                                		"class":"input",
                                		trim:true
                                '></textarea>
                            </td>
                        </tr>
						<tr>
							<td></td>
							<td>
								<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){create_modeler()}'>确定</button>
								<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){rosten.kernel.hideRostenShowDialog()}'>取消</button>
								
							</td>
						</tr>
                    </tbody>
                </table>
				
				
            </fieldset>
		</div>
	</div>
</body>
</html>
