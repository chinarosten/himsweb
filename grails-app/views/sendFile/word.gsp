<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>发文正文</title>
    <meta name="layout" content="rosten" />
    
	<script type="text/javascript">
	require(["dojo/parser", "dojo/_base/kernel", "rosten/widget/ActionBar", "rosten/app/WebOffice","rosten/app/Application","rosten/kernel/behavior"], 
		function(parser, kernel) {
		parser.parse();
		kernel.addOnLoad(function() {
			rosten.init({webpath:"${request.getContextPath()}"});
			rosten.cssinit();
			weboffice_notifyCtrlReady("${wordTemplate?wordTemplate:""}");
		});
		page_quit = function(){
			weboffice_close();
			var parentWin = window.opener;
			window.close();
			parentWin.location.reload();
		};
		word_save = function(){
			var returnValue = weboffice_uploadDoc([{name:"filename",value:"${sendFile?.serialNo}.doc"},{name:"type",value:"doc"}],"${servletPath}sendFile/addWordFile1/${sendFile?.id}");
			if("ok" == returnValue){
				page_quit();
			} else {
				rosten.alert("文件上传失败")
			}
		};
		word_menu = function(){
			var webOfficeId = "WebOffice1";
			if(!checkIsIE()){
				webOfficeId = "Control";
			}
			var webObj = document.getElementById(webOfficeId);
			if(rosten.variable.wordMenu == undefined || rosten.variable.wordMenu == true){
				//隐藏
				webObj.HideMenuArea("hideall","","","");
				rosten.variable.wordMenu = false;
			}else{
				//显示
				webObj.HideMenuArea("showmenu","","","");
				rosten.variable.wordMenu = true;
			}
		};
		
	});
    </script>
    
    <SCRIPT language=javascript event=NotifyToolBarClick(iIndex) for=WebOffice1>
		weboffice_notifyToolBarClick(iIndex);
	</SCRIPT>
</head>
<body class="claro rosten">
	<div class="rosten_action">
		<div data-dojo-type="rosten/widget/ActionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'sendFileAction',action:'sendFileWord',params:[userid:user?.id])}"'></div>
	</div>
	<div>
		<r:jsLoad dir="js/rosten/kernel" file="loadWebOffice.js"/>
	</div>
</body>
</html>