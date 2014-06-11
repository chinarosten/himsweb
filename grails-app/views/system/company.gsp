<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>组织机构</title>
	<script type="text/javascript">
		require(["dojo/parser",
		 		"dojo/_base/kernel",
		 		"dijit/registry",
		     	"rosten/widget/ActionBar",
		     	"dijit/form/RadioButton",
		     	"dijit/form/ValidationTextBox",
		     	"dijit/form/SimpleTextarea",
		     	"dijit/form/Button",
		     	"dijit/form/RadioButton",
		     	"rosten/app/SystemApplication"],
			function(parser,kernel,registry,ActionBar){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}"});
					rosten.cssinit();
				});
			company_add = function(){
				var companyname = registry.byId("companyName");
				if(!companyname.isValid()){
					rosten.alert("名称不正确！").queryDlgClose = function(){
						companyname.focus();
					};
					return;
				}
				var shortname = registry.byId("shortName");
				if(!shortname.isValid()){
					rosten.alert("简称不正确！").queryDlgClose = function(){
						shortname.focus();
					};
					return;
				}
				var companymobile = registry.byId("companyMobile");
				if(!companymobile.isValid()){
					rosten.alert("手机号码不正确！").queryDlgClose = function(){
						companymobile.focus();
					};
					return;
				}
				var companyaddress = registry.byId("companyAddress");
				if(!companyaddress.isValid()){
					rosten.alert("地址不正确！").queryDlgClose = function(){
						companyaddress.focus();
					};
					return;
				}
				var content = {};
				
				rosten.readSync(rosten.webPath + "/system/companySave",content,function(data){
					if(data.result=="true"){
						rosten.alert("保存成功！").queryDlgClose= function(){
							page_quit();	
						};
					}else{
						rosten.alert("保存失败!");
					}
				},null,"rosten_form");
			}
		});
    </script>
</head>
<body>
	<div class="rosten_action">
		<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'companyForm',params:[userid:user?.id])}"'></div>
	</div>
		<div style="text-Align:center">
        <form class="rosten_form" id="rosten_form" onsubmit="return false;" style="text-align:left;">
			
        	<input style="display:none" dojoType="dijit/form/ValidationTextBox" id="id" name="id" value="${company?.id }"></input>
            <fieldset class="fieldset-form">
                <legend class="tableHeader">组织机构配置</legend>
                <table class="tableData" style="width:550px">
                    <tbody>
                       <tr>
                            <td>
                                <div align="right" >
                                  <span style="color:red">*&nbsp;</span>组织机构名称：
                                </div>
                            </td>
                            <td>
                                <input id="companyName" data-dojo-type="dijit/form/ValidationTextBox" 
                                	data-dojo-props='name:"companyName",${fieldAcl.isReadOnly("companyName")},
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
                                    <span style="color:red">*&nbsp;</span>组织机构简称：
                                </div>
                            </td>
                             <td colspan="3">
                             	<input id="shortName" data-dojo-type="dijit/form/ValidationTextBox"
                             		data-dojo-props='name:"shortName",${fieldAcl.isReadOnly("shortName")},
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
                                <div align="right" >
                                    <span style="color:red">*&nbsp;</span>手机号码：
                                </div>
                            </td>
                             <td colspan="3">
                             	<input id="companyMobile" data-dojo-type="dijit/form/ValidationTextBox"
                             		data-dojo-props='name:"companyMobile",${fieldAcl.isReadOnly("companyMobile")},
                                		"class":"input",
                                		style:{width:"180px",marginLeft:"1px"},
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入手机号码...",
              							value:"${company?.companyMobile}"
                                '/>
    						</td>
                        </tr>
						<tr>
                        	<td>
                                <div align="right" >电话号码：</div>
                            </td>
                             <td colspan="3">
                             	<input id="companyPhone" data-dojo-type="dijit/form/ValidationTextBox"
                             		data-dojo-props='name:"companyPhone",${fieldAcl.isReadOnly("companyPhone")},
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
                             <td colspan="3">
                             	<input id="companyFax" data-dojo-type="dijit/form/ValidationTextBox"
                             		data-dojo-props='name:"companyFax",${fieldAcl.isReadOnly("companyFax")},
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
                             <td colspan="3">
                             	<input id="companyAddress" data-dojo-type="dijit.form.ValidationTextBox"
                             		data-dojo-props='name:"companyAddress",${fieldAcl.isReadOnly("companyAddress")},
                                		"class":"input",
                                		style:{width:"400px",marginLeft:"1px"},
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入地址...",
              							value:"${company?.companyAddress}"
                                '/>
    						</td>
                        </tr>
                        <tr>
                        	<td>
                                <div align="right" >
                                    <span style="color:red">*&nbsp;</span>模块是否启用：
                                </div>
                            </td>
                             <td colspan="3">
                             	<input id="isTurnon1" data-dojo-type="dijit/form/RadioButton"
                             		data-dojo-props='name:"isTurnon",
                             			type:"radio",
                             			${fieldAcl.isReadOnly("isTurnon")},
                             			<g:if test="${company?.isTurnon }">checked:true,</g:if>
              							value:"true"
                                '/>
								<label for="isTurnon1">是</label>
								
								<input id="isTurnon2" data-dojo-type="dijit/form/RadioButton"
                             		data-dojo-props='name:"isTurnon",
                             			type:"radio",
                             			${fieldAcl.isReadOnly("isTurnon")},
                             			<g:if test="${!company?.isTurnon }">checked:true,</g:if>
              							value:"false"
                                '/>
								<label for="isTurnon2">否</label>
    						</td>
                        </tr>
                        <tr>
                        	<td>
                                <div align="right" >
                                    <span style="color:red">*&nbsp;</span>短消息是否启用：
                                </div>
                            </td>
                             <td colspan="3">
                             	<input id="isSmsOn1" data-dojo-type="dijit/form/RadioButton"
                             		data-dojo-props='name:"isSmsOn",
                             			type:"radio",
                             			${fieldAcl.isReadOnly("isSmsOn")},
                             			<g:if test="${company?.isSmsOn }">checked:true,</g:if>
              							value:"true"
                                '/>
								<label for="isSmsOn1">是</label>
								
								<input id="isSmsOn2" data-dojo-type="dijit/form/RadioButton"
                             		data-dojo-props='name:"isSmsOn",
                             			type:"radio",
                             			<g:if test="${!company?.isTurnon }">checked:true,</g:if>
              							value:"false"
                                '/>
								<label for="isSmsOn2">否</label>
    						</td>
                        </tr>
                        <tr>
                        	<td>
                                <div align="right" >
                                    <span style="color:red">*&nbsp;</span>外网邮箱是否开通：
                                </div>
                            </td>
                             <td colspan="3">
                             	<input id="isOnMail1" data-dojo-type="dijit/form/RadioButton"
                             		data-dojo-props='name:"isOnMail",
                             			type:"radio",
                             			${fieldAcl.isReadOnly("isOnMail")},
                             			<g:if test="${company?.isOnMail }">checked:true,</g:if>
              							value:"true"
                                '/>
								<label for="isOnMail1">是</label>
								
								<input id="isOnMail2" data-dojo-type="dijit/form/RadioButton"
                             		data-dojo-props='name:"isOnMail",
                             			type:"radio",
                             			<g:if test="${!company?.isOnMail }">checked:true,</g:if>
              							value:"false"
                                '/>
								<label for="isOnMail2">否</label>
    						</td>
                        </tr>
						<tr>
                        	<td>
                                <div align="right" >内容描述：</div>
                            </td>
                             <td colspan="3">
                             	<textarea id="description" data-dojo-type="dijit/form/SimpleTextarea"
                             		data-dojo-props='name:"description",${fieldAcl.isReadOnly("description")},
                                		"class":"input",
                                		style:{width:"400px",marginLeft:"1px"},
                                		trim:true,
              							value:"${company?.description}"
                                '></textarea>
    						</td>
                        </tr>
                    </tbody>
                </table>
            </fieldset>
	</form>
	</div>
</body>
</html>