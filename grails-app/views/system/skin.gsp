<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rostenApp" />
    <title>个性定制</title>
    <r:jsLoad dir="js/app" file="SystemApplication.js"/>
	<script type="text/javascript">
		dojo.require("dijit.form.RadioButton");
		dojo.require("rosten.widget.ActionBar");
		dojo.addOnLoad(function(){
			rosten.cssinit();
		});
		user_uiconfig_add = function(){
			var radioItems = document.getElementsByName("theme");
			var themeValue = "";
			for(i=0;i<radioItems.length;i++){
				if(radioItems[i].checked){
					themeValue = radioItems[i].value;
				}
			}
			if(themeValue==""){
				rosten.alert("请正确选择皮肤样式！");
				return;
			}
			var urlArgs = {cssStyle:themeValue};
			rosten.readerSync(rosten.webPath + "/system/skinSave/${user?.id}",urlArgs,function(data){
				if(data.result=="true"){
					rosten.alert("成功保存!").queryDlgClose= function(){
						page_quit_1(true);
					};
				}else{
					rosten.alert("保存失败!");
				}
			},function(data_1){
				rosten.alert("保存失败!");
			})
		}
		
    </script>
</head>
<body>
	<div class="rosten_action">
		<div data-dojo-type="rosten.widget.ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'skin')}"'></div>
	</div>
		<div style="text-Align:center">
        <form class="rosten_form" id="rosten_form" onsubmit="return false;" style="text-align:left;">
        	<g:hiddenField name="id" value="${user?.id}" />
            <fieldset class="fieldset-form">
                <legend class="tableHeader">个性定制</legend>
                <table class="tableData" style="margin:30px auto;font-size:16px">
                    <tbody>
                       
                    <tr>
                        <td width="340">
                        	<center>
                        		<input type="radio" style="width:20px;border:none" name="theme" value="normal" >
                        		清凉夏季
                        	</center>
                        	<img src="${createLinkTo(dir:'css',file:'rosten/themes/normal/images/preview.gif')}" border="0">
                        </td>
                        <td width="308">
                        	<center>
                        		<input type="radio" style="width:20px;border:none" name="theme" value="rose" >
                        		浪漫玫瑰
                        	</center>
                        	<img src="${createLinkTo(dir:'css',file:'rosten/themes/rose/images/preview.gif')}" border="0">
							
                        </td>
                    </tr>
                    <tr>
                        <td width="340">
                        	<center>
                        		<input type="radio" style="width:20px;border:none" name="theme" value="cfyl" >
                        		春风杨柳
                        	</center>
                        	<img src="${createLinkTo(dir:'css',file:'rosten/themes/cfyl/images/preview.gif')}" border="0">
                        </td>
                        <td width="308">
                        	<center>
                        		<input type="radio" style="width:20px;border:none" name="theme" value="hbls" >
                        		环保绿色
                        	</center>
                        	<img src="${createLinkTo(dir:'css',file:'rosten/themes/hbls/images/preview.gif')}" border="0">
							
                        </td>
                    </tr>
                    <tr>
                        <td width="340">
                        	<center>
                        		<input type="radio" style="width:20px;border:none" name="theme" value="jqsy" >
                        		金秋十月
                        	</center>
                        	<img src="${createLinkTo(dir:'css',file:'rosten/themes/jqsy/images/preview.gif')}" border="0">
                        </td>
                        <td width="308">
                        	<center>
                        		<input type="radio" style="width:20px;border:none" name="theme" value="jsnz" >
                        		金色农庄
                        	</center>
                        	<img src="${createLinkTo(dir:'css',file:'rosten/themes/jsnz/images/preview.gif')}" border="0">
							
                        </td>
                    </tr>
                    <tr>
                        <td width="340">
                        	<center>
                        		<input type="radio" style="width:20px;border:none" name="theme" value="lhqh" >
                        		蓝灰情怀
                        	</center>
                        	<img src="${createLinkTo(dir:'css',file:'rosten/themes/lhqh/images/preview.gif')}" border="0">
                        </td>
                        <td width="308">
                        	<center>
                        		<input type="radio" style="width:20px;border:none" name="theme" value="shys" >
                        		深红夜思
                        	</center>
                        	<img src="${createLinkTo(dir:'css',file:'rosten/themes/shys/images/preview.gif')}" border="0">
							
                        </td>
                    </tr>
                        
                    </tbody>
                </table>
            </fieldset>
	</form>
	</div>
</body>
</html>