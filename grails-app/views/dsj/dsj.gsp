<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>大事记</title>
    <style type="text/css">
    	.rosten .dsj_form table tr{
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
				dsj_add = function(){
					var time = registry.byId("time");
					if(!time.isValid()){
						rosten.alert("日期不正确！").queryDlgClose = function(){
							time.focus();
						};
						return;
					}
					var drafterDepart = registry.byId("drafterDepart");
					if(!drafterDepart.isValid()){
						rosten.alert("拟稿部门不正确！").queryDlgClose = function(){
							drafterDepart.focus();
						};
						return;
					}
					var subject = registry.byId("subject");
					if(subject.attr("value")==""){
						rosten.alert("条目不正确！").queryDlgClose = function(){
							subject.focus();
						};
						return;
					}
					
					rosten.readSync(rosten.webPath + "/dsj/dsjSave",{},function(data){
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
						}else{
							rosten.alert("保存失败!");
						}
					},null,"dsj_form");
				};
				dsj_deal = function(type,readArray){
					var content = {};
					content.id = registry.byId("id").attr("value");
					content.deal = type;
					if(readArray){
						content.dealUser = readArray.join(",");
					}
					rosten.readSync(rosten.webPath + "/dsj/dsjFlowDeal",content,function(data){
						if(data.result=="true" || data.result == true){
							rosten.alert("成功！").queryDlgClose= function(){
								if(type=="agrain"){
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
				dsj_submit = function(){
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
		            	dsj_deal("submit",_data);	
		            }  
				};
				dsj_agrain = function(){
					dsj_deal("agrain");
				};
				dsj_notAgrain = function(){
					dsj_deal("notAgrain");
				};
				page_quit = function(){
					rosten.pagequit();
				};
				addComment = function(){
					var id = registry.byId("id").get("value");
					var commentDialog = rosten.addCommentDialog({type:"dsj"});
					commentDialog.callback = function(_data){
						rosten.readSync(rosten.webPath + "/dsj/addComment/" + id,{dataStr:_data.content,userId:"${user?.id}"},function(data){
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
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="dsj_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'dsjAction',action:'dsjForm',id:dsj?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props=''>
		<form id="dsj_form" name="dsj_form" url='[controller:"dsj",action:"dsjSave"]' class="rosten_form" style="padding:0px">
			<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${dsj?.id }"' />
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
        	
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"基本信息",toggleable:false,moreText:"",height:"300px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>流水号：</div></td>
					    <td width="250">
					    	<input id="serialNo" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='readOnly:true,
			                 		trim:true,placeHolder:"保存后自动生成",
									value:"${dsj?.serialNo}"
			                '/>
					    </td>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>日期：</div></td>
					    <td width="250">
					    	<input id="time" data-dojo-type="dijit/form/DateTextBox" 
			                	data-dojo-props='name:"time",${fieldAcl.isReadOnly("time")},
			                	trim:true,required:true,
			                	value:"${dsj?.getShowTimeDate()}"
			               '/>
			           </td>
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>拟稿人：</div></td>
					    <td>
					    	<input id="drafter" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='trim:true,readOnly:true,
									value:"${dsj?.drafter?.username}"
			                '/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>拟稿部门：</div></td>
					    <td>
					    	<input id="drafterDepart" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='name:"drafterDepart",trim:true,required:true,
									value:"${dsj?.drafterDepart}"
			                '/>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>条目：</div></td>
					    <td  colspan=3>
					    	<textarea id="subject" data-dojo-type="dijit/form/SimpleTextarea" 
    							data-dojo-props='name:"subject","class":"input",
                                		style:{width:"550px"},
                                		trim:true,value:"${dsj?.subject }"
                           '>
    						</textarea>
					    </td>
					</tr>
					<tr>
					    <td><div align="right">备注：</div></td>
					    <td  colspan=3>
					    	<textarea id="description" data-dojo-type="dijit/form/SimpleTextarea" 
    							data-dojo-props='name:"description","class":"input",
                               		style:{width:"550px"},rows:"10",
                               		trim:true,value:"${dsj?.description }"
                           '>
    						</textarea>
					    </td>
					</tr>
					
				</table>
			</div>
			
		</form>
	
		<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"附件信息",toggleable:false,moreText:"",
			height:"80px",href:"${createLink(controller:'dsj',action:'getFileUpload',id:dsj?.id)}"'>
		</div>
	</div>
	<div data-dojo-type="dijit/layout/ContentPane" id="dsjComment" title="流转意见" data-dojo-props='refreshOnShow:true,
		href:"${createLink(controller:'dsj',action:'getCommentLog',id:dsj?.id)}"
	'>	
	</div>
	<div data-dojo-type="dijit/layout/ContentPane" id="dsjFlowLog" title="流程跟踪" data-dojo-props='refreshOnShow:true,
		href:"${createLink(controller:'dsj',action:'getFlowLog',id:dsj?.id)}"
	'>	
	</div>
</div>
</body>