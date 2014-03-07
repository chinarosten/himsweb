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
				});
				meeting_add = function(){
					
				};
				meeting_submit = function(){
					
				};
				page_quit = function(){
					rosten.pagequit();
				};
				addComment = function(){

				};
			
		});
    </script>
</head>
<body>
<div class="rosten_action">
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="meeting_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'meetingAction',action:'meetingForm')}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props=''>
		<form id="meeting_form" name="meeting_form" url='[controller:"meeting",action:"meetingSave"]' class="rosten_form" style="padding:0px">
			<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${meeting?.id }"' />
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
        		
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"基本信息",toggleable:false,moreText:"",height:"700px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>流水号：</div></td>
					    <td width="250">
					    	<input id="serialNo" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='name:"serialNo",readOnly:true,
			                 		trim:true,placeHolder:"保存后自动生成",
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
									value:"${meeting?.drafter?.username}"
			                '/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>拟稿部门：</div></td>
					    <td>
					    	<input id="drafterDepart" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='trim:true,readOnly:true,
									value:"${meeting?.drafterDepart}"
			                '/>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>标题：</div></td>
					    <td colspan=3>
					    	<input id="subject" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='name:"subject",readOnly:true,
			                 		trim:true,style:{width:"551px"},
									value:"${meeting?.subject}"
			                '/>	
			           </td>
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>开始时间：</div></td>
					    <td>
					    	<input id="startDate" data-dojo-type="dijit/form/DateTextBox" 
			                 	data-dojo-props='name:"startDate",
			                 		trim:true,
									value:"${meeting?.getShowDate("start")}"
			                '/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>结束时间：</div></td>
					    <td>
					    	<input id="endDate" data-dojo-type="dijit/form/DateTextBox" 
			                 	data-dojo-props='name:"endDate",
			                 		trim:true,
									value:"${meeting?.getShowDate("end")}"
			                '/>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>会议地点：</div></td>
					    <td>
					    	<input id="address" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='name:"address",
			                 		trim:true,
									value:"${meeting?.address}"
			                '/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>主持人：</div></td>
					    <td>
					    	<input id="presider" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='name:"presider",
			                 		trim:true,
									value:"${meeting?.presider}"
			                '/>
			                <button data-dojo-type="dijit/form/Button" 
								data-dojo-props = 'onClick:function(){}'
							>选择</button>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right">参会人员：</div></td>
					    <td colspan=3>
					    	<textarea id="joiner" data-dojo-type="dijit/form/SimpleTextarea" 
    							data-dojo-props='name:"joiner",
                                		"class":"input",
                                		style:{width:"550px"},
                                		trim:true,
                                		value:"${meeting?.joiner }"
                           '>
    						</textarea>
    						<button data-dojo-type="dijit/form/Button" 
								data-dojo-props = 'onClick:function(){}'
							>选择</button>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right">列席人员：</div></td>
					    <td colspan=3>
					    	<textarea id="guesters" data-dojo-type="dijit/form/SimpleTextarea" 
    							data-dojo-props='name:"guesters",
                                		"class":"input",
                                		style:{width:"550px"},
                                		trim:true,
                                		value:"${meeting?.guesters }"
                           '>
    						</textarea>
    						<button data-dojo-type="dijit/form/Button" 
								data-dojo-props = 'onClick:function(){}'
							>选择</button>
			            </td>    
					</tr>
					<tr>
					    <td><div align="right">会议内容：</div></td>
					    <td colspan=3>
					    	<div data-dojo-type="dijit/Editor" style="overflow:hidden" id="content"
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