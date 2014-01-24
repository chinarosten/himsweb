<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
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
			weboffice_notifyCtrlReady();
		});
		page_quit = function(){
			weboffice_close();
			window.close();
		};
		word_save = function(){

		};
		word_menu = function(){
			if(rosten.variable.wordMenu == undefined || rosten.variable.wordMenu == true){
				//隐藏
				document.all.WebOffice1.HideMenuArea("hideall","","","");
				rosten.variable.wordMenu = false;
			}else{
				//显示
				document.all.WebOffice1.HideMenuArea("showmenu","","","");
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
		<script>
			var s = "";
			s += "<object id=WebOffice1 height=768 width='100%' style='LEFT: 0px; TOP: 0px'  classid='clsid:E77E049B-23FC-4DB8-B756-60529A35FAD5' codebase='${request.getContextPath()}/weboffice_v6.0.5.0.cab#Version=6,0,5,0'>";
			s += "<param name='_ExtentX' value='6350'><param name='_ExtentY' value='6350'>";
			s += "</OBJECT>";
			document.write(s);
		</script>
	</div>
</body>
</html>