<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>资源管理</title>
	<script type="text/javascript">
		require([
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/FilteringSelect",
		 		"dijit/form/SimpleTextarea",
		 		"dijit/form/Button"
		     	]
 		);
		
	</script>
</head>
<body>
	<div style="text-Align:center">
        <div class="rosten_form" style="margin:0 0 0 0;width:550px">
            <fieldset class="fieldset-form">
                <legend class="tableHeader">资源配置</legend>
                <table class="tableData" width="500" border="0" align="center">
                    <tbody>
                        <tr>
                            <td>
                                <div align="right" >
                                   <span style="color:red">*&nbsp;</span> 所属模块：
                                </div>
                            </td>
                            <td colspan="3">
                                <select id="modelId" data-dojo-type="dijit/form/FilteringSelect"
                                	data-dojo-props = 'name:"modelId",style:{width:"182px",marginLeft:"10px",float:"left"},trim:true,required:true'
                                >
                                	<g:each var="model" in="${modelList}">
                                		<option value="${model.id }">${model.modelName }</option>
                                	</g:each>
					   			</select>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div align="right" >
                                   <span style="color:red">*&nbsp;</span> 资源名称：
                                </div>
                            </td>
                            <td colspan="3">
                                <input id="resourceName" data-dojo-type="dijit/form/ValidationTextBox" 
                                	data-dojo-props = 'name:"resourceName",
                                		"class":"input",
                                		style:{width:"400px",marginLeft:"1px"},
                                		promptMessage:"请正确输入资源名称...",
                                		trim:true,
                                		required:true
                                '/>
                            </td>
                        </tr>
						<tr>
                           <td>
                                <div align="right" >
                                    <span style="color:red">*&nbsp;</span>图标地址：
                                </div>
                            </td>
                            <td colspan="3">
                                <input id="imgUrl" data-dojo-type="dijit/form/ValidationTextBox" 
                                	data-dojo-props = 'name:"imgUrl",
                                		"class":"input",
                                		style:{width:"400px",marginLeft:"1px"},
                                		promptMessage:"请正确输入图标地址...",
                                		trim:true,
                                		required:true
                                '/>
                            </td>
                        </tr>
						<tr>
                           <td>
                                <div align="right" >
                                    <span style="color:red">*&nbsp;</span>导航地址：
                                </div>
                            </td>
                            <td colspan="3">
                                <input id="url" data-dojo-type="dijit/form/ValidationTextBox" 
                                	data-dojo-props = 'name:"url",
                                		"class":"input",
                                		style:{width:"400px",marginLeft:"1px"},
                                		promptMessage:"请正确输入导航地址...",
                                		trim:true,
                                		required:true
                                '/>
                            </td>
                        </tr>
                        <tr>
                        	<td>
                                <div align="right" >内容描述：</div>
                            </td>
							<td colspan="3">
								<textarea id="description" data-dojo-type="dijit/form/SimpleTextarea" 
    								data-dojo-props = 'name:"url",
                                		style:{width:"400px",marginLeft:"1px"},
                                		trim:true,
                                		required:true
    							'>
    							</textarea>
    						</td>
                        </tr>
                        <tr>
						    <td>&nbsp;</td>
						    <td colspan="3">
						    	<button data-dojo-props='onClick:function(){systemtool_addResource_submit()}' data-dojo-type="dijit/form/Button">提交</button>
						    	<button data-dojo-props='onClick:function(){rosten.kernel.hideRostenShowDialog()}' data-dojo-type="dijit/form/Button">取消</button>
						    </td>
						 </tr>
                    </tbody>
                </table>
            </fieldset>
		</div>
	</div>
</body>
</html>