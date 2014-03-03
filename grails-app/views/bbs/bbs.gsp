<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>公告栏</title>
    <style type="text/css">
		
    </style>
	<script type="text/javascript">
		require(["dojo/parser",
		 		"dojo/_base/kernel",
		 		"dijit/registry",
		 		"dojo/_base/xhr",
		 		"dojo/date/stamp",
		 		"rosten/widget/DepartUserDialog",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/SimpleTextarea",
		 		"dijit/form/Button",
		 		"dijit/form/DateTextBox",
		 		"dijit/form/FilteringSelect",
		 		"dijit/form/DropDownButton",
		 		"dijit/form/Form",
		 		"dojox/form/Uploader",
		 		"dojox/form/uploader/FileList",
		 		"dijit/Editor",
				"dijit/_editor/plugins/FontChoice",
		     	"rosten/widget/ActionBar",
		     	"rosten/app/BbsManage",
		     	"rosten/app/Application"],
			function(parser,kernel,registry,xhr,datestamp,DepartUserDialog){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}"});
					rosten.cssinit();

					<g:if test="${bbs.id && bbs.id!=null && !"".equals(bbs.id)}">
					
						var ioArgs = {
							url : rosten.webPath + "/bbs/bbsGetContent/${bbs?.id}",
							sync : true,
							handleAs : "text",
							preventCache : true,
							encoding : "utf-8",
							load : function(data) {
								registry.byId("content").set("value",data);
							}
						};
						xhr.get(ioArgs);
					</g:if>
				});
				
			bbs_add = function(){
				var level = registry.byId("level");
				if(!level.isValid()){
					rosten.alert("紧急程度不正确！").queryDlgClose = function(){
						level.focus();
					};
					return;
				}
				var category = registry.byId("category");
				if(!category.isValid()){
					rosten.alert("类型不正确！").queryDlgClose = function(){
						category.focus();
					};
					return;
				}
				var publishDate = registry.byId("publishDate");
				if(!publishDate.isValid()){
					rosten.alert("发布时间不正确！").queryDlgClose = function(){
						publishDate.focus();
					};
					return;
				}
				var topic = registry.byId("topic");
				if(!topic.isValid()){
					rosten.alert("标题不正确！").queryDlgClose = function(){
						topic.focus();
					};
					return;
				}
				var content = {};
				content.level = level.attr("value");
				content.category = category.attr("value");
				content.publishDate = datestamp.toISOString(publishDate.attr("value"),{selector: "date"});
				content.topic = topic.attr("value");
				content.content = registry.byId("content").attr("value");
				content.companyId = "${company?.id }";
				content.id = registry.byId("id").attr("value");
				
				rosten.readSync(rosten.webPath + "/bbs/bbsSave",content,function(data){
					if(data.result=="true" || data.result == true){
						rosten.alert("保存成功！").queryDlgClose= function(){
							window.location.replace(window.location.href + "&id=" + data.id);
							/*
							//刷新当前操作条信息以及表单隐藏字段信息以及流水号信息
							if(data.serialNo){
								registry.byId("serialNo").set("value",data.serialNo);
							}
							var actionBar = registry.byId("rosten_actionBar");
							if(actionBar.actionBarSrc.indexOf(data.id)!=-1){
								actionBar.refresh(actionBar.actionBarSrc);
							}else{
								actionBar.refresh(actionBar.actionBarSrc + "&id=" + data.id);
								
							}
							var bbsFlowLog = registry.byId("bbsFlowLog");
							if(bbsFlowLog.get("href").indexOf(data.id)==-1){
								bbsFlowLog.set("href",bbsFlowLog.get("href") + "/" + data.id)
							}

							var bbsComment = registry.byId("bbsComment");
							if(bbsComment.get("href").indexOf(data.id)==-1){
								bbsComment.set("href",bbsComment.get("href") + "/" + data.id)
							}
							
							registry.byId("id").attr("value",data.id);
							registry.byId("companyId").attr("value",data.companyId);	

							*/						

						};
					}else if(data.result=="noConfig"){
						rosten.alert("系统不存在配置文档，请通知管理员！");
					}else{
						rosten.alert("保存失败!");
					}
				});
			}
			bbs_deal = function(type,readArray){
				var content = {};
				content.id = registry.byId("id").attr("value");
				content.deal = type;
				if(readArray){
					content.dealUser = readArray.join(",");
				}
				rosten.readSync(rosten.webPath + "/bbs/bbsFlowDeal",content,function(data){
					if(data.result=="true" || data.result == true){
						rosten.alert("成功！").queryDlgClose= function(){
							if(type=="agrain"){
								//刷新首页bbs内容
								window.opener.showStartBbs("${user?.id}","${company?.id }");
								//刷新待办事项内容
								window.opener.showStartGtask("${user?.id}","${company?.id }");
							}
							rosten.pagequit();
						}
					}else{
						rosten.alert("失败!");
					}	
				});
			};
			bbs_submit = function(){
				var args ={};
				var obj = {url:rosten.webPath + "/system/userTreeDataStore?companyId=${company?.id }",type:"single"};
	            if(args){
	                if(args.callback)obj.callback = args.callback;
	                if(args.callbackargs) obj.callbackargs = args.callbackargs;
	                if(args.onLoadFunction) obj.onLoadFunction = args.onLoadFunction;
	            }
	            var rostenShowDialog = null;
	            if(rostenShowDialog!=null) rostenShowDialog.destroy();
	            rostenShowDialog = new DepartUserDialog(obj);
	            rostenShowDialog.open();

	            rostenShowDialog.callback = function(data) {
	            	var _data = [];
	            	for (var k = 0; k < data.length; k++) {
	            		var item = data[k];
	            		_data.push(item.value + ":" + item.departId);
	            	};
	            	bbs_deal("submit",_data);	
	            }    
			};
			bbs_agrain = function(){
				bbs_deal("agrain");
			};
			bbs_notAgrain = function(){
				bbs_deal("notAgrain");
			};
			bbs_addComment = function(){
				var bbsId = registry.byId("id").get("value");
				var commentDialog = rosten.addCommentDialog({type:"bbs"});
				commentDialog.callback = function(_data){
					rosten.readSync(rosten.webPath + "/bbs/bbsAddComment/" + bbsId,{dataStr:_data.content,userId:"${user?.id}"},function(data){
						if(data.result=="true" || data.result == true){
							rosten.alert("成功！");
						}else{
							rosten.alert("失败!");
						}	
					});
				};
			};
			bbs_addAttachShow = function(node,jsonObj){
				var a = document.createElement("a");
				a.setAttribute("href", rosten.webPath + "/system/downloadFile/" + jsonObj.fileId);
				a.setAttribute("style","margin-right:20px");
				a.setAttribute("dealId",jsonObj.fileId);
				a.innerHTML = jsonObj.fileName;
				node.appendChild(a);
			};
		});
		
		
    </script>
</head>
<body>
	<div class="rosten_action">
		<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'bbsAction',action:'bbsForm',id:bbs?.id,params:[userid:user?.id])}"'></div>
	</div>
	<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	  	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props=''>
        	<form class="rosten_form" id="rosten_form" onsubmit="return false;" style="text-align:left;margin 0px">
        		<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${bbs?.id }"' />
        		<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
                <table class="tableData" style="width:740px;margin:0px">
                    <tbody>
                       <tr>
						    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>流水号：</div></td>
						    <td width="250">
						    	<input id="serialNo" data-dojo-type="dijit/form/ValidationTextBox" 
				                 	data-dojo-props='name:"serialNo",readOnly:true,
				                 		trim:true,placeHolder:"领导发布后自动生成",
										value:"${bbs?.serialNo}"
				                '/>
						    </td>
						    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>类别：</div></td>
						    <td width="250">
						    	<select id="category" data-dojo-type="dijit/form/FilteringSelect" 
					                data-dojo-props='name:"category",${fieldAcl.isReadOnly("category")},
					                trim:true,
				                 	required:true,
					      			value:"${bbs?.category}"
					            '>
								<option value="公告">公告</option>
								<option value="公文">公文</option>
					    	</select>
				           </td>
						</tr>
						<tr>
							<td>
						    	<div align="right"><span style="color:red">*&nbsp;</span>紧急程度：</div>
				            </td>
				            <td>
				            	<select id="level" data-dojo-type="dijit/form/FilteringSelect"
					                data-dojo-props='name:"level",${fieldAcl.isReadOnly("level")},
					               	trim:true,
				                 	required:true,
					      			value:"${bbs?.level}"
					            '>
									<option value="普通">普通</option>
									<option value="紧急">紧急</option>
									<option value="特急">特急</option>
					    		</select>
				            </td>
						    <td><div align="right"><span style="color:red">*&nbsp;</span>发布时间：</div></td>
						    <td>
						    	<input id="publishDate" data-dojo-type="dijit/form/DateTextBox" 
				                	data-dojo-props='name:"publishDate",${fieldAcl.isReadOnly("publishDate")},
				                	trim:true,
				                 	required:true,
				                	value:"${bbs?.getFormattedPublishDate("date")}"
				               '/>
						    
						   </td>
						        
						</tr>
						<tr>
						    <td><div align="right"><span style="color:red">*&nbsp;</span>标题：</div></td>
						    <td colspan=3>
						    	<input id="topic" data-dojo-type="dijit/form/ValidationTextBox" 
				                 	data-dojo-props='name:"topic",${fieldAcl.isReadOnly("topic")},
				                 		trim:true,
				                 		required:true,
				                 		style:{width:"490px"},
										value:"${bbs?.topic}"
				                '/>
						    
						    </td>    
						</tr>
						<tr>
						    <td><div align="right">内容：</td>
						    <td colspan=3>
						    	
						    	<div data-dojo-type="dijit/Editor" style="overflow:hidden" id="content"
									extraPlugins="[{name:'dijit/_editor/plugins/FontChoice', command: 'fontName', generic: true},'fontSize']"
									data-dojo-props='name:"content"
				            		<g:if test="${fieldAcl.readOnly.contains('content')}">,disabled:true</g:if>
					            '>
									
								</div>
						    						    
						    </td>    
						</tr>
						
                    </tbody>
                </table>
                
		</form>
		<table class="tableData" style="width:740px;margin:0px">
               	<g:if test="${!fieldAcl.readOnly.contains('attach') && bbs.id!=null}">
					
					<tr>
					    <td width="120"><div align="right">附件：</div></td>
					    <td colspan=3>
					    	<div data-dojo-type="dijit/form/DropDownButton" >
								<span>添加附件</span>
								<div data-dojo-type="dijit/TooltipDialog" id="fileUpload_dialog" data-dojo-props="title: 'fileUpload'" style="width:380px">
										<form data-dojo-type="dijit/form/Form" method="post" 
											action="${createLink(controller:'bbs',action:'uploadFile',id:bbs?.id)}" id="fileUpload_form" enctype="multipart/form-data">
											
											<div data-dojo-type="dojox/form/Uploader"  type="file" 
												id="fileUploader"  data-dojo-props="name:'uploadedfile'">添加
												<script type="dojo/method" data-dojo-event="onComplete" data-dojo-args="dataArray">
														if(dataArray.result=="true"){
															dijit.byId("fileUpload_dialog").reset();
															dijit.byId("fileUpload_dialog").onCancel();
															bbs_addAttachShow(dojo.byId("fileShow"),dataArray);
														}else if(dataArray.result=="big"){
															alert("上传文件过大，请重新上传！");
														}else{rosten.alert("上传失败");}
													</script>
											</div>
											
											<div id="fileUpload_fileList" data-dojo-type="dojox/form/uploader/FileList" 
												data-dojo-props='uploaderId:"fileUploader",headerIndex:"#",headerType:"类型",headerFilename:"文件名",headerFilesize:"大小"'></div>
											
											<div class="dijitDialogPaneActionBar">
												<button data-dojo-type="dijit/form/Button" type="reset">重置</button>
												<button data-dojo-type="dijit/form/Button" type="submit">上传
												</button>
												<button data-dojo-type="dijit/form/Button" type="button">取消
													<script type="dojo/method" data-dojo-event="onClick">
															dijit.byId("fileUpload_dialog").onCancel();
														</script>
												</button>
											</div>
										</form>
									
								</div>
							</div>
					    
					    <td>    
					</tr>
					</g:if>
					<tr>
						<td width="120">
							<g:if test="${fieldAcl.readOnly.contains('attach')}">
								<div align="right">附件：</div>
							</g:if>
						</td>
						<td colspan=3>
							<div id="fileShow" style="margin-top:5px;">
								<g:each in="${attachFiles}">
									<a href="${createLink(controller:'system',action:'downloadFile',id:it.id)}" style="margin-right:20px" dealId="${it.id }">${it.name }</a>
								</g:each>
							</div>
						</td>
					</tr>
               </table>
	</div>
	<div data-dojo-type="dijit/layout/ContentPane" id="bbsComment" title="流转意见" data-dojo-props='refreshOnShow:true,
		href:"${createLink(controller:'bbs',action:'getBbsCommentLog',id:bbs?.id)}"
	'>	
	</div>
	<div data-dojo-type="dijit/layout/ContentPane" id="bbsFlowLog" title="流程跟踪" data-dojo-props='refreshOnShow:true,
		href:"${createLink(controller:'bbs',action:'getBbsFlowLog',id:bbs?.id)}"
	'>	
	</div>
</div>
</body>
</html>