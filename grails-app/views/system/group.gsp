<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>群组管理</title>
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
			group_add = function(){
				var groupName = registry.byId("groupName");
				if(!groupName.isValid()){
					rosten.alert("群组名称不正确！").queryDlgClose = function(){
						groupName.focus();
					};
					return;
				}
				var content = {};
				
				rosten.readSync(rosten.webPath + "/system/groupSave",content,function(data){
					if(data.result=="true"){
						rosten.alert("保存成功！").queryDlgClose= function(){
							page_quit();	
						};
					}else{
						rosten.alert("保存失败!");
					}
				},null,"rosten_form");
			}
		});
    </script>
</head>
<body>
	<div class="rosten_action">
		<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'groupForm',params:[userid:user?.id])}"'></div>
	</div>
		<div style="text-Align:center">
        <form class="rosten_form" id="rosten_form" onsubmit="return false;" style="text-align:left;">
			
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${group?.id }"' />
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
            <fieldset class="fieldset-form">
                <legend class="tableHeader">群组配置</legend>
                <table class="tableData" style="width:550px">
                    <tbody>
                       <tr>
                            <td>
                                <div align="right" >
                                  <span style="color:red">*&nbsp;</span>群组名称：
                                </div>
                            </td>
                            <td>
                                <input id="groupName" data-dojo-type="dijit/form/ValidationTextBox" 
                                	data-dojo-props='name:"groupName",${fieldAcl.isReadOnly("groupName")},
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入群组名称...",
              							value:"${group?.groupName}"
                               '/>
                            </td>
                        </tr>
                       	<tr>
                        	<td>
                                <div align="right" >人员集合：</div>
                            </td>
                             <td>
                             	<input id="allowusersName" data-dojo-type="dijit/form/ValidationTextBox"
                             		data-dojo-props = '${fieldAcl.isReadOnly("allowusersName")},
                             			"class":"input",
                             			trim:true,
                             			style:{width:"400px"},
                             			value:"${allowusersName }"
                             	'/>
                             	<g:hiddenField name="allowusersId" value="${allowusersId}" />
							 	<button data-dojo-type="dijit.form.Button"
							 			data-dojo-props='onClick:function(){
							 				var _object = selectUser("${createLink(controller:'system',action:'userTreeDataStore',params:[companyId:company?.id])}");
							 				_object.afterLoad = function(){
							 					var allowusersId = "${allowusersId}".split(",");
												_object.selectedData(allowusersId);
							 				}
							 			}'
							 	>选择</button>
                             
                        </tr>
						<tr>
                        	<td>
                                <div align="right" >具有角色：</div>
                            </td>
                             <td>
                             	<input id="allowrolesName" data-dojo-type="dijit/form/ValidationTextBox"
                   					data-dojo-props='"class":"input",
                   						style:"width:400px",
                   						trim:true,
                   						${fieldAcl.isReadOnly("allowrolesName")},
                   						value:"${allowrolesName }"
                   				'/>
                   				<g:hiddenField name="allowrolesId" value="${allowrolesId }" />
								<button data-dojo-type="dijit/form/Button" 
									data-dojo-props = 'onClick:function(){selectRole("${createLink(controller:'system',action:'roleSelect',params:[companyId:company?.id])}")}'
								>选择</button>
                             
    						</td>
                        </tr>
						<tr>
                        	<td>
                                <div align="right" >内容描述：</div>
                            </td>
                             <td>
                             	<textarea id="description" data-dojo-type="dijit/form/SimpleTextarea"
                             		data-dojo-props='name:"description",${fieldAcl.isReadOnly("description")},
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
              							value:"${group?.description}"
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