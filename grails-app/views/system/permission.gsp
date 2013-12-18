<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rostenApp" />
    <title>权限管理</title>
    <r:jsLoad dir="js/app" file="SystemApplication.js"/>
	<script type="text/javascript">
		dojo.require("dijit.form.ValidationTextBox");
		dojo.require("dijit.form.SimpleTextarea");
		dojo.require("dijit.form.Button");
		dojo.require("dijit.form.CheckBox");
		dojo.require("rosten.widget.ActionBar");
		dojo.require("rosten.widget.MultiSelectDialog");

		dojo.addOnLoad(function(){
			rosten.cssinit();
		});
		permission_add = function(){
			var permissionName = dijit.byId("permissionName");
			if(!permissionName.isValid()){
				rosten.alert("权限名称不正确！").queryDlgClose = function(){
					permissionName.focus();
				};
				return;
			}
			var content = {};
			
			rosten.readerByFormSync(rosten.webPath + "/system/permissionSave","rosten_form",content,function(data){
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
		<div data-dojo-type="rosten.widget.ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'permissionForm',params:[userid:user?.id])}"'></div>
	</div>
		<div style="text-Align:center">
        <form class="rosten_form" id="rosten_form" onsubmit="return false;" style="text-align:left;">
			
        	<input  data-dojo-type="dijit.form.ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${permission?.id }"' />
        	<input  data-dojo-type="dijit.form.ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
            <fieldset class="fieldset-form">
                <legend class="tableHeader">权限配置</legend>
                <table class="tableData" style="width:550px">
                    <tbody>
                       <tr>
                            <td>
                                <div align="right" >
                                  <span style="color:red">*&nbsp;</span>权限名称：
                                </div>
                            </td>
                            <td>
                                <input id="permissionName" data-dojo-type="dijit.form.ValidationTextBox" 
                                	data-dojo-props='name:"permissionName",${fieldAcl.isReadOnly("permissionName")},
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入权限名称...",
              							value:"${permission?.permissionName}"
                               '/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div align="right" >操作类型集合：</div>
                            </td>
                            <td>
                            	<input data-dojo-type="dijit.form.CheckBox" data-dojo-props='id: "setOperation_1", name:"setOperation", value:"read",checked:true '/> 只读
                            	<input data-dojo-type="dijit.form.CheckBox" data-dojo-props='id: "setOperation_2", name:"setOperation", value:"add",checked:${setOperation.contains("add") }'/> 添加
                            	<input data-dojo-type="dijit.form.CheckBox" data-dojo-props='id: "setOperation_3", name:"setOperation", value:"delete",checked:${setOperation.contains("delete") }'/> 删除
                            	<input data-dojo-type="dijit.form.CheckBox" data-dojo-props='id: "setOperation_4", name:"setOperation", value:"change",checked:${setOperation.contains("change") } '/> 修改
                            </td>
                        </tr>
                         <tr>
                            <td>
                                <div align="right" >具有资源：</div>
                            </td>
                            <td>
                                <textarea id="allowresourcesName" data-dojo-type="dijit.form.SimpleTextarea"
                   					data-dojo-props='"class":"input",
                   						style:"width:395px",
                   						trim:true,
                   						${fieldAcl.isReadOnly("allowresourcesName")},
                   						value:"${allowresourcesName }"
                   				'></textarea>
                   				<g:hiddenField name="allowresourcesId" value="${allowresourcesId }" />
								<button data-dojo-type="dijit.form.Button" 
									data-dojo-props = 'onClick:function(){selectResource("${createLink(controller:'system',action:'resourceTreeDataStore',params:[companyId:company?.id])}")}'
								>选择</button>
                            </td>
                        </tr>
                        <tr>
	                        <td>
	                        	 <div align="right"><span style="color:red">*&nbsp;</span>是否匿名访问：</div>
	                        </td>
	                        <td>
	                        	<input id="isAnonymous1" data-dojo-type="dijit.form.RadioButton"
                             		data-dojo-props='name:"isAnonymous",
                             			type:"radio",
                             			${fieldAcl.isReadOnly("isAnonymous")},
                             			<g:if test="${permission?.isAnonymous }">checked:true,</g:if>
              							value:"true"
                                '/>
								<label for="isAnonymous1">是</label>
								
                                <input id="isAnonymous2" data-dojo-type="dijit.form.RadioButton"
                             		data-dojo-props='name:"isAnonymous",
                             			type:"radio",
                             			${fieldAcl.isReadOnly("isAnonymous")},
                             			<g:if test="${!permission?.isAnonymous }">checked:true,</g:if>
              							value:"false"
                                '/>
								<label for="isAnonymous2">否</label>
								
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
                                		style:{width:"395px"},
                                		trim:true,
              							value:"${permission?.description}"
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