<html> 
<head> 
	<meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
	<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no"/>
	<meta name="apple-mobile-web-app-capable" content="yes"/>
	<title>恒成OA-android版</title>
	
	<link href="../js/dojox/mobile/themes/common/domButtons/DomButtonYellowStar.css" rel="stylesheet"/>
	<link href="../js/dojox/mobile/themes/common/domButtons/DomButtonGrayStar.css" rel="stylesheet"/>
	<style type="text/css">
		.list1 li{
			border-style: solid;
			border-width: 1px 0px 1px 0px;
			border-top-color: #BABABC;
			border-bottom-color: #89898C;
			background-color: #ACACAF;
			line-height: 0px;
		}
		.list1 li table{
			line-height: normal;
		}
		.list1 li:nth-child(even){
			background-color: #97979B;
		}
		.lnk {
			font-size: 14px;
			color: #0B5199;
			text-decoration: none;
		}
	
	</style>
	
	<script type="text/javascript" src="../js/dojox/mobile/deviceTheme.js" data-dojo-config="mblThemeFiles: ['base','TabBar']"></script>	
  	<script type="text/javascript" src="../js/dojo/dojo.js"  djConfig="parseOnLoad: true,isDebug: true"></script> 
  	<script type="text/javascript" src="../js/rosten/rosten.js"></script> 
  	
  	<script type="text/javascript">
		require([
			"dojo/ready",
			"dojo/json",
			"dojox/mobile/parser",
			"dojox/mobile",
			"dojox/mobile/compat",
			"dojox/mobile/ScrollableView",
			"dojox/mobile/View",
			"dojox/mobile/TabBar",
			"dojox/mobile/EdgeToEdgeList",
			"dojox/mobile/EdgeToEdgeStoreList",
			"rosten/mobile/Mobile"
		],function(ready,JSON){
			initUserInfor("${request.getContextPath()}",JSON.parse('${userInfor}'));
			ready(function(){
				getBbs();
			});	
			getUserMobiles();
		});
	</script>
</head>
<body> 
  
  <div data-dojo-type="dojox.mobile.ScrollableView">
  	<div id="tabBarContent">
  		<div id="tabBar_start" data-dojo-type="dojox/mobile/View" data-dojo-props='selected:true'>
  			<h1 data-dojo-type="dojox/mobile/Heading" data-dojo-props='fixed:"top"'>通知公告</h1>
  			<ul data-dojo-type="dojox/mobile/EdgeToEdgeList" class="list1" id="list_start">
  				
  			</ul>
  		</div>
  		
  		<div id="tabBar_mobile" data-dojo-type="dojox/mobile/View" data-dojo-props=''>
  			<h1 data-dojo-type="dojox/mobile/Heading" data-dojo-props='fixed:"top"'>通讯录</h1>
  			<ul data-dojo-type="dojox/mobile/EdgeToEdgeStoreList" id="list_mobile" data-dojo-props='store:mobile_store, query:{}'></ul>
  		</div>
  		
  		<div id="tabBar_setting" data-dojo-type="dojox/mobile/View" data-dojo-props='' id="list_setting">
  			<h1 data-dojo-type="dojox/mobile/Heading" data-dojo-props='fixed:"top"'>设置</h1>
  		</div>
  		
  	</div>
  	
  	<ul data-dojo-type="dojox/mobile/TabBar" data-dojo-props='iconBase:"../js/dojox/mobile/tests/images/tab-icons.png",fixed:"bottom"'>
		<li data-dojo-type="dojox/mobile/TabBarButton" data-dojo-props='
			icon1:"../images/rosten/mobile/tab-icon-33.png",
			icon2:"../images/rosten/mobile/tab-icon-33h.png",moveTo:"tabBar_start",
			selected:true'>首页</li>
		<li data-dojo-type="dojox/mobile/TabBarButton" data-dojo-props='
			icon1:"../images/rosten/mobile/tab-icon-29.png",
			icon2:"../images/rosten/mobile/tab-icon-29h.png",moveTo:"tabBar_mobile"
			'>通讯录</li>
		<li data-dojo-type="dojox/mobile/TabBarButton" data-dojo-props='
			icon1:"../images/rosten/mobile/tab-icon-17.png",
			icon2:"../images/rosten/mobile/tab-icon-17h.png",moveTo:"tabBar_setting"
			'>设置</li>
	</ul>
  </div>	
</body> 
</html>