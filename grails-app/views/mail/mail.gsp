<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>mail</title>
    <style type="text/css">
    	/* buttons on Contact tab for filtering list of contacts to a given letter */
		.contactIndex, .contactIndexAll {
			margin: 0.5em;
			color: blue;
			font-style: italic;
			vertical-align: middle;
			cursor: pointer;
		}
		.contactIndexAll {
			/* for the word ALL that appears before and after the letters A-Z */
			color: purple;
		}
		.contactIndexSelected {
			/* the most recently selected letter gets this class too */
			color: red;
		}
    </style>
	<script type="text/javascript">
		require(["dojo/parser",
		        "dojo/_base/kernel",
		        "dojo/data/ItemFileWriteStore",
		        "rosten/widget/ActionBar",
		        "rosten/widget/RostenGrid",
		 		"dojox/grid/DataGrid",
				"dijit/layout/BorderContainer",
				"dijit/layout/TabContainer",
				"dijit/Toolbar",
				"dijit/Declaration",
				"dijit/form/Button",
				"dijit/form/ComboBox",
				"dijit/layout/ContentPane",
				"dijit/Editor",
				"dijit/_editor/plugins/FontChoice",
				"dijit/Dialog",
				"dijit/ProgressBar",
				"dijit/form/ValidationTextBox",
				"rosten/app/Mail"
		     	],
			function(parser,kernel,ActionBar){
				//kernel.addOnLoad(function(){
					 //genIndex();
				//});
		});
    </script>
</head>
<body>
	<div data-dojo-type="dojo/data/ItemFileWriteStore" data-dojo-id="contactStore" url="${createLink(controller:'mail',action:'contactData')}"></div>
	
	<div data-dojo-type="dijit/layout/BorderContainer" data-dojo-props='style:"padding:0px"'>>
		<div data-dojo-type="dijit/layout/TabContainer" id="mail_tabs" data-dojo-id="mail_tabs" region="center" tabStrip="true" >
			<div data-dojo-type="dijit/layout/BorderContainer" title="收件箱" id="mail_inbox" data-dojo-props='style:"padding:1px"'>
				
				<div data-dojo-type="dijit/layout/ContentPane" region="top" data-dojo-props='style:"padding:0px;height:350px",splitter:true'>
					<div class="rosten_action">
						<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="mail_actionBar" 
							data-dojo-props='actionBarSrc:"${createLink(controller:'mailAction',action:'inbox')}"'>
						</div>
					</div>
					<div data-dojo-type="rosten/widget/RostenGrid" data-dojo-id="mail_grid" 
						data-dojo-props='url:"${createLink(controller:'mail',action:'inboxGrid')}",showRowSelector : "new"'></div>
				</div>
				<!-- message preview pane -->
				<div id="mail_message" data-dojo-type="dijit/layout/ContentPane" data-dojo-props='region: "center"'>
					
				</div>
				
				<!-- end of "message" -->
			</div>
			
			<div dojoType="dijit/layout/BorderContainer" title="通讯录">
				<div dojoType="dijit/layout/ContentPane" region="top">
					<center id="contactIndex"></center>
				</div>
				<table dojoType="dojox/grid/DataGrid" data-dojo-id="contactTable" store="contactStore" region="center" editable="true">
					<thead>
						<tr>
							<th field="name" width="25%" editable="true">姓名</th>
							<th field="email" width="50%" editable="true">邮件地址</th>
							<th field="phone" width="25%" editable="true">手机号码</th>
						</tr>
					</thead>
				</table>
				<div dojoType="dijit/layout/ContentPane" region="bottom" >
					Edit your contact information from this page by clicking entries in the table above.
				</div>
			</div>
		</div>	
	</div>
	
	<div data-dojo-type="dijit/Declaration" widgetClass="mail.NewMessage">
		<div data-dojo-type="dijit/layout/BorderContainer" data-dojo-attach-point="container" title="Composing..." closeable="true" style="padding:1px">
			<div data-dojo-type="dijit/layout/ContentPane" region="center" data-dojo-props='style:"padding:0px"'>
				<input style="display:none" data-dojo-type="dijit/form/ValidationTextBox" data-dojo-attach-point="msgId"></input>
				<div class="rosten_action">
					<div data-dojo-type="rosten/widget/ActionBar"
						data-dojo-props='actionBarSrc:"${createLink(controller:'mailAction',action:'inbox')}"'>
					</div>
				</div>
				<div style="height:60px; overflow: visible; z-index: 10; color:#666;">
					<table width="100%">
						<tr style="padding-top:5px;">
							<td style="width:100px; text-align:right;"><label>收件人:</label></td>
							<td>
								<input data-dojo-type="dijit/form/ComboBox" data-dojo-attach-point="to" hasDownArrow="false" store="contactStore" searchAttr="display"
									style="width: 40em;">
							</td>
						</tr>
						<tr>
							<td style="text-align:right;"><label>主题:</label></td>
							<td>
								<select data-dojo-type="dijit/form/ComboBox" data-dojo-attach-point="subject" hasDownArrow="false" style="width: 40em;">
									<option></option>
									<option>会议</option>
									<option>报告</option>
								</select>
							</td>
						</tr>
					</table>
					<hr noshade size="1">
				</div>
				<!-- new message part -->
				<div data-dojo-type="dijit/Editor"style="overflow:hidden" data-dojo-attach-point="content"
					extraPlugins="[{name:'dijit/_editor/plugins/FontChoice', command: 'fontName', generic: true},'fontSize']">
	
				</div>
			</div>
			
			<div data-dojo-type="dijit/layout/ContentPane" region="bottom" style="height:30px" align="center">
				<button data-dojo-type="dijit/form/Button" iconClass="mailIconOk" data-dojo-attach-point="sendButton">发送</button>
				<button data-dojo-type="dijit/form/Button" iconClass="mailIconCancel" >取消
				<script type="dojo/method" data-dojo-event="onClick">
					mail_tabs.closeChild(mail_tabs.selectedChildWidget);
				</script>
				</button>
			</div>
		</div>
	</div>
	<div data-dojo-type="dijit/Dialog" id="sendDialog" title="发送邮件">
		<div id="sendMailBar" style="text-align:center">
			<div  id="fakeSend" data-dojo-type="dijit/ProgressBar" style="height:15px; width:175px;" indeterminate="true" ></div>
		</div>
	</div>		
	
		
</body>
</html>