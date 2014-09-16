<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>账单管理</title>
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
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/SimpleTextarea",
		 		"dijit/form/Button",
		 		"dijit/form/DateTextBox",
		 		"dijit/form/FilteringSelect",
		 		"dijit/form/ComboBox",
		 		"dijit/form/Form",
		     	"rosten/widget/ActionBar",
		     	"rosten/app/Application",
		     	"rosten/kernel/behavior"],
			function(parser,kernel,registry,xhr,datestamp){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}"});
					rosten.cssinit();
				});
				
			account_add = function(object){
				var chenkids = ["date","purpose","project","category","money"];
				if(!rosten.checkData(chenkids)) return;
				
				var content = {};
				//var date = registry.byId("date");
				//content.date = datestamp.toISOString(publishDate.attr("value"),{selector: "date"});

				//增加对多次单击的次数----2014-9-4
				var buttonWidget = object.target;
				rosten.toggleAction(buttonWidget,true);
				
				rosten.readSync(rosten.webPath + "/account/accountSave",content,function(data){
					if(data.result=="true" || data.result == true){
						rosten.alert("保存成功！").queryDlgClose= function(){
							page_quit();
						};
					}else{
						rosten.alert("保存失败!");
					}
					rosten.toggleAction(buttonWidget,false);
				},function(error){
					rosten.alert("系统错误，请通知管理员！");
					rosten.toggleAction(buttonWidget,false);
				},"rosten_form");
			};
			
			page_quit = function(){
				rosten.pagequit();
			};
		});
		
		
    </script>
</head>
<body>
	<div class="rosten_action">
		<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'accountAction',action:'accountForm',id:account?.id,params:[userid:user?.id])}"'></div>
	</div>
	<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	  	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props=''>
        	<form class="rosten_form" id="rosten_form" onsubmit="return false;" style="padding:0px;height:300px">
        		<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${account?.id }"' />
        		<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
        	  	<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"基本信息",toggleable:false,moreText:"",height:"320px",marginBottom:"2px"'>
        	  
                <table class="tableData" style="width:740px;margin:0px">
                    <tbody>
                       <tr>
						    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>日期：</div></td>
						    <td width="250">
						    	<input id="date" data-dojo-type="dijit/form/DateTextBox" 
				                	data-dojo-props='name:"date",
				                	trim:true,required:true,missingMessage:"请正确填写日期！",invalidMessage:"请正确填写日期！",
				                	value:"${account?.getFormattedDate()}"
				               '/>
						    </td>
						    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>类型：</div></td>
						    <td width="250">
						    	<select id="purpose" data-dojo-type="dijit/form/FilteringSelect" 
					                data-dojo-props='name:"purpose",
					                trim:true,required:true,missingMessage:"请选择类型！",invalidMessage:"请选择类型！",
					      			value:"${account?.purpose}"
					            '>
								<option value="支出">支出</option>
								<option value="收入">收入</option>
					    	</select>
				           </td>
						</tr>
						<tr>
							<td>
						    	<div align="right"><span style="color:red">*&nbsp;</span>项目名称：</div>
				            </td>
				            <td>
				            	<select id="project" data-dojo-type="dijit/form/ComboBox"
					                data-dojo-props='name:"project",trim:true,required:true,missingMessage:"请选择项目名称！",invalidMessage:"请选择项目名称！",value:"${account?.getProjectName()}"
					            '>
									<g:each in="${projectList}" var="item">
				                    	<option value="${item.id }">${item.name }</option>
				                    </g:each>
					    		</select>
				            </td>
						    <td><div align="right"><span style="color:red">*&nbsp;</span>用途：</div></td>
						    <td>
						    	<select id="category" data-dojo-type="dijit/form/ComboBox"
					                data-dojo-props='name:"category",trim:true,required:true,missingMessage:"请选择用途！",invalidMessage:"请选择用途！",value:"${account?.getCategoryName()}"
					            '>
									<g:each in="${cagetoryList}" var="item">
				                    	<option value="${item.id }">${item.name }</option>
				                    </g:each>
					    		</select>
						    
						   </td>
						</tr>
						<tr>
							<td>
						    	<div align="right"><span style="color:red">*&nbsp;</span>金额：</div>
				            </td>
				            <td>
				            	<input id="money" data-dojo-type="dijit/form/ValidationTextBox" 
				                 	data-dojo-props='name:"money",
				                 		trim:true,required:true,missingMessage:"请正确填写金额！",invalidMessage:"请正确填写金额！",
										value:"${account?.money}"
				                '/> 元
				            </td>
						    <td><div align="right"><span style="color:red">*&nbsp;</span>起草人：</div></td>
						    <td>
						    	<input data-dojo-type="dijit/form/ValidationTextBox" 
				                 	data-dojo-props='readOnly:true,
				                 		trim:true,
										value:"${account?.getUserName()}"
				                '/>
						    
						   </td>
						        
						</tr>
						
						<tr>
						    <td><div align="right">备注：</td>
						    <td colspan=3>
						    	<textarea id="remark" data-dojo-type="dijit/form/SimpleTextarea" 
	    							data-dojo-props='name:"remark",
	                               		style:{width:"550px"},rows:"10",
	                               		trim:true
	                           '>
	    						</textarea>
						    						    
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