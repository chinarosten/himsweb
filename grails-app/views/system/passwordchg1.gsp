<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>密码修改</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>
  
<body>
	<div style="text-Align:center">
        <div class="rosten_form" style="width:400px;text-align:left">
        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="dealunid"  data-dojo-props='name:"dealunid",style:{display:"none"},value:"${unid }"' />
            <fieldset class="fieldset-form">
                <legend class="tableHeader">用户密码修改</legend>
                <table class="tableData">
                    <tbody>
						<tr>
                            <td width="80">
                                <div align="right"> 新密码：</div>
                            </td>
                            <td  width="220">
                            	<input id="newpassword" data-dojo-type="dijit/form/ValidationTextBox"
                                	data-dojo-props='name:"newpassword",
                                		"class":"input",
                                		type:"password",
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入新密码..."
                                '/>
                            </td>
                        </tr>
                        <tr>
                           <td>
                                <div align="right" >确认密码：</div>
                            </td>
                            <td>
                            	<input id="newpasswordcheck" data-dojo-type="dijit/form/ValidationTextBox"
                                	data-dojo-props='name:"newpasswordcheck",
                                		"class":"input",
                                		type:"password",
                                		trim:true,
                                		required:true,
                                		promptMessage:"请正确输入确认密码..."
                                '/>
                            </td>
                        </tr>
						<tr>
							<td></td>
							<td>
								<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){changePasswordSubmit()}'>确定</button>
								<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){rosten.kernel.hideRostenShowDialog()}'>取消</button>
								
							</td>
						</tr>
                    </tbody>
                </table>
				
				
            </fieldset>
		</div>
	</div>
</body>
</html>
