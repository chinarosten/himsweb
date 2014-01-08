<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>quick</title>
    <style type="text/css">
    	/* Icons */

		.mailIconCancel,
		.mailIconOptions,
		.mailIconFolderDocuments,
		.mailIconFolderInbox,
		.mailIconFolderSent,
		.mailIconGetMail,
		.mailIconNewMessage,
		.mailIconMailbox,
		.mailIconOk,
		.mailIconTrashcanFull {
			background-image: url('../images/rosten/share/icons.png');  /*mail icons sprite image */
			background-repeat: no-repeat; 
			width: 16px;
			height: 16px;
			text-align: center;
			padding-right:4px; 
		}
		
		.dj_ie6 .mailIconCancel,
		.dj_ie6 .mailIconOptions,
		.dj_ie6 .mailIconFolderDocuments,
		.dj_ie6 .mailIconFolderInbox,
		.dj_ie6 .mailIconFolderSent,
		.dj_ie6 .mailIconGetMail,
		.dj_ie6 .mailIconNewMessage,
		.dj_ie6 .mailIconMailbox,
		.dj_ie6 .mailIconOk,
		.dj_ie6 .mailIconTrashcanFull {
			 background-image: url('../images/rosten/share/icons.gif');
		}
		
		
		.mailIconCancel { background-position: 0px; }
		.mailIconOptions { background-position: -22px; }
		.mailIconFolderDocuments { background-position: -44px; }
		.mailIconFolderInbox { background-position: -66px; }
		.mailIconFolderSent { background-position: -88px; }
		.mailIconGetMail { background-position: -110px; }
		.mailIconNewMessage { background-position: -132px; }
		.mailIconMailbox { background-position: -154px; }
		.mailIconOk { background-position: -176px; }
		.mailIconTrashcanFull { background-position: -198px; }
    </style>
	<script type="text/javascript">
	
		require([
		 		"dijit/Toolbar",
		 		"dijit/form/ComboButton",
		 		"dijit/Menu",
		 		"dijit/MenuItem",
				"dijit/form/Button",
				"rosten/app/Mail"
		     	]);
    </script>
</head>
<body>

	<div data-dojo-type="dijit/Toolbar" region="top">
		<div id="mail_getMail" data-dojo-type="dijit/form/ComboButton"
			iconClass="mailIconGetMail" optionsTitle="Mail Source Options">
			<script type="dojo/method" data-dojo-event="onClick">
				/* fakeDownload(); */
				rosten.alert("暂未开通,敬请关注...");
			</script>
			<span>收件</span>
			<ul data-dojo-type="dijit/Menu">
				<li data-dojo-type="dijit/MenuItem" iconClass="mailIconGetMail">Yahoo</li>
				<li data-dojo-type="dijit/MenuItem" iconClass="mailIconGetMail">GMail</li>
			</ul>
		</div>
		<button id="mail_newMsg" data-dojo-type="dijit/form/Button" iconClass="mailIconNewMessage">写信
			<script type="dojo/method" data-dojo-event="onClick">
				write_mail();
			</script>
		</button>
	</div>

</body>
</html>