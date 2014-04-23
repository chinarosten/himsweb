<html> 
<head> 
	<meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
	<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no"/>
	<meta name="apple-mobile-web-app-capable" content="yes"/>
	<title>恒成OA-android版</title>
	
	<link href="../js/dojox/mobile/themes/iphone/iphone.css" type="text/css" rel="stylesheet">
	
	<script type="text/javascript" src="../js/dojox/mobile/deviceTheme.js" data-dojo-config="mblThemeFiles: ['base','Button']"></script>	
  	<script type="text/javascript" src="../js/dojo/dojo.js"  djConfig="parseOnLoad: true,isDebug: false"></script> 
  	
  	<script type="text/javascript">
		require([
			"dojo/_base/connect",
			"dijit/registry",
			"dojo/ready",	
			"dojox/mobile/parser",
			"dojox/mobile",
			"dojox/mobile/compat",
			"dojox/mobile/ScrollableView",
			"dojox/mobile/Button",
			"dojox/mobile/TextBox"
		],function(connect,registry,ready){
			ready(function(){
				var btnWidget = registry.byId("loginButton");
				connect.connect(btnWidget.domNode, "click", function(){
					var username = registry.byId("username");
					if(username.get("value")==""){
						alert("用户名不正确！");
						return false;
					}
					
					var password = registry.byId("password");
					if(password.get("value")==""){
						alert("密码不正确！");
						return false;
					}
					var content = {};
					content.j_username = username.get("value");
					content.j_password = password.get("value");
					content.loginType = "mobile";
					jsPost("/himsweb/j_spring_security_check",content);
				});
			});
			jsPost = function(action, values){
				var id = Math.random();
				document.write('<form id="post' + id + '" name="post'+ id +'" action="' + action + '" method="post">');
				for (var key in values) {
					document.write('<input type="hidden" name="' + key + '" value="' + values[key] + '" />');
				}
				document.write('</form>');
				document.getElementById('post' + id).submit();
			};
		});
	</script>
</head>
<body> 
  
  <div data-dojo-type="dojox/mobile/ScrollableView" style="text-align:center">
  	<div style="margin-top: 30px;margin-bottom: 10px">
  		<img src="../images/rosten/mobile/ic_launcher.png" />
  	</div>
  	<div>
  		<input type="text" class="mblTextBox" style="width:98%;height:50px;font-size:20px;text-align:center" id="username" name="username"
  			data-dojo-type="dojox/mobile/TextBox" data-dojo-props='placeHolder:"用户名",trim:true '>
  	</div>
  	<div style="margin-top:1px">
  		<input type="password" class="mblTextBox" style="width:98%;height:50px;font-size:20px;text-align:center" id="password" name="password"
  			data-dojo-type="dojox/mobile/TextBox" data-dojo-props='placeHolder:"密码",trim:true '>
  	</div>
  	<div>
  		<button data-dojo-type="dojox/mobile/Button" id="loginButton" class="mblBlueButton" 
			style="width:96%;margin-top: 10px;margin-bottom: 10px;font-size:20px;height:40px;line-heigth:40px">登  录</button>
  	</div>
  </div>	
</body> 
</html>