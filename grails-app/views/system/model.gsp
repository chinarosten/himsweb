<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rostenApp" />
    <title>模块管理</title>
    <r:jsLoad dir="js/app" file="SystemApplication.js"/>
	<script type="text/javascript">
		dojo.require("dijit.form.ValidationTextBox");
		dojo.require("dijit.form.SimpleTextarea");
		dojo.require("dijit.form.Button");
		dojo.require("rosten.widget.ActionBar");
		dojo.require("rosten.widget.MultiSelectDialog");

		dojo.addOnLoad(function(){
			rosten.cssinit();
		});

		model_add = function(){
			var modelName = dijit.byId("modelName");
			if(!modelName.isValid()){
				rosten.alert("模块名称不正确！").queryDlgClose = function(){
					modelName.focus();
				};
				return;
			}
			/*
			var modelUrl = dijit.byId("modelUrl");
			if(!modelUrl.isValid()){
				rosten.alert("链接导航路径不正确！").queryDlgClose = function(){
					modelUrl.focus();
				};
				return;
			}*/
			var content = {};
			rosten.readerByFormSync(rosten.webPath + "/system/modelSave","rosten_form",content,function(data){
				if(data.result=="true"){
					rosten.alert("保存成功！").queryDlgClose= function(){
						page_quit();	
					};
				}else{
					rosten.alert("保存失败!");
				}
			});
		}
		
    </script>
</head>
<body>
	<div class="rosten_action">
		<div data-dojo-type="rosten.widget.ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'modelForm',params:[userid:user?.id])}"'></div>
	</div>
		<div style="text-Align:center">
        <form class="rosten_form" id="rosten_form" onsubmit="return false;" style="text-align:left;">
			
        	<input  data-dojo-type="dijit.form.ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${model?.id }"' />
        	<input  data-dojo-type="dijit.form.ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
            <fieldset class="fieldset-form">
                <legend class="tableHeader">模块配置</legend>
                <table class="tableData" style="width:550px">
                    <tbody>
                       <tr>
                            <td>
                                <div align="right" >
                                  <span style="color:red">*&nbsp;</span>模块名称：
                                </div>
                            </td>
                            <td>
                                <input id="modelName" data-dojo-type="dijit.form.ValidationTextBox" 
                                	data-dojo-props='name:"modelName",${fieldAcl.isReadOnly("modelName")},
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入模块名称...",
              							value:"${model?.modelName}"
                               '/>
                            </td>
                            </tr>
						<tr>
                        	<td>
                                <div align="right" >
                                    <span style="color:red">*&nbsp;</span>链接导航路径：
                                </div>
                            </td>
                             <td>
                             	<input id="modelUrl" data-dojo-type="dijit.form.ValidationTextBox"
                             		data-dojo-props='name:"modelUrl",${fieldAcl.isReadOnly("modelUrl")},
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入导航路径...",
              							value:"${model.modelUrl!=null?model.modelUrl:"/himsweb/system/navigation"}"
                                '/>
    						</td>
                        </tr>
                       	<tr>
                        	<td>
                                <div align="right" >允许进入用户：</div>
                            </td>
                             <td><input id="allowusersName" data-dojo-type="dijit.form.ValidationTextBox"
                             		data-dojo-props = '${fieldAcl.isReadOnly("allowusersName")},
                             			"class":"input",
                             			trim:true,
                             			style:{width:"400px"},
                             			value:"${allowusersName }"
                             	'/>
                             	<g:hiddenField name="allowusersId" value="${allowusersId}" />
							 	<button data-dojo-type="dijit.form.Button"
							 			data-dojo-props='onClick:function(){
							 					selectUser("${createLink(controller:'system',action:'userTreeDataStore',params:[companyId:company?.id])}")
							 			}'
							 	>选择</button>
    							
    						</td>
                        </tr>
						<tr>
                        	<td>
                                <div align="right" >允许进入部门：</div>
                            </td>
                             <td><input id="allowdepartsName" data-dojo-type="dijit.form.ValidationTextBox"
                   					data-dojo-props='"class":"input",
                   						style:"width:400px",
                   						trim:true,
                   						${fieldAcl.isReadOnly("allowdepartsName")},
                   						value:"${allowdepartsName }"
                   				'/>
                   				<g:hiddenField name="allowdepartsId" value="${allowdepartsId }" />
								<button data-dojo-type="dijit.form.Button" 
										data-dojo-props = 'onClick:function(){selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}")}'
								>选择</button>
    						</td>
                        </tr>
						<tr>
                        	<td>
                                <div align="right" >允许进入群组：</div>
                            </td>
                             <td><input id="allowgroupsName" data-dojo-type="dijit.form.ValidationTextBox"
                   					data-dojo-props='"class":"input",
                   						style:"width:400px",
                   						trim:true,
                   						${fieldAcl.isReadOnly("allowgroupsName")},
                   						value:"${allowgroupsName }"
                   				'/>
                   				<g:hiddenField name="allowgroupsId" value="${allowgroupsId }" />
								<button data-dojo-type="dijit.form.Button" 
									data-dojo-props = 'onClick:function(){selectGroup("${createLink(controller:'system',action:'groupSelect',params:[companyId:company?.id])}")}'
								>选择</button>
    						</td>
                        </tr>					
						<tr>
                        	<td>
                                <div align="right" >内容描述：</div>
                            </td>
                             <td colspan="3">
                             	<textarea id="description" data-dojo-type="dijit.form.SimpleTextarea"
                             		data-dojo-props='name:"description",${fieldAcl.isReadOnly("description")},
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
              							value:"${model?.description}"
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