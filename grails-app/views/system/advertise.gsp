<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>广告设置</title>
	<script type="text/javascript">
        dojo.require("dijit.form.ValidationTextBox");
        dojo.require("dijit.form.FilteringSelect");
		dojo.require("rosten.widget.ActionBar");

		saveAdvertise = function(){
			var isUse = dijit.byId("isUse");
			if(!isUse.isValid()){
				rosten.alert("启用广告不正确！").queryDlgClose = function(){
					isUse.focus();
				};
				return;
			}
			var url = dijit.byId("url");
			if(!url.isValid()){
				rosten.alert("服务器地址不正确！").queryDlgClose = function(){
					url.focus();
				};
				return;
			}
			var title = dijit.byId("title");
			if(!title.isValid()){
				rosten.alert("广告标题不正确！").queryDlgClose = function(){
					title.focus();
				};
				return;
			}
			var content = {};
			content.isUse = isUse.attr("value");
			content.url = url.attr("value");
			content.title = title.attr("value");
			
			var contentStr = dijit.byId("content");
			if(contentStr.attr("value")!=""){
				content.content = contentStr.attr("value");
			}
			var unid = dojo.byId("unid");
			if(unid.innerHTML!=""){
				content.id = unid.innerHTML;
			}
			rosten.reader("${createLink(controller:'system',action:'advertiseSave')}",content,function(data){
				if(data.result=="true"){
					if(data.unid){
						if(unid.innerHTML==""){
							unid.innerHTML = data.unid;
						}
					}
					rosten.alert("成功保存!");
				}else{
					rosten.alert("保存失败!");
				}	
			});
		}
		
    </script>
</head>
<body>
		<div data-dojo-type="rosten.widget.ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'advertise')}"'></div>
		<div style="text-Align:center">
        <div class="rosten_form" style="width:600px">
        	<div style="display:none" id="unid">${advertise?.id }</div>
            <fieldset class="fieldset-form">
                <legend class="tableHeader">广告配置</legend>
                <table class="tableData" style="text-align:left">
                    <tbody>
                    	<tr>
                            <td width="120">
                                <div align="right">
                                    <span style="color:red">*&nbsp;</span>启用广告：
                                </div>
                            </td>
                            <td  width="450">
                                <select id="isUse" data-dojo-type="dijit.form.FilteringSelect" 
                                	data-dojo-props='name:"isUse",style:{width:"182px"},trim:true,required:true,value:"${advertise?.isUsed }"'>
					   				<option value="true">是</option>
					   				<option value="false">否</option>
					   			</select>	
                               	
                            </td>
                    	</tr>
                        <tr>
                            <td width="120">
                                <div align="right">
                                    <span style="color:red">*&nbsp;</span>服务器地址：
                                </div>
                            </td>
                            <td  width="450">
                                <input id="url" data-dojo-props='"class":"input",style:{width:"400px"},trim:true,required:true,promptMessage:"输入错误,样例:http://www.rostensoft.com",value:"${advertise?.url }"'
                                	data-dojo-type="dijit.form.ValidationTextBox" />
                            </td>
                    	</tr>
                        <tr>
                            <td>
                                <div align="right" > 获取参数：</div>
                            </td>
                            <td>
                                <input id="content" data-dojo-props='"class":"input",style:{width:"400px"},trim:true,promptMessage:"输入样例:{args:argsdata}",value:"${advertise?.content }"' 
                                	data-dojo-type="dijit.form.ValidationTextBox"
                                />
                            </td>
                           
                        </tr>
						<tr>
                            <td>
                                <div align="right">
                                    <span style="color:red">*&nbsp;</span>广告标题：
                                </div>
                            </td>
                            <td>
                                <input id="title"
                                	data-dojo-type="dijit.form.ValidationTextBox" 
                                	data-dojo-props='"class":"input",style:{width:"400px"},promptMessage:"请正确输入广告标题...",trim:true,required:true,value:"${advertise?.title }"'	
                                	
                                />
							</td>
                        </tr>
                       
                    </tbody>
                </table>
            </fieldset>
	</div>
	</div>
</body>
</html>