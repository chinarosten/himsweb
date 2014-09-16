<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>项目管理</title>
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
			
			project_add = function(){
				var name = registry.byId("name");
				if(!name.isValid()){
					rosten.alert("项目名称不正确！").queryDlgClose = function(){
						name.focus();
					};
					return;
				}
				var content = {userId:"${user?.id}"};
				rosten.readSync(rosten.webPath + "/account/projectSave",content,function(data){
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
		data-dojo-props='actionBarSrc:"${createLink(controller:'accountAction',action:'projectForm',params:[userid:user?.id])}"'></div>
</div>
<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="项目信息" data-dojo-props=''>
		<form id="rosten_form" name="rosten_form" url='[controller:"account",action:"accountSave"]' onsubmit="return false;" class="rosten_form" style="padding:0px;height:100px">
			<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${project?.id }"' />
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
        		
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"基本信息",toggleable:false,moreText:"",height:"100px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td  width="120"><div align="right"><span style="color:red">*&nbsp;</span>项目名称：</div></td>
					    <td colspan=3 width="620px">
					    	<input id="name" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='name:"name",required:true,
			                 		trim:true,style:{width:"551px"},
									value:"${project?.name}"
			                '/>	
			           </td>
					</tr>
					
				</table>
			</div>
			
		</form>
	</div>
	
</div>	
</body>
</html>