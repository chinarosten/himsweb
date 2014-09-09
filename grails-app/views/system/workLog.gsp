<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>工作日志</title>
    <meta name="layout" content="rosten" />
	<script type="text/javascript">

		require(["dojo/parser",
		 		"dojo/_base/kernel",
		 		"dijit/registry",
		 		"dojo/_base/xhr",
		 		"dijit/layout/TabContainer",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/layout/ContentPane",
		 		"dijit/form/SimpleTextarea",
		 		"dijit/Editor",
				"dijit/_editor/plugins/FontChoice",
		 		"rosten/widget/TitlePane",
		 		"rosten/widget/ActionBar",
		     	"rosten/app/Application",
		     	"rosten/kernel/behavior"
		     	],function(parser,kernel,registry,xhr){
			kernel.addOnLoad(function(){
				rosten.init({webpath:"${request.getContextPath()}"});
				rosten.cssinit();

				<g:if test="${workLog.id && workLog.id!=null && !"".equals(workLog.id)}">
				
				var ioArgs = {
					url : rosten.webPath + "/system/workLogGetContent/${workLog?.id}",
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
			workLog_add = function(){
		    	var date = registry.byId("date");
				if(!date.isValid()){
					rosten.alert("日期不正确！").queryDlgClose = function(){
						date.focus();
					};
					return;
				}
				
				var content = {};
				var contentdom = registry.byId("content");
				if(contentdom.attr("value")!=""){
					content.content = contentdom.attr("value");
				}
				
		    	rosten.readSync(rosten.webPath + "/system/personWorkLogSave",content,function(data){
					if(data.result=="true"){
						rosten.alert("保存成功！").queryDlgClose= function(){
							page_quit();	
						};
					}else{
						rosten.alert("保存失败!");
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
		<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" 
			data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'personWorkLogForm',params:[userid:user?.id])}"'></div>
	</div>
	<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	  	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props='style:{height:"420px"}'>
        	<form class="rosten_form" id="rosten_form" onsubmit="return false;" style="padding:0px">
        		<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${workLog?.id }"' />
        	  	<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"基本信息",toggleable:false,moreText:"",height:"460px",marginBottom:"2px"'>
        	  
                <table class="tableData" style="width:740px;margin:0px">
                    <tbody>
                       <tr>
						    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>日期：</div></td>
						    <td width="250">
						    	<input id="date" data-dojo-type="dijit/form/DateTextBox" 
				                 	data-dojo-props='trim:true,required:true,name:"date",
										value:"${workLog?.getFormattedCreatedDate()}"
				                '/>
						    </td>
						    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>类型：</div></td>
						    <td width="250">
						    	<select id="logType" data-dojo-type="dijit/form/FilteringSelect" 
					                data-dojo-props='name:"logType",${fieldAcl.isReadOnly("logType")},
					                trim:true,required:true,missingMessage:"请选择类别！",invalidMessage:"请选择类型！",
					      			value:"${workLog?.logType}"
					            '>
								<option value="工作日志">工作日志</option>
								<option value="工作周报">工作周报</option>
					    	</select>
				           </td>
						</tr>
						
						<tr>
						    <td><div align="right">工作内容：</td>
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
		</div>
	</div>
</body>
</html>