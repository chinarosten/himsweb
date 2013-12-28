<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>mail</title>
	<script type="text/javascript">
		require(["dojo/parser",
		        "dojo/_base/kernel",
		        "rosten/widget/ActionBar",
		 		"dojox/grid/DataGrid",
				"dijit/layout/BorderContainer",
				"dijit/layout/TabContainer",
				"dijit/Toolbar",
				"dijit/form/Button",
				"dijit/layout/ContentPane",
				"dojo/data/ItemFileWriteStore"
		     	],
			function(parser,kernel,ActionBar){
				kernel.addOnLoad(function(){
					
				});
		});
    </script>
</head>
<body>
	<div data-dojo-type="dojo/data/ItemFileWriteStore" data-dojo-id="mailStore" url="${createLink(controller:'mail',action:'mailData')}"></div>
	<div data-dojo-type="dijit/layout/BorderContainer">
		<div data-dojo-type="dijit/layout/TabContainer" id="mail_tabs" data-dojo-id="mail_tabs" region="center" tabStrip="true" >
			<div data-dojo-type="dijit/layout/BorderContainer" title="收件箱" id="mail_inbox">
				
				<div data-dojo-type="dijit/Toolbar" region="top" >
					<button data-dojo-type="dijit/form/Button" iconClass="mailIconNewMessage">回复
						<script type="dojo/method" data-dojo-event="onClick">
								onMessageDbClick();
							</script>
					</button>
					
				</div>
				<div data-dojo-type="dijit/layout/BorderContainer"  region="center" style="padding:0px" >
					<!-- list of messages pane -->
					<table data-dojo-type="dojox/grid/DataGrid"
						region="top" 
						data-dojo-id="mail_table"
						id="mail_table"
						store="mailStore" query="{ type: 'message' ,folder: 'inbox'}"
						onRowClick="onMessageClick"
						onRowDblClick = "onMessageDbClick"
						style="height: 320px;padding:0px">
						<thead>
							<tr>
								<th field="sender" width="10%">发件人</th>
								<th field="label" width="80%">主题</th>
								<th field="sent" width="10%" formatter="formatDate">发件时间</th>
							</tr>
						</thead>
					</table>
					<!-- end of listPane -->
	
					<!-- message preview pane -->
					<div id="mail_message" data-dojo-type="dijit/layout/ContentPane" region="center" >
						
					</div>
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
							<th field="first" width="25%" editable="true">First Name</th>
							<th field="last" width="25%" editable="true">Last Name</th>
							<th field="email" width="50%" editable="true">Email</th>
						</tr>
					</thead>
				</table>
				<div dojoType="dijit/layout/ContentPane" region="bottom" >
					Edit your contact information from this page by clicking entries in the table above.
				</div>
			</div>
		</div>	
	</div>	
</body>
</html>