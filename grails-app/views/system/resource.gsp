<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rostenApp" />
    <title>资源管理</title>
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
		resource_add = function(){
			var resourceName = dijit.byId("resourceName");
			if(!resourceName.isValid()){
				rosten.alert("资源名称不正确！").queryDlgClose = function(){
					resourceName.focus();
				};
				return;
			}
			var modelName = dijit.byId("modelName");
			if(!modelName.isValid()){
				rosten.alert("所属模块不正确！").queryDlgClose = function(){
				};
				return;
			}
			var imgUrl = dijit.byId("imgUrl");
			if(!imgUrl.isValid()){
				rosten.alert("图标地址不正确！").queryDlgClose = function(){
					imgUrl.focus();
				};
				return;
			}
			var url = dijit.byId("url");
			if(!url.isValid()){
				rosten.alert("导航地址不正确！").queryDlgClose = function(){
					url.focus();
				};
				return;
			}
			var content = {};
			
			rosten.readerByFormSync(rosten.webPath + "/system/resourceSave","rosten_form",content,function(data){
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
		<div data-dojo-type="rosten.widget.ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'resourceForm',params:[userid:user?.id])}"'></div>
	</div>
		<div style="text-Align:center">
        <form class="rosten_form" id="rosten_form" onsubmit="return false;" style="text-align:left;">
			
        	<input  data-dojo-type="dijit.form.ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${resource?.id }"' />
        	<input  data-dojo-type="dijit.form.ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
            <fieldset class="fieldset-form">
                <legend class="tableHeader">资源配置</legend>
                <table class="tableData" style="width:550px">
                    <tbody>
                       <tr>
                            <td>
                                <div align="right" >
                                  <span style="color:red">*&nbsp;</span>资源名称：
                                </div>
                            </td>
                            <td>
                                <input id="resourceName" data-dojo-type="dijit.form.ValidationTextBox" 
                                	data-dojo-props='name:"resourceName",${fieldAcl.isReadOnly("resourceName")},
                                		"class":"input",
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入资源名称...",
              							value:"${resource?.resourceName}"
                               '/>
                            </td>
                        </tr>
						<tr>
                        	<td>
                                <div align="right" >所属模块：</div>
                            </td>
                             <td>
                             	<input id="modelName" data-dojo-type="dijit.form.ValidationTextBox" 
                                	data-dojo-props='name:"modelName",${fieldAcl.isReadOnly("modelName")},
                                		"class":"input",
                                		trim:true,
                                		required:true,
                                		disabled:true,
              							value:"${resource?.model?.modelName}"
                                '/>
                                <input id="modelId" data-dojo-type="dijit.form.ValidationTextBox" data-dojo-props='name:"modelId",value:"${resource?.model?.id }",style:{display:"none"}'/>
								<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){selectModel("${company?.id }")}'>选择</button>
                             
    						</td>
                        </tr>
                        <tr>
                            <td>
                                <div align="right" >
                                  <span style="color:red">*&nbsp;</span>图标地址：
                                </div>
                            </td>
                            <td>
                                <input id="imgUrl" data-dojo-type="dijit.form.ValidationTextBox" 
                                	data-dojo-props='name:"imgUrl",${fieldAcl.isReadOnly("imgUrl")},
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入图标地址...",
              							value:"${resource?.imgUrl}"
                               '/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div align="right" >
                                  <span style="color:red">*&nbsp;</span>导航地址：
                                </div>
                            </td>
                            <td>
                                <input id="url" data-dojo-type="dijit.form.ValidationTextBox" 
                                	data-dojo-props='name:"url",${fieldAcl.isReadOnly("url")},
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入导航地址...",
              							value:"${resource?.url}"
                               '/>
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
              							value:"${resource?.description}"
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