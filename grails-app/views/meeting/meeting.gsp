<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>会议通知</title>
    <style type="text/css">
    	.rosten .meeting_form table tr{
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
		 		"dijit/Editor",
				"dijit/_editor/plugins/FontChoice",
		     	"rosten/widget/ActionBar",
		     	"rosten/widget/TitlePane",
		     	"rosten/app/Application",
		     	"rosten/kernel/behavior"],
			function(parser,kernel,registry,xhr,datestamp,DepartUserDialog){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}"});
					rosten.cssinit();
					<g:if test="${meeting.id && meeting.id!=null && !"".equals(meeting.id)}">
					
						var ioArgs = {
							url : rosten.webPath + "/meeting/meetingGetContent/${meeting?.id}",
							sync : true,
							handleAs : "json",
							preventCache : true,
							encoding : "utf-8",
							load : function(data) {
								registry.byId("content").set("value",data.content);
								registry.byId("description").set("value",data.description);
							}
						};
						xhr.get(ioArgs);
					</g:if>
				});
				meeting_add = function(object){

					var chenkids = ["drafterDepart","subject","startDate","endDate","address","presider"];
					if(!rosten.checkData(chenkids)) return;
					
					var joiner = registry.byId("joiner");
					if(joiner.get("value")==""){
						rosten.alert("参与人员不正确！").queryDlgClose = function(){
							joiner.focus();
						};
						return;
					}

					//增加对多次单击的次数----2014-9-4
					var buttonWidget = object.target;
					rosten.toggleAction(buttonWidget,true);
					
					rosten.readSync(rosten.webPath + "/meeting/meetingSave",{content:registry.byId("content").get("value")},function(data){
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
						rosten.toggleAction(buttonWidget,false);
					},function(error){
						rosten.alert("系统错误，请通知管理员！");
						rosten.toggleAction(buttonWidget,false);
					},"meeting_form");
				};
				meeting_submit_select = function(url,buttonWidget){
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
			            	meeting_deal("submit",_data,buttonWidget);	
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
				meeting_deal = function(type,readArray,buttonWidget){
					var content = {};
					content.id = registry.byId("id").attr("value");
					content.deal = type;
					if(readArray){
						content.dealUser = readArray.join(",");
					}
					rosten.readSync(rosten.webPath + "/meeting/meetingFlowDeal",content,function(data){
						if(data.result=="true" || data.result == true){
							rosten.alert("成功！").queryDlgClose= function(){
								//刷新待办事项内容
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
				meeting_submit = function(object){
					//从后台获取下一处理人
					
					//增加对多次单击的次数----2014-9-4
					var buttonWidget = object.target;
					rosten.toggleAction(buttonWidget,true);
					
					var content = {};
					rosten.readSync("${createLink(controller:'meeting',action:'getSelectFlowUser',params:[companyId:company?.id,id:meeting?.id])}",content,function(data){
						if(data.dealFlow==false){
							//流程无下一节点
							meeting_deal("submit",null,buttonWidget);
							return;
						}
						var url = "${createLink(controller:'system',action:'userTreeDataStore',params:[companyId:company?.id])}";
						if(data.dealType=="user"){
							//人员处理
							if(data.showDialog==false){
								//单一处理人
								var _data = [];
								_data.push(data.userId + ":" + data.userDepart);
								meeting_deal("submit",_data,buttonWidget);
							}else{
								//多人，多部门处理
								url += "&type=user&user=" + data.user;
								meeting_submit_select(url,buttonWidget);
							}
						}else{
							//群组处理
							url += "&type=group&groupIds=" + data.groupIds;
							if(data.limitDepart){
								url += "&limitDepart="+data.limitDepart;
							}
							meeting_submit_select(encodeURI(url),buttonWidget);
						}

					},function(error){
						rosten.alert("系统错误，请通知管理员！");
						rosten.toggleAction(buttonWidget,false);
					});
				};
				page_quit = function(){
					rosten.pagequit();
				};
				meeting_addComment = function(){
					var id = registry.byId("id").get("value");
					var commentDialog = rosten.addCommentDialog({type:"meeting"});
					commentDialog.callback = function(_data){
						rosten.readSync(rosten.webPath + "/meeting/addComment/" + id,{dataStr:_data.content,userId:"${user?.id}"},function(data){
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
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="meeting_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'meetingAction',action:'meetingForm',id:meeting?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props=''>
		<form id="meeting_form" name="meeting_form" url='[controller:"meeting",action:"meetingSave"]' onsubmit="return false;" class="rosten_form" style="padding:0px">
			<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${meeting?.id }"' />
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
        		
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"基本信息",toggleable:false,moreText:"",height:"700px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>流水号：</div></td>
					    <td width="250">
					    	<input id="serialNo" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='readOnly:true,trim:true,placeHolder:"保存后自动生成",
									value:"${meeting?.serialNo}"
			                '/>
					    </td>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>会议类别：</div></td>
					    <td width="250">
					    	<select id="category" data-dojo-type="dijit/form/FilteringSelect"
				                data-dojo-props='name:"category",${fieldAcl.isReadOnly("category")},
				               	trim:true,
				      			value:"${meeting?.category}"
				            '>
								<option value="部门会议">部门 会议</option>
								<option value="全体大会">全体大会</option>
								<option value="网络会议">网络会议</option>
								<option value="研讨会">研讨会</option>
				    		</select>
			           </td>
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>拟稿人：</div></td>
					    <td>
					    	<input id="drafter" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='trim:true,readOnly:true,
									value:"${meeting.drafter?.getFormattedName()}"
			                '/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>拟稿部门：</div></td>
					    <td>
					    	<input id="drafterDepart" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='name:"drafterDepart",trim:true,required:true,readOnly:true,
									value:"${meeting?.drafterDepart}"
			                '/>
			                <button data-dojo-type="dijit/form/Button" 
								data-dojo-props = 'onClick:function(){rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",false,"drafterDepart")}'
							>选择</button>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>标题：</div></td>
					    <td colspan=3>
					    	<input id="subject" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='name:"subject",required:true,
			                 		trim:true,style:{width:"555px"},
									value:"${meeting?.subject}"
			                '/>	
			           </td>
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>开始时间：</div></td>
					    <td>
					    	<input id="startDate" data-dojo-type="dijit/form/DateTextBox" 
			                 	data-dojo-props='name:"startDate",
			                 		trim:true,required:true,
									value:"${meeting?.getShowDate("start")}"
			                '/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>结束时间：</div></td>
					    <td>
					    	<input id="endDate" data-dojo-type="dijit/form/DateTextBox" 
			                 	data-dojo-props='name:"endDate",
			                 		trim:true,required:true,
									value:"${meeting?.getShowDate("end")}"
			                '/>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>会议地点：</div></td>
					    <td>
					    	<input id="address" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='name:"address",
			                 		trim:true,required:true,
									value:"${meeting?.address}"
			                '/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>主持人：</div></td>
					    <td>
					    	<input id="presider" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='readOnly:true,trim:true,required:true,
									value:"${meeting.presider?.getFormattedName()}"
			                '/>
			                <input  data-dojo-type="dijit/form/ValidationTextBox" id="presiderId" data-dojo-props='name:"presiderId",style:{display:"none"},value:"${meeting?.presider?.id }"' />
			                <button data-dojo-type="dijit/form/Button" 
								data-dojo-props = 'onClick:function(){rosten.selectUser("${createLink(controller:'system',action:'userTreeDataStore',params:[companyId:company?.id])}","single","presider","presiderId")}'
							>选择</button>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>参会人员：</div></td>
					    <td colspan=3>
					    	<input  data-dojo-type="dijit/form/ValidationTextBox" id="joinerIds" data-dojo-props='name:"joinerIds",style:{display:"none"},value:"${joinerIds.join(",") }"' />
					    	<textarea id="joiner" data-dojo-type="dijit/form/SimpleTextarea" 
    							data-dojo-props='readOnly:true,"class":"input",
                                		style:{width:"550px"},
                                		trim:true,
                                		value:"${joiners.join(",")}"
                           '>
    						</textarea>
    						<button data-dojo-type="dijit/form/Button" 
								data-dojo-props = 'onClick:function(){rosten.selectUser("${createLink(controller:'system',action:'userTreeDataStore',params:[companyId:company?.id])}","multile","joiner","joinerIds")}'
							>选择</button>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right">列席人员：</div></td>
					    <td colspan=3>
					    	<input  data-dojo-type="dijit/form/ValidationTextBox" id="guesterIds" data-dojo-props='name:"guesterIds",style:{display:"none"},value:"${guesterIds.join(",") }"' />
					    	<textarea id="guesters" data-dojo-type="dijit/form/SimpleTextarea" 
    							data-dojo-props='readOnly:true,"class":"input",
                                		style:{width:"550px"},
                                		trim:true,
                                		value:"${guesters.join(",")}"
                           '>
    						</textarea>
    						<button data-dojo-type="dijit/form/Button" 
								data-dojo-props = 'onClick:function(){rosten.selectUser("${createLink(controller:'system',action:'userTreeDataStore',params:[companyId:company?.id])}","multile","guesters","guesterIds")}'
							>选择</button>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right">会议内容：</div></td>
					    <td colspan=3>
					    	<div data-dojo-type="dijit/Editor" style="overflow:hidden;width:620px" id="content"
								extraPlugins="[{name:'dijit/_editor/plugins/FontChoice', command: 'fontName', generic: true},'fontSize']"
								data-dojo-props='name:"content"
			            		<g:if test="${fieldAcl.readOnly.contains('content')}">,disabled:true</g:if>
				            '></div>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right">备注：</div></td>
					    <td colspan=3>
					    	<textarea id="description" data-dojo-type="dijit/form/SimpleTextarea" 
    							data-dojo-props='name:"description",
                              		"class":"input",
                              		style:{width:"620px"},
                              		trim:true,
                              		value:"${meeting?.description }"
                           '>
    						</textarea>
			            </td>    
					</tr>
				</table>
			</div>
			
		</form>
	
		<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"附件信息",toggleable:false,moreText:"",
			height:"80px",href:"${createLink(controller:'meeting',action:'getFileUpload',id:meeting?.id)}"'>
		</div>
	</div>
	<div data-dojo-type="dijit/layout/ContentPane" id="meetingComment" title="流转意见" data-dojo-props='refreshOnShow:true,
		href:"${createLink(controller:'meeting',action:'getCommentLog',id:meeting?.id)}"
	'>	
	</div>
	<div data-dojo-type="dijit/layout/ContentPane" id="meetingFlowLog" title="流程跟踪" data-dojo-props='refreshOnShow:true,
		href:"${createLink(controller:'meeting',action:'getFlowLog',id:meeting?.id)}"
	'>	
	</div>
</div>
</body>