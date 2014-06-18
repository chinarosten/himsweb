<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>常用服务</title>
	<script type="text/javascript">
		require(["dojo/parser",
		 		"dojo/_base/kernel",
		 		"dijit/registry",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/Button",
		     	"rosten/widget/ActionBar",
		     	"rosten/widget/MultiSelectDialog",
		     	"rosten/app/SystemApplication"],
			function(parser,kernel,registry){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}"});
					rosten.cssinit();
				});
			service_add = function(){
				var serviceName = registry.byId("serviceName");
				if(!serviceName.isValid()){
					rosten.alert("服务名称不正确！").queryDlgClose = function(){
						serviceName.focus();
					};
					return;
				}
				var modelName = registry.byId("modelName");
				if(!modelName.isValid()){
					rosten.alert("所属模块不正确！").queryDlgClose = function(){
					};
					return;
				}
				var imgUrl = registry.byId("imgUrl");
				if(!imgUrl.isValid()){
					rosten.alert("图标地址不正确！").queryDlgClose = function(){
						imgUrl.focus();
					};
					return;
				}
				var functionName = registry.byId("functionName");
				if(!functionName.isValid()){
					rosten.alert("方法名称不正确！").queryDlgClose = function(){
						functionName.focus();
					};
					return;
				}
				var functionArgs = registry.byId("functionArgs");
				if(!functionArgs.isValid()){
					rosten.alert("参数不正确！").queryDlgClose = function(){
						functionArgs.focus();
					};
					return;
				}
				var content = {};
				
				rosten.readSync(rosten.webPath + "/system/serviceSave",content,function(data){
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
		<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'serviceForm',params:[userid:user?.id])}"'></div>
	</div>
		<div style="text-Align:center">
        <form class="rosten_form" id="rosten_form" onsubmit="return false;" style="text-align:left;">
			
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${normalService?.id }"' />
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
            <fieldset class="fieldset-form">
                <legend class="tableHeader">常用服务</legend>
                <table class="tableData" style="width:550px">
                    <tbody>
                       <tr>
                            <td>
                                <div align="right" >
                                  <span style="color:red">*&nbsp;</span>服务名称：
                                </div>
                            </td>
                            <td>
                                <input id="serviceName" data-dojo-type="dijit/form/ValidationTextBox" 
                                	data-dojo-props='name:"serviceName",${fieldAcl.isReadOnly("serviceName")},
                                		"class":"input",
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入服务名称...",
              							value:"${normalService?.serviceName}"
                               '/>
                            </td>
                        </tr>
						<tr>
                        	<td>
                                <div align="right" >所属模块：</div>
                            </td>
                             <td>
                             	<input id="modelName" data-dojo-type="dijit/form/ValidationTextBox" 
                                	data-dojo-props='name:"modelName",${fieldAcl.isReadOnly("modelName")},
                                		"class":"input",
                                		trim:true,
                                		required:true,
                                		disabled:true,
              							value:"${normalService?.model?.modelName}"
                                '/>
                                <input id="modelId" data-dojo-type="dijit/form/ValidationTextBox" data-dojo-props='name:"modelId",value:"${normalService?.model?.id }",style:{display:"none"}'/>
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
                                <input id="imgUrl" data-dojo-type="dijit/form/ValidationTextBox" 
                                	data-dojo-props='name:"imgUrl",${fieldAcl.isReadOnly("imgUrl")},
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入图标地址...",
              							value:"${normalService?.imgUrl}"
                               '/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div align="right" >
                                  <span style="color:red">*&nbsp;</span>方法名称：
                                </div>
                            </td>
                            <td>
                                <input id="functionName" data-dojo-type="dijit/form/ValidationTextBox" 
                                	data-dojo-props='name:"functionName",readOnly:true,
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
                                		required:true,
              							value:"${normalService?.functionName}"
                               '/>
                            </td>
                        </tr>
						<tr>
                        	<td>
                                <div align="right" ><span style="color:red">*&nbsp;</span>参数：</div>
                            </td>
                             <td>
                             	<textarea id="functionArgs" data-dojo-type="dijit/form/ValidationTextBox"
                             		data-dojo-props='name:"functionArgs",${fieldAcl.isReadOnly("functionArgs")},
                                		"class":"input",
                                		style:{width:"400px"},
                                		trim:true,
                                		required:true,
              							value:"${normalService?.functionArgs}"
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