<html> 
<head> 
	<meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
	<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no"/>
	<meta name="apple-mobile-web-app-capable" content="yes"/>
	<title>恒成OA-android版</title>
	
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
		.content {
			padding:0px 10px;
			background-color: white;
			text-align:center;
		}
		.title {
			color: blue;
			margin-bottom: 3px;
			margin-top:6px;
		}
		.mblToolBarButtonText .mblToolBarButtonLabel{
			color:#FFFFFF;
			font-size:13px;
		}
	
	</style>
	
	<script type="text/javascript" src="../js/dojox/mobile/deviceTheme.js" data-dojo-config="mblThemeFiles: ['base','TabBar','Button']"></script>	
  	<script type="text/javascript" src="../js/dojo/dojo.js"  djConfig="parseOnLoad: true,isDebug: true"></script> 
  	<script type="text/javascript" src="../js/rosten/rosten.js"></script> 
  	
  	<script type="text/javascript">
  		var mobile_store;
		require([
			"dojo/_base/connect",
			"dojo/ready",
			"dijit/registry",
			"dojo/json",
			"dojox/mobile/parser",
			"dojox/mobile",
			"dojox/mobile/compat",
			"dojox/mobile/ScrollableView",
			"dojox/mobile/View",
			"dojox/mobile/TabBar",
			"dojox/mobile/EdgeToEdgeList",
			"dojox/mobile/EdgeToEdgeStoreList",
			"rosten/mobile/Mobile",
			"dojox/mobile/Button"
		],function(connect,ready,registry,JSON){
			initUserInfor("${request.getContextPath()}",JSON.parse('${userInfor}'));
			ready(function(){
				getBbs();
				getUserMobiles();
				var btnWidget = registry.byId("logoutButton");
				connect.connect(btnWidget.domNode, "click", function(){
					var url = "${createLink(controller:'j_spring_security_logout')}";
					window.location = url;
				});	
			});	
		});
	</script>
</head>
<body style="background-color:white"> 
  
  <div data-dojo-type="dojox.mobile.ScrollableView">
  	<div id="tabBarContent">
  		<div id="tabBar_start" data-dojo-type="dojox/mobile/View" data-dojo-props='selected:true'>
  			<h1 data-dojo-type="dojox/mobile/Heading" data-dojo-props='fixed:"top"'>通知公告</h1>
  			<ul data-dojo-type="dojox/mobile/EdgeToEdgeList" class="list1" id="list_start">
  			</ul>
  		</div>
  		<div id="bbs_infor" data-dojo-type="dojox/mobile/View" style="background-color:white;height:100%">
			<h1 data-dojo-type="dojox/mobile/Heading" data-dojo-props='back:"返回", moveTo:"#tabBar_start"'>公告内容</h1>
			<div class="content">
				<div style="border-bottom:1px solid #B9BDBE">
					<h3 class="title" id="topic"></h3>
					<div style="font-size:10px;color:#B9BDBE;margin-bottom: 2px">
						<span>公告类型：</span>
						<span id="category"></span>
						<span style="margin-left:20px">时间：</span>
						<span id="date"></span>
						
					</div>
				</div>
				
				<div id="content" style="margin-top:5px;text-align:left;font-size:10px"></div>
			</div>
		</div>
  		
  		<div id="tabBar_mobile" data-dojo-type="dojox/mobile/View" data-dojo-props=''>
  			<h1 data-dojo-type="dojox/mobile/Heading" data-dojo-props='fixed:"top"'>通讯录</h1>
  			<ul data-dojo-type="dojox/mobile/EdgeToEdgeStoreList" id="list_mobile" data-dojo-props='store:mobile_store, query:{},append:true'></ul>
  		</div>
  		
  		<div id="mobile_infor" data-dojo-type="dojox/mobile/View" data-dojo-props=''>
  			<h1 id="mobile_header" data-dojo-type="dojox/mobile/Heading" data-dojo-props='fixed:"top",back:"返回",moveTo:"tabBar_mobile"'></h1>
  			<div style="border-bottom:1px solid #B9BDBE;height:50px;line-height:50px;text-align:left" class="content">
  				<label>所属部门：</label>
  				<span id="mobile_depart"></span>
  			</div>
  			
  			<div style="border-bottom:1px solid #B9BDBE;height:50px;line-height:50px;text-align:left" class="content">
  				<label>移动电话：</label>
  				<span id="mobile_telephone"></span>
  			</div>
  			
  		</div>
  		
  		<div id="tabBar_setting" data-dojo-type="dojox/mobile/View" data-dojo-props='' id="list_setting">
  			<h1 data-dojo-type="dojox/mobile/Heading" data-dojo-props='fixed:"top"'>设置</h1>
  			
  			<div style="border-bottom:1px solid #B9BDBE;height:50px;line-height:50px;text-align:left" class="content">
  				<label>用户名称：</label>
  				<span id="setting_username">${user.getFormattedName() }</span>
  			</div>
  			
  			<div style="border-bottom:1px solid #B9BDBE;height:50px;line-height:50px;text-align:left" class="content">
  				<label>所属部门：</label>
  				<span id="setting_depart">${user.getDepartName() }</span>
  			</div>
  			
  			<div style="border-bottom:1px solid #B9BDBE;height:50px;line-height:50px;text-align:left" class="content">
  				<label>移动电话：</label>
  				<span id="setting_telephone">${user.telephone }</span>
  			</div>
  			<div style="text-align:center">
  				<button data-dojo-type="dojox/mobile/Button" id="logoutButton" class="mblRedButton" 
					style="width:96%;margin-top: 10px;margin-bottom: 10px;font-size:20px;height:40px;line-heigth:40px">退出登录</button>
  			</div>
  			
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