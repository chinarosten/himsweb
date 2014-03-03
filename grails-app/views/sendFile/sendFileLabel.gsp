<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>发文代字</title>
    <style type="text/css">
    	.rosten .rosten_form table tr{
    		height:30px;
    	}
    </style>
	<script type="text/javascript">
	require(["dojo/parser",
		 		"dojo/_base/kernel",
		 		"dijit/registry",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/SimpleTextarea",
		 		"dijit/form/Button",
		 		"dijit/form/Form",
		 		"dijit/form/FilteringSelect",
		     	"rosten/widget/ActionBar",
		     	"rosten/app/Application",
		     	"rosten/kernel/behavior"],
			function(parser,kernel,registry){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}"});
					rosten.cssinit();
				});
				sendfileLabel_add = function(){
					var category = registry.byId("category");
					if(!category.isValid()){
						rosten.alert("代字类型不正确！").queryDlgClose = function(){
							category.focus();
						};
						return;
					}

					var subCategory = registry.byId("subCategory");
					if(!subCategory.isValid()){
						rosten.alert("代字名称不正确！").queryDlgClose = function(){
							subCategory.focus();
						};
						return;
					}
					
					var nowYear = registry.byId("nowYear");
					if(!nowYear.isValid()){
						rosten.alert("今年年份不正确！").queryDlgClose = function(){
							nowYear.focus();
						};
						return;
					}
					var nowSN = registry.byId("nowSN");
					if(!nowSN.isValid()){
						rosten.alert("今年流水号不正确！").queryDlgClose = function(){
							nowSN.focus();
						};
						return;
					}
					var frontYear = registry.byId("frontYear");
					if(!frontYear.isValid()){
						rosten.alert("去年年份不正确！").queryDlgClose = function(){
							frontYear.focus();
						};
						return;
					}
					var frontSN = registry.byId("frontSN");
					if(!frontSN.isValid()){
						rosten.alert("去年流水号不正确！").queryDlgClose = function(){
							frontSN.focus();
						};
						return;
					}
					var content = {};
					
					rosten.readSync("${createLink(controller:'sendFile',action:'sendFileLabelSave')}",content,function(data){
						if(data.result==true){
							rosten.alert("保存成功！").queryDlgClose= function(){
								page_quit();	
							};
						}else{
							rosten.alert("保存失败！");
						}	
					},null,"rosten_form");
				};
				page_quit = function(){
					rosten.pagequit();
				};
			
		});
    </script>
</head>
<body>
<div class="rosten_action">
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="rosten_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'sendFileAction',action:'sendFileLabelForm')}"'>
	</div>
</div>
<div style="text-Align:center">
	<g:form id="rosten_form" name="rosten_form" onsubmit="return false;" class="rosten_form" style="text-align:left">
		<fieldset class="fieldset-form">
		<legend class="tableHeader">发文代字</legend>
			<input id="id" data-dojo-type="dijit/form/ValidationTextBox"  data-dojo-props='name:"id",style:{display:"none"},value:"${sendFileLabel?.id }"' />
	        <input id="companyId" data-dojo-type="dijit/form/ValidationTextBox"  data-dojo-props='name:"companyId",style:{display:"none"},value:"${companyId }"' />
			<table border="0" style="width:740px;margin:0 auto">
				<tr>
				    <td width="130"><div align="right"><span style="color:red">*&nbsp;</span>代字类型：</div></td>
				    <td width="240">
				    	<select id="category" data-dojo-type="dijit/form/FilteringSelect" 
			                data-dojo-props='name:"category",${fieldAcl.isReadOnly("category")},
			                trim:true,required:true,style:{width:"195px"},
			      			value:"${sendFileLabel?.category}"
			            '>
							<option value="上行文">上行文</option>
							<option value="下行文">下行文</option>
			    		</select>
				    </td>
				    <td width="130">&nbsp;</td>
				    <td width="240">&nbsp;</td>
				</tr>
				<tr>
				    <td><div align="right"><span style="color:red">*&nbsp;</span>代字名称：</div></td>
				    <td  colspan=3>
				    	<input id="subCategory" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"subCategory",${fieldAcl.isReadOnly("subCategory")},
		                 		trim:true,required:true,
		                 		class:"input",
								value:"${sendFileLabel?.subCategory}"
		                '/>
				    </td>
				</tr>
				<tr>
				    <td><div align="right"><span style="color:red">*&nbsp;</span>今年年份：</div></td>
				    <td>
				    	<input id="nowYear" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"nowYear",${fieldAcl.isReadOnly("nowYear")},
		                 		trim:true,required:true,
		                 		class:"input",
								value:"${sendFileLabel?.nowYear}"
		                '/>
				    </td>
				    <td><div align="right"><span style="color:red">*&nbsp;</span>今年流水号：</div></td>
				    <td>
				    	<input id="nowSN" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"nowSN",${fieldAcl.isReadOnly("nowSN")},
		                 		trim:true,required:true,
		                 		class:"input",
								value:"${sendFileLabel?.nowSN}"
		                '/>
		           </td>
				</tr>
				<tr>
				    <td><div align="right">今年保留号或废弃号：</div></td>
				    <td colspan=3>
				    	<textarea id="nowCancel" data-dojo-type="dijit/form/SimpleTextarea" 
						data-dojo-props='name:"nowCancel",${fieldAcl.isReadOnly("nowCancel")},
		            		"class":"input",
		            		style:{width:"564px"},
		            		trim:true,
		            		value:"${sendFileLabel?.nowCancel }"
			            '>
						</textarea>
				    </td>
				</tr>
				<tr>
				    <td><div align="right"><span style="color:red">*&nbsp;</span>去年年份：</div></td>
				    <td>
				    	<input id="frontYear" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"frontYear",${fieldAcl.isReadOnly("frontYear")},
		                 		trim:true,
		                 		required:true,
		                 		class:"input",
								value:"${sendFileLabel?.frontYear}"
		                '/>
				    </td>
				    <td><div align="right"><span style="color:red">*&nbsp;</span>去年流水号：</div></td>
				    <td>
				    	<input id="frontSN" data-dojo-type="dijit/form/ValidationTextBox" 
		                 	data-dojo-props='name:"frontSN",${fieldAcl.isReadOnly("frontSN")},
		                 		trim:true,required:true,
		                 		class:"input",
								value:"${sendFileLabel?.frontSN}"
		                '/>
		           </td>
				</tr>
				<tr>
				    <td><div align="right">去年保留号或废弃号：</div></td>
				    <td colspan=3>
				    	<textarea id="frontCancel" data-dojo-type="dijit/form/SimpleTextarea" 
						data-dojo-props='name:"frontCancel",${fieldAcl.isReadOnly("frontCancel")},
		            		"class":"input",
		            		trim:true,
		            		style:{width:"564px"},
		            		value:"${sendFileLabel?.frontCancel }"
			            '>
						</textarea>
				    </td>
				</tr>
			</table>
		</fieldset>
	</g:form>
</div>
</body>