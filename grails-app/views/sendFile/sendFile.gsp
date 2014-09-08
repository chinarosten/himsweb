<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>发文管理</title>
    <style type="text/css">
    	.rosten .sendfile_form table tr{
    		height:30px;
    	}
    	body{
			overflow:auto;
		}
    </style>
	<script type="text/javascript">
	require(["dojo/parser",
		 		"dojo/_base/kernel",
		 		"dijit/registry",
		 		"dojo/_base/xhr",
		 		"dojo/date/stamp",
		 		"rosten/widget/DepartUserDialog",
		 		"dijit/layout/TabContainer",
		 		"dijit/layout/ContentPane",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/SimpleTextarea",
		 		"dijit/form/Button",
		 		"dijit/form/DateTextBox",
		 		"dijit/form/NumberTextBox",
		 		"dijit/form/FilteringSelect",
		 		"dijit/form/DropDownButton",
		 		"dijit/form/Form",
		 		"dojox/form/Uploader",
		 		"dojox/form/uploader/FileList",
		     	"rosten/widget/ActionBar",
		     	"rosten/widget/TitlePane",
		     	"rosten/app/Application",
		     	"rosten/kernel/behavior"],
			function(parser,kernel,registry,xhr,datestamp,DepartUserDialog){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}"});
					rosten.cssinit();
				});
				sendfile_add = function(object){
					var chenkids = ["fileType","dealDepart","title","mainSend"];
					if(!rosten.checkData(chenkids)) return;

					//增加对多次单击的次数----2014-9-4
					var buttonWidget = object.target;
					rosten.toggleAction(buttonWidget,true);
					
					rosten.readSync(rosten.webPath + "/sendFile/sendFileSave",{},function(data){
						if(data.result=="true" || data.result == true){
							rosten.alert("保存成功！").queryDlgClose= function(){
								if(window.location.href.indexOf(data.id)==-1){
									window.location.replace(window.location.href + "&id=" + data.id);
								}else{
									window.location.reload();
								}
							};
						}else if(data.result=="noConfig"){
							rosten.alert("系统不存在配置文档，请通知管理员！");
						}else if(data.result=="noSendFileLabel"){
							rosten.alert("系统不存在对应的发文代字，请通知管理员！");
						}else{
							rosten.alert("保存失败!");
						}
						rosten.toggleAction(buttonWidget,false);
					},function(error){
						rosten.alert("系统错误，请通知管理员！");
						rosten.toggleAction(buttonWidget,false);
					},"sendfile_form");
					
				};
				sendfile_deal = function(type,readArray,buttonWidget){
					var content = {};
					content.id = registry.byId("id").attr("value");
					content.deal = type;
					if(readArray){
						content.dealUser = readArray.join(",");
					}
					content.fileTypeId = registry.byId("fileType").attr("value");
					rosten.readSync(rosten.webPath + "/sendFile/sendFileFlowDeal",content,function(data){
						if(data.result=="true" || data.result == true){
							rosten.alert("成功！").queryDlgClose= function(){
								if(type=="send"){
									window.opener.showStartGtask("${user?.id}","${company?.id }");
									return;
								}
								//刷新待办事项
								window.opener.showStartGtask("${user?.id}","${company?.id }");

								if(data.refresh=="true" || data.refresh==true){
									window.location.reload();
								}else{
									rosten.pagequit();
								}
							}
						}else{
							rosten.alert("失败!");
							rosten.toggleAction(buttonWidget,false);
						}	
					},function(error){
						rosten.alert("系统错误，请通知管理员！");
						rosten.toggleAction(buttonWidget,false);
					});
				};
				sendfile_submit_select = function(url,buttonWidget){
					var rostenShowDialog = rosten.selectFlowUser(url,"single");
		            rostenShowDialog.callback = function(data) {
		            	if(data.length==0){
			            	rosten.alert("请正确选择人员！");
		            		rosten.toggleAction(buttonWidget,false);
			            }else{
			            	var _data = [];
			            	for (var k = 0; k < data.length; k++) {
			            		var item = data[k];
			            		_data.push(item.value + ":" + item.departId);
			            	};
			            	sendfile_deal("submit",_data,buttonWidget);	
				       	}
		            };
					rostenShowDialog.afterLoad = function(){
						var _data = rostenShowDialog.getData();
			            if(_data && _data.length==1){
				            //直接调用
			            	rostenShowDialog.doAction();
				        }else{
							//显示对话框
							rostenShowDialog.open();
					    }
					};
					rostenShowDialog.queryDlgClose = function(){
						rosten.toggleAction(buttonWidget,false);
					};
				};
				sendfile_submit = function(object){
					//判断是否已经创建word文档
					
					if("${hasWordOLE}"=="false"){
						rosten.alert("请先创建正文！");
						return;
					}
					
					//从后台获取下一处理人
					//增加对多次单击的次数----2014-9-4
					var buttonWidget = object.target;
					rosten.toggleAction(buttonWidget,true);
					
					var content = {};
					rosten.readSync("${createLink(controller:'sendFile',action:'getSelectFlowUser',params:[companyId:company?.id,id:sendFile?.id])}",content,function(data){
						if(data.dealFlow==false){
							//流程无下一节点
							sendfile_deal("submit",null,buttonWidget);
							return;
						}
						var url = "${createLink(controller:'system',action:'userTreeDataStore',params:[companyId:company?.id])}";
						if(data.dealType=="user"){
							//人员处理
							if(data.showDialog==false){
								//单一处理人
								var _data = [];
								_data.push(data.userId + ":" + data.userDepart);
								sendfile_deal("submit",_data,buttonWidget);
							}else{
								//多人，多部门处理
								url += "&type=user&user=" + data.user;
								sendfile_submit_select(url,buttonWidget);
							}
						}else{
							//群组处理
							url += "&type=group&groupIds=" + data.groupIds;
							if(data.limitDepart){
								url += "&limitDepart="+data.limitDepart;
							}
							sendfile_submit_select(encodeURI(url),buttonWidget);
						}

					},function(error){
						rosten.alert("系统错误，请通知管理员！");
						rosten.toggleAction(buttonWidget,false);
					});
				};
				sendfile_achive = function(){
					var isSend = registry.byId("isSend").attr("value");
					if(isSend=="false" || isSend == false){
						rosten.alert("当前发文尚未分发，请先分发！");
						return;
					}
					sendfile_deal("achive");
				};
				sendfile_send = function(){
					var args ={};
					var obj = {url:rosten.webPath + "/system/userTreeDataStore?companyId=${company?.id }"};
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
		            		_data.push(item.value + ":" + item.departName);
		            	};
		            	sendfile_deal("send",_data);	
		            }  
				};
				sendFile_addWord = function(){
					rosten.openNewWindow("sendFile_addWord", rosten.webPath + "/sendFile/addWord/${sendFile?.id }");
				};
				sendFile_readWord = function(){
					rosten.openNewWindow("sendFile_readWord", rosten.webPath + "/sendFile/addWord/${sendFile?.id }");
				};
				page_quit = function(){
					rosten.pagequit();
				};
				sendfile_addComment = function(){
					var id = registry.byId("id").get("value");
					var commentDialog = rosten.addCommentDialog({type:"sendFile"});
					commentDialog.callback = function(_data){
						rosten.readSync(rosten.webPath + "/sendFile/addComment/" + id,{dataStr:_data.content,userId:"${user?.id}"},function(data){
							if(data.result=="true" || data.result == true){
								rosten.alert("成功！");
							}else{
								rosten.alert("失败!");
							}	
						});
					};
				};
			
		});
    </script>
</head>
<body>
<div class="rosten_action">
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="sendFile_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'sendFileAction',action:'sendFileForm',id:sendFile?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props=''>
		<form id="sendfile_form" name="sendfile_form" url='[controller:"sendFile",action:"sendFileSave"]' onsubmit="return false;" class="rosten_form" style="padding:0px">
			<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${sendFile?.id }"' />
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="isSend"  data-dojo-props='style:{display:"none"},value:"${sendFile?.isSend }"' />
        		
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"基本信息",toggleable:false,moreText:"",height:"300px",marginBottom:"2px"'>
			<table border="0" width="740" align="left">
				<tr>
				    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>流水号：</div></td>
				    <td width="250">
				    	<input id="serialNo" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='readOnly:true,
		                 		trim:true,placeHolder:"保存后自动生成",
								value:"${sendFile?.serialNo}"
		                '/>
				    </td>
				    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>文件编号：</div></td>
				    <td width="250">
				    	<input id="fileNo" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='readOnly:true,
		                 		trim:true,placeHolder:"领导签发后自动生成",
								value:"${sendFile?.fileNo}"
		                '/>
		           </td>
				</tr>
				<tr>
				    <td><div align="right"><span style="color:red">*&nbsp;</span>发文种类：</div></td>
				    <td>
				    	<div data-dojo-type="dojo/data/ItemFileReadStore" data-dojo-id="rosten.storeData.fileType"
							data-dojo-props='url:"${createLink(controller:'sendFile',action:'getAllSendFileLabel',params:[companyId:company?.id]) }"'></div>
							
						<select id="fileType" data-dojo-type="dijit/form/FilteringSelect" 
							data-dojo-props='name:"fileTypeId",${fieldAcl.isReadOnly("fileType")},
								store:rosten.storeData.fileType,
								trim:true,required:true,
								searchAttr:"subCategory",
								value:"${sendFile?.fileType?.id}"
							'>	
							
						</select>
				    </td>
				    <td><div align="right"><span style="color:red">*&nbsp;</span>成文日期：</div></td>
				    <td>
				    	<input id="fileDate" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='readOnly:true,
		                 		trim:true,placeHolder:"领导签发后自动生成",
								value:"${sendFile?.getFormattedFileDate()}"
		                '/>	
		           </td>
				</tr>
				<tr>
				    <td><div align="right"><span style="color:red">*&nbsp;</span>主办部门：</div></td>
				    <td>
				    	<input id="dealDepart" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"dealDepart",required:true,
		                 		trim:true,
								value:"${sendFile?.dealDepart}"
		                '/>
				    	<button data-dojo-type="dijit/form/Button" 
							data-dojo-props = 'onClick:function(){rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",false,"dealDepart")}'
						>选择</button>
				    <td><div align="right"><span style="color:red">*&nbsp;</span>拟稿人：</div></td>
				    <td>
				    	<input id="drafter" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"drafter",
		                 		trim:true,readOnly:true,
								value:"${sendFile?.drafter?.username}"
		                '/>
		            </td>    
				</tr>
				<tr>
				    <td><div align="right"><span style="color:red">*&nbsp;</span>文件题名：</div></td>
				    <td colspan=3>
				    	<input id="title" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"title",${fieldAcl.isReadOnly("title")},
		                 		trim:true,required:true,
		                 		style:{width:"551px"},
								value:"${sendFile?.title}"
		                '/>
				    </td>    
				</tr>
				<tr>
				    <td><div align="right">主题词：</div></td>
				    <td colspan=3>
				    	<input id="subject" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"subject",${fieldAcl.isReadOnly("subject")},
		                 		trim:true,
		                 		style:{width:"551px"},
								value:"${sendFile?.subject}"
		                '/>
				    </td>    
				</tr>
				<tr>
				    <td><div align="right"><span style="color:red">*&nbsp;</span>主送单位：</div></td>
				    <td colspan=3>
				    	<input id="mainSend" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"mainSend",${fieldAcl.isReadOnly("mainSend")},
		                 		trim:true,required:true,
		                 		style:{width:"480px"},
								value:"${sendFile?.mainSend}"
		                '/>
		                <button data-dojo-type="dijit/form/Button" 
							data-dojo-props = 'onClick:function(){rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",false,"mainSend")}'
						>选择</button>
				    </td>    
				</tr>
				<tr>
				    <td><div align="right">抄送单位：</div></td>
				    <td colspan=3>
				    	<input id="copyTo" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"copyTo",${fieldAcl.isReadOnly("copyTo")},
		                 		trim:true,
		                 		style:{width:"480px"},
								value:"${sendFile?.copyTo}"
		                '/>
		                <button data-dojo-type="dijit/form/Button" 
							data-dojo-props = 'onClick:function(){rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",true,"copyTo")}'
						>选择</button>
				    </td>    
				</tr>
				<tr>
				    <td><div align="right">内部抄送：</div></td>
				    <td colspan=3>
				    	<input id="insideCopy" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"insideCopy",${fieldAcl.isReadOnly("insideCopy")},
		                 		trim:true,
		                 		style:{width:"480px"},
								value:"${sendFile?.insideCopy}"
		                '/>
		                <button data-dojo-type="dijit/form/Button" 
							data-dojo-props = 'onClick:function(){rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",true,"insideCopy")}'
						>选择</button>
				    </td>    
				</tr>
				<tr>
				    <td><div align="right">缓急：</div></td>
				    <td>
				    	<select id="emergency" data-dojo-type="dijit/form/FilteringSelect"
			                data-dojo-props='name:"emergency",${fieldAcl.isReadOnly("emergency")},
			               	trim:true,
			      			value:"${sendFile?.emergency}"
			            '>
							<option value="缺省">缺省</option>
							<option value="平急">平急</option>
							<option value="紧急">紧急</option>
							<option value="特急">特急</option>
			    		</select>
				    
				    <td><div align="right">印发份数：</div></td>
				    <td>
				    	<input id="printCopy" data-dojo-type="dijit/form/NumberTextBox" 
		                 	data-dojo-props='name:"printCopy",
		                 		trim:true,
								value:"${sendFile?.printCopy}"
		                '/>
		            </td>    
				</tr>
			</table>
			</div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"档案信息",toggleable:false,moreText:"",marginBottom:"2px",height:"100px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right">档案种类：</div></td>
					    <td width="250">
					    	<select id="archiveType" data-dojo-type="dijit/form/FilteringSelect"
				                data-dojo-props='name:"archiveType",${fieldAcl.isReadOnly("archiveType")},
				               	trim:true,
				      			value:"${sendFile?.archiveType}"
				            '>
								<option value="文档">文档</option>
								<option value="科档">科档</option>
								<option value="会档">会档</option>
								<option value="音像">音像</option>
				    		</select>
					    </td>
					    <td width="120"><div align="right">归档到：</div></td>
					    <td width="250">
					    	<input id="archiveDbName" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='name:"archiveDbName",readOnly:true,
			                 		trim:true,placeHolder:"归档时自动生成",
									value:"${sendFile?.archiveDbName}"
			                '/>
			           </td>
					</tr>
					<tr>
					    <td><div align="right">份数：</div></td>
					    <td>
					    	<input id="copys" data-dojo-type="dijit/form/NumberTextBox" 
			                 	data-dojo-props='name:"copys",
			                 		trim:true,
									value:"${sendFile?.copys}"
			                '/>
					    
					    <td><div align="right">页数：</div></td>
					    <td>
					    	<input id="pages" data-dojo-type="dijit/form/NumberTextBox" 
			                 	data-dojo-props='name:"pages",
			                 		trim:true,
									value:"${sendFile?.pages}"
			                '/>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right">密级：</div></td>
					    <td>
			                <select id="secretLevel" data-dojo-type="dijit/form/FilteringSelect"
				                data-dojo-props='name:"secretLevel",${fieldAcl.isReadOnly("secretLevel")},
				               	trim:true,
				      			value:"${sendFile?.secretLevel}"
				            '>
								<option value="普通">普通</option>
								<option value="机密">机密</option>
				    		</select>
					    
					    <td><div align="right">期限：</div></td>
					    <td>
			                <select id="term" data-dojo-type="dijit/form/FilteringSelect"
				                data-dojo-props='name:"term",${fieldAcl.isReadOnly("term")},
				               	trim:true,
				      			value:"${sendFile?.term}"
				            '>
								<option value="暂存">暂存</option>
								<option value="短期">短期</option>
								<option value="长期">长期</option>
								<option value="永久">永久</option>
				    		</select>
			            </td>    
					</tr>
				</table>
			</div>
		</form>
	
	
		<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"附件信息",toggleable:false,moreText:"",
			height:"80px",href:"${createLink(controller:'sendFile',action:'getFileUpload',id:sendFile?.id)}"'>
		</div>
	</div>
	<div data-dojo-type="dijit/layout/ContentPane" id="sendfileComment" title="流转意见" data-dojo-props='refreshOnShow:true,
		href:"${createLink(controller:'sendFile',action:'getCommentLog',id:sendFile?.id)}"
	'>	
	</div>
	<div data-dojo-type="dijit/layout/ContentPane" id="sendfileFlowLog" title="流程跟踪" data-dojo-props='refreshOnShow:true,
		href:"${createLink(controller:'sendFile',action:'getFlowLog',id:sendFile?.id)}"
	'>	
	</div>
</div>
</body>