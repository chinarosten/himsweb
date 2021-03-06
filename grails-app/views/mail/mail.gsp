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
		.dojoxUploaderFileList{
			border:1px solid #ccc;
			min-height:50px;
		}
		.dojoxUploaderFileListTable{
			width:100%;
			border-collapse:collapse;
			margin-top:5px;
		}
		.dojoxUploaderFileListHeader th{
			background-color:#eee;
			padding:3px;
		}
		.dojoxUploaderFileListRow{
		
		}
		.dojoxUploaderIndex{
			width:20px;
		}
		.dojoxUploaderIcon{
			width:50px;
		}
		.dojoxUploaderFileName{
		
		}
		.dojoxUploaderSize{
			width:70px;
		}
		.dojoxUploaderFileListContent{
			width:100%;
		}
		.dojoxUploaderFileListProgress{
			border:1px solid #666;
			height:15px;
			position:relative;
			background:#fff;
			overflow:hidden;
		}
		.dojoxUploaderFileListPercentText{
			position:absolute;
			right:3px;
			top:3px;
			font-size:10px;
			text-align:right;
		}
		.dojoxUploaderFileListProgressBar{
			position:absolute;
			top:0px;
			left:0px;
			height:15px;
			width:0%;
			background:#bfe1fd;
		}
		.rosten .tooltipLink .dijitButtonNode,
		.rosten .tooltipLink .dijitButtonNode .dijitDropDownButtonHover,
		.rosten .buttonLink .dijitButtonNode,
		.rosten .buttonLink .dijitButtonHover .dijitButtonNode {
			background: none !important;
			border:none;
		}
		.rosten .tooltipLink .dijitArrowButtonInner {
			display:none;
		}
		.rosten .tooltipLink button,
		.rosten .tooltipLink button .dijitButtonText,
		.rosten .buttonLink button .dijitButtonText {
			text-decoration:underline !important;
			color:blue;
			display:inline;
		}
    </style>
	<script type="text/javascript">
		require(["dojo/parser",
		        "dojo/_base/kernel",
		        "rosten/widget/RostenLinkTree",
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
				"dijit/form/DropDownButton",
				'dojox/form/Uploader',
				'dojox/form/uploader/FileList',
				"dijit/form/Form",
				"dijit/TooltipDialog",
				"rosten/app/Mail"
		     	],
			function(parser,kernel,ActionBar,RostenLinkTree){

			mail_addSendInfor = function(item){
				if(rosten.variable.mailTargetNode == undefined) return;
				var type = item.type;
				var name = item.name;
				var typeStr = "";

				if(type[0] == "depart"){
					typeStr = "(部门)";
				}else if(type[0] == "group"){
					typeStr = "(群组)";
				}
				
				var inputValue = rosten.variable.mailTargetNode.attr("value");
				if(inputValue==""){
					rosten.variable.mailTargetNode.attr("value",name[0] + typeStr);
				}else{
					rosten.variable.mailTargetNode.attr("value",inputValue + "," + name[0] + typeStr);
				}
			};
			mail_onClick = function(item,store){
				if(!item || item.root)return;
				if(rosten.variable.mailTargetNode == undefined) return;

				var type = store.getValue(item, "type");
				if(type=="depart" || type =="group") return;
					
				var username = store.getValue(item, "username");
				var inputValue = rosten.variable.mailTargetNode.attr("value");
				
				if(inputValue==""){
					rosten.variable.mailTargetNode.attr("value",username);
				}else{
					rosten.variable.mailTargetNode.attr("value",inputValue + "," + username);
				}
			};
			mail_onOpen = function(item,store){
				if( item && !item.root && item.children.length == 0){
					mail_addDepart(item,store);
				}
			};
			
		});
    </script>
</head>
<body>
	<div data-dojo-type="dijit/Dialog" id="sendDialog" title="邮件处理">
		<div id="sendMailBar" style="text-align:center">
			<div  id="fakeSend" data-dojo-type="dijit/ProgressBar" style="height:15px; width:175px;" indeterminate="true" ></div>
		</div>
	</div>	
	<div data-dojo-type="dojo/data/ItemFileWriteStore" data-dojo-id="contactStore" url="${createLink(controller:'mail',action:'contactData')}"></div>
	
	<div data-dojo-type="dijit/layout/BorderContainer" data-dojo-props='style:"padding:0px"'>>
		<div data-dojo-type="dijit/layout/TabContainer" id="mail_tabs" data-dojo-id="mail_tabs" region="center" tabStrip="true" >
			<div data-dojo-type="dijit/layout/BorderContainer" title="收件箱" id="mail_inbox" data-dojo-props='style:"padding:1px"'>
				
				<div data-dojo-type="dijit/layout/ContentPane" region="top" data-dojo-props='style:"padding:0px;height:280px",splitter:true'>
					<div class="rosten_action">
						<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="mail_actionBar" 
							data-dojo-props='actionBarSrc:"${createLink(controller:'mailAction',action:'inbox')}"'>
						</div>
					</div>
					<div data-dojo-type="rosten/widget/RostenGrid" data-dojo-id="mail_grid" 
						data-dojo-props='url:"${createLink(controller:'mail',action:'inboxGrid')}",showRowSelector : "new"'>
						 <script type="dojo/method" data-dojo-event="onCellClick" data-dojo-args="cell">
							onMessageClick(cell)
						</script>
					</div>
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
				<div class="rosten_action">
					<div data-dojo-type="rosten/widget/ActionBar" data-dojo-attach-point="actionBar"
						data-dojo-props='actionBarSrc:"${createLink(controller:'mailAction',action:'newMessage')}"'>
					</div>
				</div>
				<div style="overflow: visible; z-index: 10; color:#666;margin-top:8px">
					<table width="100%">
						<tr style="padding-top:5px;">
							<td style="width:100px; text-align:right;"><label>收件人:</label></td>
							<td>
								<div data-dojo-type="dijit/form/ComboBox" 
									data-dojo-attach-point="to" hasDownArrow="false" store="contactStore" searchAttr="email"
									style="width: 40em;margin-left:2px">
									<script type="dojo/method" data-dojo-event="onClick" data-dojo-args="event">
										rosten.variable.mailTargetNode = this;
									</script>
								</div>	
							</td>
						</tr>
						<tr>
							<td style="text-align:right;"><label>主题:</label></td>
							<td>
								<select data-dojo-type="dijit/form/ComboBox" 
									data-dojo-attach-point="subject" hasDownArrow="false" style="width: 40em;margin-left:2px">
									<option></option>
									<option>会议</option>
									<option>报告</option>
								</select>
							</td>
						</tr>
						<tr>
							<td style="text-align:right;"><label>附件:</label></td>
							<td>
								<div data-dojo-type="dijit/form/DropDownButton" >
								<!--<div data-dojo-type="dijit/form/DropDownButton" data-dojo-props='dropDownPosition:["below-centered","above-centered"],class:"tooltipLink"' >  -->
									<span>添加附件</span>
									<div data-dojo-type="dijit/TooltipDialog" id="fileUpload_dialog_${'\${departid}'}" data-dojo-props="title: 'fileUpload'" style="width:380px">
											<form data-dojo-type="dijit/form/Form" method="post" 
												action="${createLink(controller:'mail',action:'uploadFile')}" id="fileUpload_form_${'\${departid}'}" enctype="multipart/form-data">
												
												<div data-dojo-type="dojox/form/Uploader"  type="file" 
													id="fileUploader_${'\${departid}'}"  data-dojo-props="name:'uploadedfile'">添加
													<script type="dojo/method" data-dojo-event="onComplete" data-dojo-args="dataArray">
														if(dataArray.result=="true"){
															dijit.byId("fileUpload_dialog_${'\${departid}'}").reset();
															dijit.byId("fileUpload_dialog_${'\${departid}'}").onCancel();
															mail_addAttachShow(dojo.byId("fileShow_${'\${departid}'}"),dataArray,true);
														}else if(dataArray.result=="big"){
															alert("上传文件过大，请重新上传！");
														}else{rosten.alert("上传失败");}
													</script>
												</div>
												
												<div id="fileUpload_fileList_${'\${departid}'}" data-dojo-type="dojox/form/uploader/FileList" 
													data-dojo-props='uploaderId:"fileUploader_${'\${departid}'}",headerIndex:"#",headerType:"类型",headerFilename:"文件名",headerFilesize:"大小"'></div>
												
												<div class="dijitDialogPaneActionBar">
													<button data-dojo-type="dijit/form/Button" type="reset">重置</button>
													<button data-dojo-type="dijit/form/Button" type="submit">上传</button>
													<button data-dojo-type="dijit/form/Button" type="button">取消
														<script type="dojo/method" data-dojo-event="onClick">
															dijit.byId("fileUpload_dialog_${'\${departid}'}").onCancel();
														</script>
													</button>
												</div>
											</form>
										
									</div>
								</div>
								
							</td>
						</tr>
						<tr><td></td>
						<td>
							<div id="fileShow_${'\${departid}'}" class="attachFile" style="margin-top:5px;" data-dojo-attach-point="attachFile"></div>
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
			<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='region:"trailing", style:"width: 260px;", splitter:true'>
				<div data-dojo-type="dijit/layout/ContentPane" data-dojo-props='title:"通讯录"'>
					<div data-dojo-type="rosten/widget/RostenLinkTree"  
						data-dojo-props='url:"${createLink(controller:'mail',action:'getDepart')}",linkName:"添加部门",linkShowType:"depart",
							linkFunction:mail_addSendInfor,onClick:mail_onClick,onOpen:mail_onOpen'>
            		</div>
				</div>
				
				<div data-dojo-type="dijit/layout/ContentPane" data-dojo-props='title:"用户群组"'>
					<div data-dojo-type="rosten/widget/RostenLinkTree"  
						data-dojo-props='url:"${createLink(controller:'mail',action:'getGroup')}",linkName:"添加群组",linkShowType:"group",
						onClick:mail_onClick,linkFunction:mail_addSendInfor'>
            		</div>
				</div>	
				  
			</div>
			<div data-dojo-type="dijit/layout/ContentPane" region="bottom" style="height:30px" align="center">
				<button data-dojo-type="dijit/form/Button" iconClass="mailIconOk" data-dojo-attach-point="sendButton">发送</button>
				<button data-dojo-type="dijit/form/Button" iconClass="mailIconCancel" >取消
				<script type="dojo/method" data-dojo-event="onClick">
					cancel_mail();
				</script>
				</button>
			</div>
		</div>
	</div>
		
	<div data-dojo-type="dijit/Declaration" widgetClass="mail.showMessage">
		<div data-dojo-type="dijit/layout/BorderContainer" data-dojo-attach-point="container" title="Composing..." closeable="true" style="padding:1px">
			<div data-dojo-type="dijit/layout/ContentPane" region="top" data-dojo-props='style:"padding:0px;height:140px"'>
				<div class="rosten_action">
					<div data-dojo-type="rosten/widget/ActionBar" data-dojo-attach-point="actionBar"
						data-dojo-props='actionBarSrc:"${createLink(controller:'mailAction',action:'showMessage')}/${'\${mailnavigation}'}" '>
					</div>
				</div>
				<div style="overflow: visible; z-index: 10; color:#666;margin-top:8px">
					<table width="100%">
						<tr style="padding-top:5px;">
							<td style="text-align:right;"><label>主题:</label></td>
							<td>
								<div data-dojo-attach-point="subject" style="margin-left:10px"></div>
							</td>
						</tr>
						<tr style="padding-top:5px;">
							<td style="width:100px; text-align:right;"><label>发件人:</label></td>
							<td>
								<div data-dojo-attach-point="sender" style="margin-left:10px"></div>
							</td>
						</tr>
						<tr style="padding-top:5px;">
							<td style="width:100px; text-align:right;"><label>收件人:</label></td>
							<td>
								<div data-dojo-attach-point="to" style="margin-left:10px"></div>
							</td>
						</tr>
						<tr style="padding-top:5px;">
							<td style="text-align:right;"><label>时间:</label></td>
							<td>
								<div data-dojo-attach-point="sent" style="margin-left:10px"></div>
							</td>
						</tr>
						<tr data-dojo-attach-point="fileNodeTr" style="display:none;padding-top:5px;">
							<td style="text-align:right;"><label>附件:</label></td>
							<td>
								<div data-dojo-attach-point="fileNode" class="attachFile" style="margin-left:10px"></div>
								<div data-dojo-attach-point="fileNodeStr" style="display:none"></div>
							</td>
						</tr>
					</table>
					<hr noshade size="1">
				</div>
				
			</div>
			<div data-dojo-type="dijit/layout/ContentPane" region="center" data-dojo-props='style:"padding:10px"'>
				<div data-dojo-attach-point="content">
				</div>
			</div>
		</div>
	</div>
	
</body>
</html>