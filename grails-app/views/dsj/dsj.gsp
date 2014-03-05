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
					
				};
				dsj_submit = function(){
					
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
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="dsj_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'dsjAction',action:'dsjForm')}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props=''>
		<form id="dsj_form" name="dsj_form" url='[controller:"dsj",action:"dsjSave"]' class="rosten_form" style="padding:0px">
			<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${meeting?.id }"' />
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
        	
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"基本信息",toggleable:false,moreText:"",height:"300px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>流水号：</div></td>
					    <td width="250">
					    	<input id="serialNo" data-dojo-type="dijit/form/ValidationTextBox" 
			                 	data-dojo-props='name:"serialNo",readOnly:true,
			                 		trim:true,placeHolder:"保存后自动生成",
									value:"${dsj?.serialNo}"
			                '/>
					    </td>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>日期：</div></td>
					    <td width="250">
					    	<input id="time" data-dojo-type="dijit/form/DateTextBox" 
			                	data-dojo-props='name:"time",${fieldAcl.isReadOnly("time")},
			                	trim:true,required:true,
			                	value:"${dsj?.getFormattedTimeDate()}"
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
			                 	data-dojo-props='trim:true,readOnly:true,
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
                                		trim:true,
                                		value:"${dsj?.subject }"
                           '>
    						</textarea>
					    </td>
					</tr>
					<tr>
					    <td><div align="right">备注：</div></td>
					    <td  colspan=3>
					    	<textarea id="description" data-dojo-type="dijit/form/SimpleTextarea" 
    							data-dojo-props='name:"description","class":"input",
                               		style:{width:"550px"},
                               		trim:true,
                               		value:"${dsj?.description }"
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