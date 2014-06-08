<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>授权管理</title>
    <style type="text/css">
    	.rosten .rosten_form table tr{
    		height:35px;
    	}
    	body{
			overflow:auto;
		}
    </style>
	<script type="text/javascript">
	require(["dojo/parser",
		 		"dojo/_base/kernel",
		 		"dijit/registry",
		 		"rosten/widget/DepartUserDialog",
		 		"dijit/layout/TabContainer",
		 		"dijit/layout/ContentPane",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/Button",
		 		"dijit/form/DateTextBox",
		     	"rosten/widget/ActionBar",
		     	"rosten/widget/TitlePane",
		     	"rosten/app/Application",
		     	"rosten/kernel/behavior"],
			function(parser,kernel,registry){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}"});
					rosten.cssinit();
				});
			
			authorize_add = function(){
				var authority = registry.byId("authority");
				if(!authority.isValid()){
					rosten.alert("角色名称不正确！").queryDlgClose = function(){
						authority.focus();
					};
					return;
				}
				var content = {};
				
				rosten.readSync(rosten.webPath + "/system/authorizeSave",content,function(data){
					if(data.result=="true"){
						rosten.alert("保存成功！").queryDlgClose= function(){
							page_quit();	
						};
					}else{
						rosten.alert("保存失败!");
					}
				},null,"rosten_form");
			};
			authorize_selectUser = function(){
				var rostenShowDialog = rosten.selectUser("${createLink(controller:'system',action:'userTreeDataStore',params:[companyId:company?.id])}","single");
				rostenShowDialog.callback = function(data) {
					var item = data[0];
					if(item){
						registry.byId("beAuthorizer").attr("value",item.name);
						registry.byId("beAuthorizerId").attr("value",item.value);
						registry.byId("beAuthorizerDepart").attr("value",item.departName);
					}else{
						registry.byId("beAuthorizer").attr("value","");
						registry.byId("beAuthorizerId").attr("value","");
						registry.byId("beAuthorizerDepart").attr("value","");
					}
	            };
			};
			page_quit = function(){
				rosten.pagequit();
			};
		});
    </script>
</head>
<body>
<div class="rosten_action">
	<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'authorizeForm',params:[userid:user?.id])}"'></div>
</div>
<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="授权信息" data-dojo-props=''>
		<form id="rosten_form" name="rosten_form" url='[controller:"system",action:"authorizeSave"]' onsubmit="return false;" class="rosten_form" style="padding:0px">
			<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${authorize?.id }"' />
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
        		
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"基本信息",toggleable:false,moreText:"",height:"220px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td  width="120"><div align="right"><span style="color:red">*&nbsp;</span>授权信息：</div></td>
					    <td colspan=3 width="620px">
					    	<input id="authorizeInfor" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='name:"authorizeInfor",required:true,
			                 		trim:true,style:{width:"551px"},
									value:"${authorize?.authorizeInfor}"
			                '/>	
			           </td>
					</tr>
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>授权人：</div></td>
					    <td width="250">
					    	<input id="authorizer" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='trim:true,readOnly:true,
									value:"${authorize?.getFormattedAuthorizer()}"
			                '/>
			            </td>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>授权人部门：</div></td>
					    <td width="250">
					    	<input id="authorizerDepart" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='name:"authorizerDepart",trim:true,required:true,readOnly:true,
									value:"${authorize?.authorizerDepart}"
			                '/>
			                <button data-dojo-type="dijit/form/Button" 
								data-dojo-props = 'onClick:function(){rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",false,"authorizerDepart")}'
							>选择</button>
			            </td>    
					</tr>
					
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>被授权人：</div></td>
					    <td>
					    	<input id="beAuthorizer" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='trim:true,readOnly:true,
									value:"${authorize?.getFormattedBeAuthorizer()}"
			                '/>
			                <input  data-dojo-type="dijit/form/ValidationTextBox" id="beAuthorizerId" 
			                	data-dojo-props='name:"beAuthorizerId",style:{display:"none"},value:"${authorize?.beAuthorizer?.id }"' />
			                <button data-dojo-type="dijit/form/Button" 
								data-dojo-props = 'onClick:authorize_selectUser'
							>选择</button>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>被授权人部门：</div></td>
					    <td>
					    	<input id="beAuthorizerDepart" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='name:"beAuthorizerDepart",trim:true,required:true,readOnly:true,
			                 		placeHolder:"选择被授权人时自动生成",
									value:"${authorize?.beAuthorizerDepart}"
			                '/>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>开始时间：</div></td>
					    <td>
					    	<input id="startDate" data-dojo-type="dijit/form/DateTextBox" 
			                 	data-dojo-props='name:"startDate",
			                 		trim:true,required:true,
									value:"${authorize?.getShowDate("start")}"
			                '/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>结束时间：</div></td>
					    <td>
					    	<input id="endDate" data-dojo-type="dijit/form/DateTextBox" 
			                 	data-dojo-props='name:"endDate",
			                 		trim:true,required:true,
									value:"${authorize?.getShowDate("end")}"
			                '/>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>授权模块：</div></td>
					    <td colspan=3>
					    	<textarea id="authrizeModels" data-dojo-type="dijit/form/SimpleTextarea" 
    							data-dojo-props='name:"authrizeModels",
                              		"class":"input",
                              		style:{width:"549px"},
                              		trim:true,
                              		readOnly:true,
                              		value:"${authrizeModels}"
                           '>
    						</textarea>
    						<input id="authrizeModelsId" data-dojo-type="dijit/form/ValidationTextBox" data-dojo-props='name:"authrizeModelsId",value:"${authrizeModelsId}",style:{display:"none"}'/>
							<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){selectModel("${company?.id }")}'>选择</button>
			            </td>    
					</tr>
				</table>
			</div>
			
		</form>
	</div>
	
</div>	
</body>
</html>