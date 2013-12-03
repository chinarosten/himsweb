<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>${logoname}</title>
<meta name="layout" content="rosten" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
   	logout = function(){
    	var url = "${createLink(controller:'logout')}";
		window.location = url;
	}
	require(["dojo/parser", 
	     	"dojo/_base/kernel", 
	     	"dojo/json",
	     	"dojo/_base/lang",
	     	"dojo/query",
	     	"dojo/_base/window",
	     	"dojo/dom-style",
	     	"dojo/dom-class",
	     	"dojo/dom-construct",
	     	"dijit/registry",
	     	"dojox/layout/ContentPane",
	     	"dijit/Dialog",
	     	"rosten/app/Main" ],
		function(parser, kernel,JSON,lang,query,win,domStyle,domClass,domConstruct,registry,ContentPane,Dialog) {
			kernel.addOnLoad(function() {
				var data = JSON.parse('${userinfor}');
	   	   		var naviJson = {
		            naviMenuSrc: "${createLink(controller:'system',action:'naviMenu',id:user.id)}",
		            type: "stand"
		        };
	   	   		rosten.init({webpath : "/himsweb",dojogridcss : true,rostencss : "normal"});
				//initInstance(naviJson,data);
			});
			lang.extend(ContentPane,{
	   			onDownloadError:function(error){
	   				if(error.status=="401"){
	   					var dlg
	   					var nl = query(".logindlg",win.body())
	   					if(nl.length>0){
	   						dlg = registry.getEnclosingWidget(nl[0]);
	   					}else{
	   						dlg = new Dialog({title:"用户登录"},document.createElement("div"))
	   						domConstruct.place(dlg.domNode,dojo.body());
	   						domStyle.set(dlg.domNode,"height","185px");
	   						domStyle.set(dlg.domNode,"width","280px");
	   						domClass.add(dlg.domNode,"logindlg");
	   						var div = document.createElement("div");
	   						domConstruct.place(div,dlg.containerNode);
	   						var cp1 = new ContentPane({executeScripts:true,renderStyles:true},div)
	   						cp1.attr("href","${createLink(controller:'login',action:'dlgauth')}");
	   					}
	   					dlg.cp = this;
	   					dlg.show();
	   					return "<span class='dijitContentPaneError'>页面失效，请刷新页面并重新登录！</span>";
	   				}else return this.errorMessage;
	   			}
			});
		});
</script>
</head>
<body>
	<div data-dojo-type="dijit/layout/BorderContainer" id="container"
		data-dojo-props='gutters:false'>
		<div data-dojo-type="dijit/layout/ContentPane" id="header"
			data-dojo-props='region:"top"'>
			<div class="headerTop">
				<table width="100%" border="0" margin=0 cellpadding="0"
					cellspacing="0">
					<tr width="100%">
						<td valign="top" class="headerLogo"></td>
						<td valign="top">
							<div class="nav verticalAlign">
								<span class="nav0Icon">&nbsp;</span> <span class="nav0Div">&nbsp;<span
									id="header_username">
										${(user.chinaName!=null?user.chinaName:user.username) + "&nbsp;" + usertype}
								</span>&nbsp;&nbsp;欢迎您的到来！
								</span> <span class="nav5Icon">&nbsp;</span> <span class="nav5Div"><a
									href="javascript:changeSkin();">更换皮肤</a></span> <span class="nav4Icon">&nbsp;</span>
								<span class="nav4Div"><a href="javascript:addBookmark();">添加为书签</a></span>
								<g:if test="${normal}">
									<span class="nav3Icon">&nbsp;</span>
									<span class="nav3Div"><a
										href="javascript:chgPassword();">密码修改</a></span>
								</g:if>
								<span class="nav2Icon">&nbsp;</span> <span class="nav2Div"><a
									href="javascript:logout();">注销</a></span> <span class="nav1Icon">&nbsp;</span>
								<span class="nav1Div"><a href="javascript:quit();">退出</a></span>
							</div>
						</td>
					</tr>
				</table>
			</div>
			<div class="headerMenu">
				<div
					style="float: left; width: 216px; text-align: center; padding: 5px 0 0 0; color: #004c7e;"
					id="header_time">
					<g:formatDate format="yyyy-MM-dd ( EEE )" date="${new Date()}" />
				</div>
				<div class="naviMenuList">
					<ul id="naviMenuUL">
					</ul>
				</div>
			</div>
		</div>

		<div data-dojo-type="dijit/layout/BorderContainer"
			data-dojo-props='region:"center",splitter:true,style:{padding:"1px 1px 0px 1px",height:"100%"}'>
			<div data-dojo-type="dijit/layout/BorderContainer" id="sideBar"
				data-dojo-props='region:"left",minSize:"210",splitter:true,gutters:false,"class":"sideBar",style:"padding:0"'>

				<div id="navigationContainer"
					data-dojo-type="dijit/layout/AccordionContainer"
					data-dojo-props='region:"center"'>
					<div id="navigation" data-dojo-type="dijit/layout/ContentPane"
						data-dojo-props='style:"padding:0px"'>
						<div
							style="text-align: center; position: absolute; top: 30%; margin-left: 20px"
							class="verticalAlign">
							<img src="images/rosten/share/wait_big.gif" alt="waiting..."></img>
							<span>&nbsp;&nbsp;请稍候,正在获取内容...</span>
						</div>
					</div>
				</div>
			</div>

			<div data-dojo-type="dojox/layout/ContentPane" id="contentBody"
				data-dojo-props='region:"center",executeScripts:true,renderStyles:true,style:"padding:1px"'>
				<div
					style="text-align: center; position: absolute; top: 30%; left: 30%"
					class="verticalAlign">
					<img src="images/rosten/share/wait_big.gif" alt="waiting..."></img>
					<span>&nbsp;&nbsp;请稍候,正在获取内容...</span>
				</div>
			</div>
		</div>
		<div data-dojo-type="dijit/layout/ContentPane" id="footer"
			data-dojo-props='region:"bottom"'>Copyright @2012 ; rosten
			版权所有,提供技术支持</div>
	</div>
</body>
</html>
