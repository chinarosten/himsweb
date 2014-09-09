<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>Logo设置</title>
	<script type="text/javascript">

		require([
				"dijit/registry",
				"dijit/form/SimpleTextarea",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/FilteringSelect",
		 		"rosten/widget/ActionBar"
		     	],function(registry){
			saveLogoSet = function(){

				var companyName = registry.byId("companyName");
				if(!companyName.isValid()){
					rosten.alert("单位名称不正确！").queryDlgClose = function(){
						companyName.focus();
					};
					return;
				}

				var shortName = registry.byId("shortName");
				if(!shortName.isValid()){
					rosten.alert("简称不正确！").queryDlgClose = function(){
						shortName.focus();
					};
					return;
				}

				var companyAddress = registry.byId("companyAddress");
				if(!companyAddress.isValid()){
					rosten.alert("地址不正确！").queryDlgClose = function(){
						companyAddress.focus();
					};
					return;
				}

				var logoname = registry.byId("logoName");
				if(!logoname.isValid()){
					rosten.alert("Logo配置名称不正确！").queryDlgClose = function(){
						logoname.focus();
					};
					
					return;
				}
				var imgfilename = registry.byId("imgFileName");
				if(!imgfilename.isValid()){
					rosten.alert("图像资源附件名称不正确！").queryDlgClose = function(){
						imgfilename.focus();
					};
					
					return;
				}
				var cssStyle = registry.byId("cssStyle");
				if(!cssStyle.isValid()){
					rosten.alert("css样式不正确！").queryDlgClose = function(){
						cssStyle.focus();
					};
					
					return;
				}
				var content = {};
				content.companyName = companyName.attr("value");
				content.shortName = shortName.attr("value");
				content.companyPhone = registry.byId("companyPhone").attr("value");
				content.companyFax = registry.byId("companyFax").attr("value");
				content.companyAddress = companyAddress.attr("value");
				
				content.logoName = logoname.attr("value");
				content.imgFileName = imgfilename.attr("value");
				content.modelId = registry.byId("modelId").attr("value");
				content.imgfilepath = content.imgFileName;
				content.cssStyle = cssStyle.attr("value");
				
				content.id = registry.byId("id").attr("value");
				content.companyId = registry.byId("companyId").attr("value");
			
				rosten.readSync("${createLink(controller:'system',action:'logoSetSave')}",content,function(data){
					if(data.result==true){
						show_systemNaviEntity("logSet");
						rosten.alert("成功，请使用<重新登录系统>查看变化！");
					}else{
						rosten.alert("保存失败！");
					}	
				});
			}

		});	

		
    </script>
</head>
<body>
	<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'logoSet')}"'></div>
		<div style="text-Align:center">
        <div class="rosten_form" style="margin:10px">
        	<input id="id" data-dojo-type="dijit/form/ValidationTextBox"  data-dojo-props='name:"id",style:{display:"none"},value:"${logoSet?.id }"' />
        	<input id="companyId" data-dojo-type="dijit/form/ValidationTextBox"  data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
        	<fieldset class="fieldset-form">
        		<legend class="tableHeader">单位信息</legend>
        		<table class="tableData" style="text-align:left;margin:10px 10px 10px 20px">
        			<tbody>
						  <tr>
                            <td width="120">
                                <div align="right">
                                    <span style="color:red">*&nbsp;</span>单位名称：
                                </div>
                            </td>
                            <td  width="450">
                                <input id="companyName" data-dojo-type="dijit/form/ValidationTextBox" 
                                	data-dojo-props='name:"companyName",
                                		"class":"input",
                                		style:{width:"400px",marginLeft:"1px"},
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入名称...",
              							value:"${company?.companyName}"
                               '/>
                            </td>
                    	</tr> 
                    	<tr>
                        	<td>
                                <div align="right" >
                                    <span style="color:red">*&nbsp;</span>简称：
                                </div>
                            </td>
                             <td>
                             	<input id="shortName" data-dojo-type="dijit/form/ValidationTextBox"
                             		data-dojo-props='name:"shortName",
                                		"class":"input",
                                		style:{width:"60px",marginLeft:"1px"},
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入简称...",
              							value:"${company?.shortName}"
                                '/>
    						</td>
                        </tr> 
                        <tr>
                        	<td>
                                <div align="right" >电话号码：</div>
                            </td>
                             <td>
                             	<input id="companyPhone" data-dojo-type="dijit/form/ValidationTextBox"
                             		data-dojo-props='name:"companyPhone",
                                		"class":"input",
                                		style:{width:"180px",marginLeft:"1px"},
                                		trim:true,
                                		promptMessage:"请正确输入电话号码...",
              							value:"${company?.companyPhone}"
                                '/>
    						</td>
                        </tr>
                        <tr>
                        	<td>
                                <div align="right" >传真号码：
                                </div>
                            </td>
                             <td>
                             	<input id="companyFax" data-dojo-type="dijit/form/ValidationTextBox"
                             		data-dojo-props='name:"companyFax",
                                		"class":"input",
                                		style:{width:"180px",marginLeft:"1px"},
                                		trim:true,
                                		promptMessage:"请正确输入传真号码...",
              							value:"${company?.companyFax}"
                                '/>
    						</td>
                        </tr>
                        <tr>
                        	<td>
                                <div align="right" >
                                    <span style="color:red">*&nbsp;</span>地址：
                                </div>
                            </td>
                             <td>
                             	<input id="companyAddress" data-dojo-type="dijit.form.ValidationTextBox"
                             		data-dojo-props='name:"companyAddress",
                                		"class":"input",
                                		style:{width:"400px",marginLeft:"1px"},
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入地址...",
              							value:"${company?.companyAddress}"
                                '/>
    						</td>
                        </tr>    			
        			</tbody>
        		</table>
        	</fieldset>
            <fieldset class="fieldset-form">
                <legend class="tableHeader">Logo配置</legend>
                <table class="tableData" style="text-align:left;margin:10px 10px 10px 20px">
                    <tbody>
                        <tr>
                            <td width="120">
                                <div align="right">
                                    <span style="color:red">*&nbsp;</span>Logo配置名称：
                                </div>
                            </td>
                            <td  width="450">
                                <input id="logoName" data-dojo-type="dijit/form/ValidationTextBox" 
                                	data-dojo-props='name:"logoName",
                                		"class":"input",
                                		trim:true,
                                		required:true,
                                		promptMessage:"请输入logo配置名称...",
                                		value:"${logoSet?.logoName }"
                               '/>
                            </td>
                    	</tr>
                        <tr>
                            <td>
                                <div align="right" >
                                    <span style="color:red">*&nbsp;</span>图像资源附件名称：
                                </div>
                            </td>
                            <td>
                                <input id="imgFileName" data-dojo-type="dijit/form/ValidationTextBox" 
                                	data-dojo-props='name:"imgFileName",
                                		"class":"input",
                                		trim:true,
                                		required:true,
                                		promptMessage:"请输入logo图像资源附件名称...",
                                		disabled:true,
                                		value:"logo.png;header_logo.png"'
                                />
                                <button data-dojo-type="dijit/form/Button" style="display:none">上传图片</button>
                            </td>
                        </tr>
                         <tr>
                            <td>&nbsp;</td>
                            <td>
                                <div style="color:red;margin-Top:4px;margin-bottom:2px">注：附件的文件名必须按照图像资源附件名称显示的内容来命名！</div>
                            </td>
                        </tr>
						<tr>
                            <td>
                                <div align="right" > 图像资源附件：</div>
                            </td>
                            <td>
                                <div id="imgFilePath" name="imgFilePath" style="height:20px;margin-Top:2px">logo.png;header_logo.png</div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div align="right" > 图像对应默认大小：</div>
                            </td>
                            <td>
                                <div style="height:20px;margin-Top:2px">logo.png【167×60】;header_logo.png【533×87】</div> 
                            </td>
                           
                        </tr>
						<tr style="display:none">
                            <td>
                                <div align="right">
                                    <span style="color:red">*&nbsp;</span>默认页面打开模块：
                                </div>
                            </td>
                            <td>
                            	<select id="modelId" data-dojo-type="dijit/form/FilteringSelect"
                                	data-dojo-props = 'name:"modelId",style:{width:"197px",fontFamily:"Courier"},trim:true,required:true,disabled:true'
                                >
                                	<g:each var="model" in="${modelList}">
                                		<option value="${model.id }">${model.modelName }</option>
                                	</g:each>
					   			</select>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div align="right">
                                    <span style="color:red">*&nbsp;</span>css样式：
                                </div>
                            </td>
                            <td>
                            	<select id="cssStyle" data-dojo-type="dijit/form/FilteringSelect" 
                            		data-dojo-props='name:"cssStyle",
                                		style:{width:"197px",fontFamily:"Courier"},
                                		trim:true,
                                		required:true,
                                		disabled:true,
                                		value:"${logoSet.cssStyle!=null?logoSet.cssStyle:"claro" }"
                            	'>
                            		<option value="claro">claro</option>
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