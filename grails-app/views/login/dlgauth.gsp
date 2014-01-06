<html>
<head>
<title>Login</title>
<style type='text/css' media='screen'>
	#login{
		margin:5px 0px; padding:0px;
		text-align:center;
		font-size:12px
	}
	#login .inner .cssform p {
		clear: left;
		margin: 0;
		padding: 4px 0 4px 0;
		border-top: 0px dashed gray;
		margin-bottom: 5px;
		height: 1%;
	}
	#login .inner .cssform input[type='text'] {
		width: 130px;
	}
	#login .inner .cssform label {
		font-weight: bold;
		float: left;
		text-align:right;
		width: 70px;
		font-size:12px;
		line-height:20px;
		margin-right:10px;
	}
    #loginMessage {color:red;}
	#login .inner .text_ {width:130px;}
	#login .inner {
		width:250px;
		margin:0px auto;
		text-align:left;
		padding:2px;
		border-top:0px dashed #499ede;
		border-bottom:0px dashed #499ede;
		background-color:#fff;
	}
	
	#login input {
    	background-color: #fcfcfc;
    	border: 1px solid #ccc;
    	font: 12px verdana, arial, helvetica, sans-serif;
    	margin: 2px 0;
    	padding: 2px 4px;
	}

	#login input:focus{
    	border: 1px solid #b2d1ff;
	}
	
</style>
<script type="text/javascript">
	require([
	 		"dojo/_base/kernel",
	 		"dojo/dom", 
	 		"dojo/_base/connect",
			"dojo/query",
			"dojo/_base/window", 
	 		"dijit/registry",
	 		"dojo/_base/json",
	 		"dojo/dom-style",
	 		"dojo/_base/xhr"
	 		],
	     	function(kernel,dom,connect,query,win,registry,jsonTool,domStyle,xhr){
				kernel.addOnLoad(function(){
					var username = rosten.kernel.getUserInforByKey("username");;
					dom.byId("username").value = username;
					dom.byId("displayLoginUser").innerHTML = username;
					var pwd = dom.byId("password");
					connect.connect(pwd,"onkeypress","onPasswordKeyPress");
				});
			onPasswordKeyPress = function(evt){
				if (evt.keyCode == 13)doLogin();
			}
			hideLogin = function(){
				var nl = query(".logindlg",win.body());
				if(nl.length>0){
					var dlg = registry.getEnclosingWidget(nl[0]);
					dlg.hide();
				}
			}
			doLogin = function(){
				var _1 = query(".logindlg",win.body());
				var dlg;
				if(_1.length>0){
					dlg = registry.getEnclosingWidget(_1[0]);
				}
				var form = dom.byId("ajaxLoginForm");
				var ioArgs = {
					form:form,
					handleAs:"text",
					load:function(response,args){
						dom.byId("password").value = "";
						if(response.substring(0,1)=="{" && response.substring(response.length-1)=="}"){
							var json = jsonTool.fromJson(response);
							if(json.success){
								dlg.hide();
								dlg.cp.refresh();
							}else if(json.error){
								var nd = dom.byId("loginMessage");
								nd.innerHTML = json.error;
								domStyle.set(nd,"display","");
							}
						}else{
							dlg.cp.set("content",response);
							dlg.hide();
							var _0 = dom.byId("loginMessage");
							_0.innerHTML = "";
							domStyle.set(_0,"display","none");
						}
					},
					error:function(err,obj){
						var nd = dom.byId("loginMessage");
						nd.innerHTML = err;
						domStyle.set(nd,"display","");
					}
				}
				xhr.post(ioArgs);
			}
	});
</script>
</head>
<body>
	<div id='login' style="height:140px">
		<div class='inner'>
			<form action='${request.contextPath}/j_spring_security_check' method='POST'
       id='ajaxLoginForm' name='ajaxLoginForm' class='cssform'>
      			<div style='display:none;text-align: left;' id='loginMessage'></div>
				<p>
					<label for='username'>用户名:</label>
					<span style="float:left;padding:2px 4px;margin:2px 0;border:1px solid #ddd;width:130px" id="displayLoginUser"></span>
					<input style="display:none" type='text' class='text_' name='j_username' id='username' />
				</p>
				<p>
					<label for='password'>口令:</label>
					<input type='password' class='text_' name='j_password' id='password'  />
				</p>
				<p style="text-align:center;margin:auto">
					<span data-dojo-type="dijit/form/Button" 
						data-dojo-props="iconClass:'okIcon',onClick:function(){doLogin()}">登录
					</span>
					<span data-dojo-type="dijit/form/Button" 
						data-dojo-props="iconClass:'cancelIcon',onClick:function(){hideLogin()}">取消
					</span>
				</p>
			</form>
		</div>
	</div>
</body>
</html>
