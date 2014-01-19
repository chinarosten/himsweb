<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>短信发送</title>
    <meta name="layout" content="rosten" />
	<script type="text/javascript">

		require(["dojo/parser",
		 		"dojo/_base/kernel",
		 		"dijit/registry",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/SimpleTextarea",
		     	"rosten/widget/ActionBar",
		     	"rosten/app/SystemApplication",
				"rosten/app/SmsManage"
		     	],function(parser,kernel,registry){
			kernel.addOnLoad(function(){
				rosten.init({webpath:"${request.getContextPath()}"});
				rosten.cssinit();
			});
			smsGroup_add = function(){
		    	var smsGroupName = registry.byId("smsGroupName");
				if(!smsGroupName.isValid()){
					rosten.alert("群组名称不正确！").queryDlgClose = function(){
						smsGroupName.focus();
					};
					return;
				}
				
				var content = {};
				var unid = registry.byId("unid");
				if(unid.attr("value")!=""){
					content.unid = unid.attr("value");
				}
				content.groupName = smsGroupName.attr("value");

				var members = registry.byId("members");
				if(members.attr("value")!=""){
					content.members = members.attr("value");
				}
				var description = registry.byId("description");
				if(description.attr("value")!=""){
					content.description = description.attr("value");
				}
				
		    	rosten.readSync(rosten.webPath + "/system/smsGroupSave",content,function(data){
					if(data.result=="true"){
						rosten.alert("保存成功！").queryDlgClose= function(){
							page_quit();	
						};
					}else{
						rosten.alert("保存失败!");
					}
				});
		    }
			

		});	

		
    </script>
</head>
<body>
	<div class="rosten_action">
		<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'smsGroupForm',params:[userid:user?.id])}"'></div>
	</div>
	<div style="text-Align:center">
		 
	    <div class="rosten_form">
	    	<input style="display:none" data-dojo-type="dijit/form/ValidationTextBox" id="unid" name="unid"  value="${smsgroup?.id }"></input>
	        <fieldset class="fieldset-form" style="text-align:left">
	            <legend class="tableHeader">短信群组
	            </legend>
	            <table class="tableData">
				<tr>
				    <td width="100" align="right"><span style="color:red">*&nbsp;</span>群组名称：</td>
				    <td>
				    	<input id="smsGroupName" name="smsGroupName" class="input" type="text" promptMessage="请正确输入群组名称..."
				    		value="${smsgroup?.groupName }" style="margin-left:1px"
                            data-dojo-type="dijit/form/ValidationTextBox" trim="true" required="true" />
				    </td>
				  </tr>
				  <tr>
				    <td align="right"><span style="color:red">*&nbsp;</span>成员：
				    	<a href="javascript:rosten.selectUser('members',false,'<%=path%>','/jsproot/hairdress/normal/Select_Consumer.jsp')">
                           <img src="<%=path%>/jslib/rosten/src/share/group.gif" width="16" height="16" border="0" align="absbottom">
						</a>
				    <td ><textarea id="members" name="members" rows="10" data-dojo-type="dijit/form/SimpleTextarea" 
				    	value="${smsgroup?.members }"
				    	trim="true" required="true" style="width: 550px;margin-left:1px;" ></textarea>
				    	</td>
				  </tr>
 				
				  <tr>
				    <td align="right">备注：</td>
				    <td ><textarea id="description" name="description" data-dojo-type="dijit/form/SimpleTextarea" 
				    	value="${smsgroup?.description }"
				    	rows="4" style="width: 550px;margin-left:1px;" trim="true" ></textarea></td>
				  </tr>

				</table>
	        </fieldset>
		</div>
	</div>
</body>
</html>