<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>发文管理</title>
    <style type="text/css">
    	.rosten .sendfile_form table tr{
    		height:30px;
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
		     	"rosten/app/SendFileManage"],
			function(parser,kernel,registry,xhr,datestamp,DepartUserDialog){
				
			
		});
    </script>
</head>
<body>
<div class="rosten_action">
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="sendFile_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'sendFileAction',action:'sendFileForm')}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props=''>
		<g:form id="sendfile_form" name="sendfile_form" url='[controller:"sendFile",action:"sendFileSave"]' class="rosten_form" >
			<table border="0" width="740" align="left">
				<tr>
				    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>流水号：</div></td>
				    <td width="250">
				    	<input id="serialNo" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"serialNo",readOnly:true,
		                 		trim:true,placeHolder:"保存后自动生成",
								value:"${sendFile?.serialNo}"
		                '/>
				    </td>
				    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>文件编号：</div></td>
				    <td width="250">
				    	<input id="fileNo" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"fileNo",readOnly:true,
		                 		trim:true,placeHolder:"领导签发后自动生成",
								value:"${sendFile?.fileNo}"
		                '/>
		           </td>
				</tr>
				<tr>
				    <td><div align="right"><span style="color:red">*&nbsp;</span>发文种类：</div></td>
				    <td>
				    	<div data-dojo-type="dojo/data/ItemFileReadStore" data-dojo-id="rosten.storeData.cardType"
							data-dojo-props='url:"${createLink(controller:'sendFile',action:'getAllFileType',params:[companyId:companyId]) }"'></div>
							
						<select id="fileType" data-dojo-type="dijit/form/FilteringSelect" 
							data-dojo-props='name:"cardtype",${fieldAcl.isReadOnly("fileType")},
								store:rosten.storeData.cardType,
								trim:true,
								required:true,
								searchAttr:"fileTypeName",
								value:"${sendFile?.fileType?.id}"
							'>	
							
						</select>
				    </td>
				    <td><div align="right">成文日期：</div></td>
				    <td>
				    	<input id="fileDate" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"fileDate",readOnly:true,
		                 		trim:true,
								value:"${sendFile?.getFormattedDate()}"
		                '/>	
		           </td>
				</tr>
				<tr>
				    <td><div align="right">主办部门：</div></td>
				    <td>
				    	<input id="dealDepart" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"dealDepart",
		                 		trim:true,
								value:"${sendFile?.dealDepart}"
		                '/>
				    
				    <td><div align="right">拟稿人：</div></td>
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
		                 		trim:true,
		                 		required:true,
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
		                 		trim:true,
		                 		required:true,
		                 		style:{width:"480px"},
								value:"${sendFile?.mainSend}"
		                '/>
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
				    	<input id="copys" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"copys",
		                 		trim:true,
								value:"${sendFile?.copys}"
		                '/>
		            </td>    
				</tr>
			</table>
		</g:form>
		<table class="tableData" style="width:740px;margin:0px">
	      <g:if test="${!fieldAcl.readOnly.contains('attach') && sendFile?.id!=null}">
			
			<tr>
			    <td width="120"><div align="right">附件：</div></td>
			    <td colspan=3>
			    	<div data-dojo-type="dijit/form/DropDownButton" >
						<span>添加附件</span>
						<div data-dojo-type="dijit/TooltipDialog" id="fileUpload_dialog" data-dojo-props="title: 'fileUpload'" style="width:380px">
								<form data-dojo-type="dijit/form/Form" method="post" 
									action="${createLink(controller:'sendFile',action:'uploadFile',id:bbs?.id)}" id="fileUpload_form" enctype="multipart/form-data">
									
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
	<div data-dojo-type="dijit/layout/ContentPane" id="sendfileComment" title="流转意见" data-dojo-props='refreshOnShow:true
	'>	
	</div>
	<div data-dojo-type="dijit/layout/ContentPane" id="sendfileFlowLog" title="流程跟踪" data-dojo-props='refreshOnShow:true
	'>	
	</div>
</div>
</body>