<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rostenApp" />
    <title>角色管理</title>
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
		role_add = function(){
			var authority = dijit.byId("authority");
			if(!authority.isValid()){
				rosten.alert("角色名称不正确！").queryDlgClose = function(){
					authority.focus();
				};
				return;
			}
			var content = {};
			
			rosten.readerByFormSync(rosten.webPath + "/system/roleSave","rosten_form",content,function(data){
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
		<div data-dojo-type="rosten.widget.ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'roleForm',params:[userid:user?.id])}"'></div>
	</div>
		<div style="text-Align:center">
        <form class="rosten_form" id="rosten_form" onsubmit="return false;" style="text-align:left;">
			
        	<input  data-dojo-type="dijit.form.ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${role?.id }"' />
        	<input  data-dojo-type="dijit.form.ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
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
                                <input id="authority" data-dojo-type="dijit.form.ValidationTextBox" 
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
                             	<input id="allowpermissionsName" data-dojo-type="dijit.form.ValidationTextBox"
                   					data-dojo-props='"class":"input",
                   						style:"width:400px",
                   						trim:true,
                   						${fieldAcl.isReadOnly("allowpermissionsName")},
                   						value:"${allowpermissionsName }"
                   				'/>
                   				<g:hiddenField name="allowpermissionsId" value="${allowpermissionsId }" />
								<button data-dojo-type="dijit.form.Button" 
									data-dojo-props = 'onClick:function(){selectPermission("${createLink(controller:'system',action:'permissionSelect',params:[companyId:company?.id])}")}'
								>选择</button>
                             
    						</td>
                        </tr>
						<tr>
                        	<td>
                                <div align="right" >内容描述：</div>
                            </td>
                             <td>
                             	<textarea id="description" data-dojo-type="dijit.form.SimpleTextarea"
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