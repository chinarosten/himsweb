<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <title>系统工具</title>
	<script type="text/javascript">
		require([
				"dojo/dom",
				"dijit/registry",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/FilteringSelect",
		 		"rosten/widget/ActionBar"
		     	],function(dom,registry){
	     	
			systemtool_addResource = function(){
				var dealCompanyId = registry.byId("dealCompany").attr("value");
				var args = {onLoadFunction:function(){
					//dojo.byId("resourceName").focus();
				}};
		    	rosten.kernel.createRostenShowDialog("${createLink(controller:'system',action:'systemTool_Resource')}" + "?dealCompanyId=" + dealCompanyId,args);
		    }
			systemtool_addResource_submit = function(){
				var resourcename = registry.byId("resourceName");
				if(!resourcename.isValid()){
					rosten.alert("资源名称不正确！").queryDlgClose = function(){
						resourcename.focus();
					};
					return;
				}
				
				var imgsrc = registry.byId("imgUrl");
				if(!imgsrc.isValid()){
					rosten.alert("图标地址不正确！").queryDlgClose = function(){
						imgsrc.focus();
					};
					return;
				}
				var href = registry.byId("url");
				if(!href.isValid()){
					rosten.alert("导航地址不正确！").queryDlgClose = function(){
						href.focus();
					};
					return;
				}
						
				var content = {};
				content.modelId = registry.byId("modelId").attr("value");
				content.resourceName = resourcename.attr("value");
				content.imgUrl = imgsrc.attr("value");
				content.url = href.attr("value");

				var description = registry.byId("description");
				if(description.attr("value")!=""){
					content.description = description.attr("value");
				}
		    	rosten.readSync("${createLink(controller:'system',action:'systemTool_AddResource')}",content,function(data){
		    		if(data.result==true){
		    			rosten.kernel.hideRostenShowDialog();
						rosten.alert("成功保存,请使用<重新登录系统>查看变化!");
					}else{
						rosten.alert("保存失败!");
					}
		    	});
		    }	    
			systemtool_deleteData = function(){
		    	var _1 = rosten.confirm("删除机构数据将会删除所有数据无法还原，是否继续?");
		    	_1.callback = function(){
		    		rosten.readSync("${createLink(controller:'system',action:'systemTool_DeleteData')}",{"dealCompany":registry.byId("dealCompany").attr("value")},function(data){
			    		if(data.result==true){
			    			rosten.alert("成功!");
			    			show_systemNaviEntity("systemToolManage");
			    		}else if(data.result==false){
			    			rosten.alert("失败！");
			    		}else if(data.result=="error"){
			    			rosten.alert("错误！");
			    		}
			    	});
		    	}
		    }
		    systemtool_init = function(){
		    	var _1 = rosten.confirm("初始化功能将会删除原有所有数据无法还原，是否继续?");
		    	_1.callback = function(){
		    		rosten.readSync("${createLink(controller:'system',action:'systemTool_Init')}",{"dealCompany":registry.byId("dealCompany").attr("value")},function(data){
			    		if(data.result==true){
			    			rosten.alert("成功!");
			    		}else if(data.result==false){
			    			rosten.alert("失败！");
			    		}else if(data.result=="error"){
			    			rosten.alert("错误！");
			    		}
			    	});
		    	}
		    	
		    };
		    systemtool_copyInit = function(){
		    	var dealCompany = registry.byId("dealCompany");
		    	var fromCompany = registry.byId("fromCompany");
		    	if(dealCompany.attr("value")==fromCompany.attr("value")){
		    		rosten.alert("处理机构与源机构不允许相同！");
		    		return;
		    	}
		    	var _1 = rosten.confirm("初始化功能将会删除原有所有数据无法还原，是否继续?");
		    	_1.callback = function(){
		    		rosten.readSync("${createLink(controller:'system',action:'systemTool_Init')}",{
			    			"dealCompany":dealCompany.attr("value"),
			    			"fromCompany":fromCompany.attr("value")
			    		},function(data){
			    		if(data.result==true){
			    			rosten.alert("成功!");
			    		}else if(data.result==false){
			    			rosten.alert("失败！");
			    		}else if(data.result=="error"){
			    			rosten.alert("错误！");
			    		}
			    	});
		    	}
		    	
		    };

	     	

		});
    </script>
</head>
<body>
	<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'systemTool')}"'></div>
	<div style="text-Align:center">
        <div class="rosten_form">
            <fieldset class="fieldset-form">
                <legend class="tableHeader">系统工具</legend>
                <table class="tableData" style="text-align:left">
                    <tbody>
                        <tr>
                            <td width="120">
                                <div align="right"><span style="color:red">*&nbsp;</span>处理机构名称：</div>
                            </td>
                            <td  width="450">
                                <select id="dealCompany" data-dojo-type="dijit/form/FilteringSelect"
                                	data-dojo-props = 'name:"dealCompany",style:{width:"182px"},trim:true,required:true'
                                >
                                	<g:each var="company" in="${companyList}">
                                		<option value="${company.id }">${company.companyName }</option>
                                	</g:each>
					   			</select>
                            </td>
                    	</tr>
                        
						<tr>
                            <td>
                                <div align="right" >源机构名称：</div>
                            </td>
                            <td>
                                 <select id="fromCompany" data-dojo-type="dijit/form/FilteringSelect"
                                 	data-dojo-props = 'name:"fromCompany",style:{width:"182px"},trim:true,required:true'
                                 >
                                 	<g:each var="company" in="${companyList}">
                                		<option value="${company.id }">${company.companyName }</option>
                                	</g:each>
                                 </select>
                            </td>
                           
                        </tr>
                        
                    </tbody>
                </table>
            </fieldset>
		</div>
	</div>
</body>
</html>