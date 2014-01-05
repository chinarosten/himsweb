<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>角色管理</title>
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

			role_add = function(){
				var authority = registry.byId("authority");
				if(!authority.isValid()){
					rosten.alert("角色名称不正确！").queryDlgClose = function(){
						authority.focus();
					};
					return;
				}
				var content = {};
				
				rosten.readSync(rosten.webPath + "/system/roleSave",content,function(data){
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
		<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'roleForm',params:[userid:user?.id])}"'></div>
	</div>
		<div style="text-Align:center">
        <form class="rosten_form" id="rosten_form" onsubmit="return false;" style="text-align:left;">
			
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${role?.id }"' />
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
            <fieldset class="fieldset-form">
                <legend class="tableHeader">角色配置</legend>
                <table class="tableData" style="width:550px">
                    <tbody>
                       <tr>
                            <td>
                                <div align="right" >
                                  <span style="color:red">*&nbsp;</span>角色名称：
                                </div>
                            </td>
                            <td>
                                <input id="authority" data-dojo-type="dijit/form/ValidationTextBox" 
                                	data-dojo-props='name:"authority",${fieldAcl.isReadOnly("authority")},
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入角色名称...",
              							value:"${role?.authority}"
                               '/>
                            </td>
                        </tr>
						<tr>
                        	<td>
                                <div align="right" >具有权限：</div>
                            </td>
                             <td>
                             	<input id="allowpermissionsName" data-dojo-type="dijit/form/ValidationTextBox"
                   					data-dojo-props='"class":"input",
                   						style:"width:400px",
                   						trim:true,
                   						${fieldAcl.isReadOnly("allowpermissionsName")},
                   						value:"${allowpermissionsName }"
                   				'/>
                   				<g:hiddenField name="allowpermissionsId" value="${allowpermissionsId }" />
								<button data-dojo-type="dijit/form/Button" 
									data-dojo-props = 'onClick:function(){selectPermission("${createLink(controller:'system',action:'permissionSelect',params:[companyId:company?.id])}")}'
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
              							value:"${role?.description}"
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