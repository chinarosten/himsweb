<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>业务流程管理</title>
	<script type="text/javascript">
		require(["dojo/parser",
		 		"dojo/_base/kernel",
		 		"dijit/registry",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/SimpleTextarea",
		 		"dijit/form/Button",
		     	"rosten/widget/ActionBar",
		     	"rosten/widget/MultiSelectDialog",
		     	"rosten/app/SystemApplication"],
			function(parser,kernel,registry){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}"});
					rosten.cssinit();
				});

				flowBusiness_add = function(){
					var flowCode = registry.byId("flowCode");
					if(!flowCode.isValid()){
						rosten.alert("业务流程代码不正确！").queryDlgClose = function(){
							flowCode.focus();
						};
						return;
					}
					var modelName = registry.byId("modelName");
					if(!modelName.isValid()){
						rosten.alert("所属模块不正确！").queryDlgClose = function(){
						};
						return;
					}
					var content = {};
					rosten.readSync(rosten.webPath + "/modeler/flowBusinessSave",content,function(data){
						if(data.result=="true"){
							rosten.alert("保存成功！").queryDlgClose= function(){
								page_quit();	
							};
						}else{
							rosten.alert("保存失败!");
						}
					},function(error){
						rosten.alert("系统错误，请通知管理员！");
					},"rosten_form");
				}
		});
		
		
		
    </script>
</head>
<body>
	<div class="rosten_action">
		<div data-dojo-type="rosten.widget.ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'modelerAction',action:'flowBusinessForm',params:[userid:user?.id])}"'></div>
	</div>
		<div style="text-Align:center">
        <form class="rosten_form" id="rosten_form" onsubmit="return false;" style="text-align:left;">
			
        	<input  data-dojo-type="dijit.form.ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${flowBusiness?.id }"' />
        	<input  data-dojo-type="dijit.form.ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
            <fieldset class="fieldset-form">
                <legend class="tableHeader">业务流程</legend>
                <table class="tableData" style="width:550px">
                    <tbody>
                       <tr>
                            <td>
                                <div align="right" >
                                  <span style="color:red">*&nbsp;</span>业务流程代码：
                                </div>
                            </td>
                            <td>
                                <input id="flowCode" data-dojo-type="dijit.form.ValidationTextBox" 
                                	data-dojo-props='name:"flowCode",
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入业务流程代码...",
              							value:"${flowBusiness?.flowCode}"
                               '/>
                            </td>
                        </tr>
                       <tr>
                            <td>
                                <div align="right" >业务流程名称：
                                </div>
                            </td>
                            <td>
                                <input id="flowName" data-dojo-type="dijit.form.ValidationTextBox" 
                                	data-dojo-props='name:"flowName",
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
                                		promptMessage:"请正确输入业务流程名称...",
              							value:"${flowBusiness?.flowName}"
                               '/>
                            </td>
                        </tr>
                        <tr>
                        	<td>
                                <div align="right" > <span style="color:red">*&nbsp;</span>所属模块：</div>
                            </td>
                             <td>
                             	<input id="modelName" data-dojo-type="dijit/form/ValidationTextBox" 
                                	data-dojo-props='name:"modelName",
                                		"class":"input",
                                		style:"width:400px",
                                		trim:true,
                                		required:true,
                                		disabled:true,
              							value:"${flowBusiness?.model?.modelName}"
                                '/>
                                <input id="modelId" data-dojo-type="dijit/form/ValidationTextBox" data-dojo-props='name:"modelId",value:"${flowBusiness?.model?.id }",style:{display:"none"}'/>
								<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){selectModel("${company?.id }")}'>选择</button>
    						</td>
                        </tr>
                        <tr>
                        	<td>
                                <div align="right" >关联流程：</div>
                            </td>
                             <td><input id="allowRelationFlow" data-dojo-type="dijit.form.ValidationTextBox"
                   					data-dojo-props='"class":"input",
                   						style:"width:400px",
                   						trim:true,
                   						name:"relationFlowName",
                   						value:"${allowRelationFlow }"
                   				'/>
                   				<g:hiddenField name="relationFlow" value="${flowBusiness.relationFlow }" />
								<button data-dojo-type="dijit.form.Button" 
									data-dojo-props = 'onClick:function(){selectRelationFlow("${createLink(controller:'modeler',action:'flowSelect',params:[companyId:company?.id])}")}'
								>选择</button>
    						</td>
                        </tr>					
						<tr>
                        	<td>
                                <div align="right" >内容描述：</div>
                            </td>
                             <td colspan="3">
                             	<textarea id="description" data-dojo-type="dijit.form.SimpleTextarea"
                             		data-dojo-props='name:"description",
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
              							value:"${flowBusiness?.description}"
                                '></textarea>
    						</td>
                        </tr>
                    </tbody>
                </table>
            </fieldset>
	</form>
	</div>
</body>
</html>