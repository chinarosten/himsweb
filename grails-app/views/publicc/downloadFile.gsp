<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>附件上传</title>
    <style type="text/css">
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
		     	"rosten/app/Application",
		     	"rosten/kernel/behavior"],
			function(parser,kernel,registry,xhr,datestamp,DepartUserDialog){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}"});
					rosten.cssinit();
				});
				
			downloadFile_add = function(){
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
							if(window.location.href.indexOf(data.id)==-1){
								window.location.replace(window.location.href + "&id=" + data.id);
							}else{
								window.location.reload();
							}
							
						};
					}else{
						rosten.alert("保存失败!");
					}
				});
			}
			page_quit = function(){
				rosten.pagequit();
			};
		});
		
		
    </script>
</head>
<body>
	<div class="rosten_action">
		<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'publiccAction',action:'downloadFileForm',id:downloadFile?.id,params:[userid:user?.id])}"'></div>
	</div>
	<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	  	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props=''>
        	<form class="rosten_form" id="rosten_form" onsubmit="return false;" style="padding:0px">
        		<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${downloadFile?.id }"' />
        		<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
        	  	<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"基本信息",toggleable:false,moreText:"",height:"460px",marginBottom:"2px"'>
        	  
                <table class="tableData" style="width:740px;margin:0px">
                    <tbody>
                       <tr>
						    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>流水号：</div></td>
						    <td width="250">
						    	<input id="serialNo" data-dojo-type="dijit/form/ValidationTextBox" 
				                 	data-dojo-props='name:"serialNo",readOnly:true,
				                 		trim:true,placeHolder:"领导发布后自动生成",
										value:"${downloadFile?.serialNo}"
				                '/>
						    </td>
						    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>类别：</div></td>
						    <td width="250">
						    	<select id="category" data-dojo-type="dijit/form/FilteringSelect" 
					                data-dojo-props='name:"category",${fieldAcl.isReadOnly("category")},
					                trim:true,
				                 	required:true,
					      			value:"${downloadFile?.category}"
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
					      			value:"${downloadFile?.level}"
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
				                	value:"${downloadFile?.getFormattedPublishDate("date")}"
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
										value:"${downloadFile?.topic}"
				                '/>
						    
						    </td>    
						</tr>
						<tr>
						    <td><div align="right">内容：</td>
						    <td colspan=3>
						    	
						    	<div data-dojo-type="dijit/Editor" style="overflow:hidden;width:620px" id="content"
									extraPlugins="[{name:'dijit/_editor/plugins/FontChoice', command: 'fontName', generic: true},'fontSize']"
									data-dojo-props='name:"content"
				            		<g:if test="${fieldAcl.readOnly.contains('content')}">,disabled:true</g:if>
					            '>
									
								</div>
						    						    
						    </td>    
						</tr>
						
                    </tbody>
                </table>
                </div>
			</form>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"附件信息",toggleable:false,moreText:"",
				height:"60px",href:"${createLink(controller:'publicc',action:'getFileUpload',id:downloadFile?.id)}"'>
			</div>
			
		</div>
	</div>
</body>
</html>