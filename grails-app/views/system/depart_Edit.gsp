<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>部门管理</title>
    <script type="text/javascript">
    	require([
		 		"dojo/_base/kernel",
		 		"dijit/registry",
		 		"dojo/dom",
		 		"dojo/_base/xhr",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/TextBox",
		 		"dijit/form/SimpleTextarea",
		 		"dojox/layout/ContentPane",
		     	"rosten/widget/ActionBar"
		    ],
			function(kernel,registry,dom,xhr){
				
    		depart_save = function(){
    			var departName = registry.byId("departName");
    			if(!departName.isValid()){
    				rosten.alert("部门名称不正确！").queryDlgClose = function(){
    					departName.focus();
    				};
    				return;
    			}
    			var pane = registry.byId("departEditPane");
    			var form = dom.byId("rosten_form");

    			var xhrArgs = {
    	    		form:form,
    	        	handleAs: "text",
    	        	/*
    	        	handle: function(data, ioargs){
    		        	console.debug(ioargs.xhr.status);
    	        		switch(ioargs.xhr.status){
    	        		case 200:
    	        			pane.set("content",data);
    	        			break;
    	        		case 302:
    		        		alert("luhangyu");
    	        			break;
    	        		default:
    		        		console.debug("Unknown error");	
    	        		}
    	        	}*/

    	        	load: function(data) {
    	        		pane.set("content",data);
    	        	},
    	        	error: function(error) {
    	            	pane.set("content",error);
    	        	}
    	     	}
    			var deferred = xhr.post(xhrArgs);
    			
    		}
    		kernel.addOnLoad(function(){
    			<g:if test="${flash.refreshTree}">
    				refreshDepartTree();
    			</g:if>
    		})
    	});
    </script>
</head>
<body>
	<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'departForm')}"'></div>
	<g:if test="${flash.message}">
		<div class="message">${flash.message}</div>
	</g:if>
	<g:form name="rosten_form" url='[controller:"system",action:"departSave"]' class="rosten_form" style="width:none;height:none">
		<g:hiddenField name="id" value="${depart?.id}" />
		<g:hiddenField name="parentId" value="${parentId}"/>
		<g:hiddenField name="companyId" value="${companyId}"/>
        <fieldset class="fieldset-form">
        	<legend class="tableHeader">部门配置</legend>
            <table class="tableData">
            	<tbody>
	                <tr>
	                    <td width="100">
	                        <div align="right" >
	                            <span style="color:red">*&nbsp;</span>部门名称：
	                        </div>
	                    </td>
	                    <td  width="180">
	                    	<input id="departName" data-dojo-type="dijit/form/ValidationTextBox" 
	                    		data-dojo-props='name:"departName",
	                    			"class":"input",
	                    			trim:true,
	                    			required:true,
	                    			promptMessage:"请正确输入部门名称...",
	                    			value:"${depart?.departName }"
	                    	'/>
	                    </td>
	           		 </tr>
					<tr>
                        <td>
                            <div align="right">部门电话：</div>
                        </td>
                        <td>
                        	<input id="departPhone" data-dojo-type="dijit/form/ValidationTextBox" 
	                    		data-dojo-props='name:"departPhone",
	                    			"class":"input",
	                    			trim:true,
	                    			regExp:"\d{11}",
	                    			promptMessage:"移动电话必须11位数字",
	                    			invalidMessage:"移动电话必须11位数字",
	                    			value:"${depart?.departPhone }"
	                    	'/>
                        </td>
                    </tr>
                    <tr>
                       <td>
                            <div align="right" >传真号码：</div>
                        </td>
                        <td>
                        	<input id="departFax" data-dojo-type="dijit/form/ValidationTextBox" 
	                    		data-dojo-props='name:"departFax",
	                    			"class":"input",
	                    			trim:true,
	                    			promptMessage:"移动电话必须11位数字",
	                    			value:"${depart?.departFax }"
	                    	'/>
                        </td>
                    </tr>
					<tr>
                      <td>
                           <div align="right" >部门手机：</div>
                       </td>
                       <td colspan="3">
                       		<input id="departMobile" data-dojo-type="dijit/form/ValidationTextBox" 
	                    		data-dojo-props='name:"departMobile",
	                    			"class":"input",
	                    			trim:true,
	                    			value:"${depart?.departMobile }"
	                    	'/>
	                    	
                       </td>
                   </tr>
                   <tr>
                      <td>
                           <div align="right" >地址：</div>
                       </td>
                       <td colspan="3">
                       		<input id="departAdderess" data-dojo-type="dijit/form/ValidationTextBox" 
	                    		data-dojo-props='name:"departAdderess",
	                    			"class":"input",
	                    			trim:true,
	                    			value:"${depart?.departAdderess }"
	                    	'/>
	                    	
                       </td>
                   </tr>
                   <tr>
                      <td>
                          <div align="right" >内容描述：</div>
                      </td>

						<td colspan="3">
							<textarea id="description" data-dojo-type="dijit/form/SimpleTextarea"  
								data-dojo-props='name:"description",
								 	"class":"input",
									trim:true,
									style:{width:"400px"},
									value:"${depart?.description }"
							
							'></textarea>
  						</td>
                   </tr>
	            </tbody>
	        </table>
	    </fieldset>
	</g:form>
</body>
</html>