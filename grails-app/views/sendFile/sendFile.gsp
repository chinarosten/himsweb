<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>发文管理</title>
	<script type="text/javascript">
		dojo.require("dijit.layout.BorderContainer");
		dojo.require("dojox.layout.ContentPane");
		dojo.require("dijit.layout.TabContainer");
		dojo.require("dijit.layout.ContentPane");
		
		dojo.require("dijit.form.ValidationTextBox");
		dojo.require("dijit.form.DateTextBox");
		dojo.require("dijit.form.FilteringSelect");
		dojo.require("dijit.form.Button");

		dojo.require("dojo.data.ItemFileReadStore");

		sendfile_Save = function(){
			if(usual_cardCode_fieldCheck()==false) return;
			
			var content = {};
			content.companyId = "${companyId}";
			content.consumerId = "${consumerId}";
			content.userId = rosten.kernel.getUserInforByKey("idnumber");
			
			rosten.readerByFormSync("${createLink(controller:'consumer',action:'consumerCardCodeSave')}","cardCode_form",content,function(data){
				if(data.result=="true" || data.result == true){
					rosten.alert("保存成功！").queryDlgClose= function(){
						dijit.byId("cardCodeDlg").hide();	
						dijit.byId("consumeCardCodeShow_Grid").refresh();
					};
				}else{
					rosten.alert("保存失败!");
				}
			});
		}
		sendfile_fieldCheck = function(){
			var cardcode = dijit.byId("cardcode");
			if(!cardcode.isValid()){
				rosten.alert("卡号不正确！").queryDlgClose = function(){
					cardcode.focus();
				};
				return false;
			}
		}
		
    </script>
</head>
<body>
<div class="rosten_action">
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="sendFile_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'sendFileAction',action:'sendFileForm')}"'>
	</div>
</div>
<g:form id="sendfile_form" name="sendfile_form" url='[controller:"sendFile",action:"sendFileSave"]' class="rosten_form" >
	<table border="0" width="740" align="left">
		<tr>
		    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>标题：</div></td>
		    <td width="250">
		    	<input id="cardcode" data-dojo-type="dijit.form.ValidationTextBox" 
                 	data-dojo-props='name:"cardcode",${fieldAcl.isReadOnly("cardcode")},
                 		trim:true,
                 		required:true,
                 		promptMessage:"请正确输入客户卡号...",
						value:"${cardCode?.cardcode}"
                '/>
		    </td>
		    <td width="120"><div align="right">卡类型：</div></td>
		    <td width="250">
		    	<div data-dojo-type="dojo.data.ItemFileReadStore" data-dojo-id="rosten.storeData.cardType"
					data-dojo-props='url:"${createLink(controller:'config',action:'cardType_GetInforAll',params:[companyId:companyId]) }"'></div>
					
				<select id="cardtype" data-dojo-type="dijit.form.FilteringSelect" 
					data-dojo-props='name:"cardtype",${fieldAcl.isReadOnly("cardtype")},
						store:rosten.storeData.cardType,
						trim:true,
						required:true,
						searchAttr:"cardTypeName",
						value:"${cardCode?.cardtype?.id}"
					'>	
					<script type="dojo/method" event="onChange">
						if(this.item){
							dijit.byId("discount").attr("value",this.item.discount);
							dijit.byId("productDiscount").attr("value",this.item.productDiscount);
							dijit.byId("technicDiscount").attr("value",this.item.technicDiscount);
							dijit.byId("term").attr("value",this.item.term);
							dijit.byId("money").attr("value",this.item.lowMoney);	
							dijit.byId("amount").attr("value",this.item.amount);	
							dijit.byId("grade").attr("value",this.item.grade);		
						}
					</script>
				</select>	
           </td>
		</tr>
		<tr>
		    <td><div align="right"><span style="color:red">*&nbsp;</span>客户密码：</div></td>
		    <td>
		    	<input id="password" data-dojo-type="dijit.form.ValidationTextBox" 
                 	data-dojo-props='name:"password",${fieldAcl.isReadOnly("password")},
                 		type:"password",
                 		trim:true,
                 		required:true,
						value:"${cardCode?.password}"
                '/>
		    
		    <td><div align="right">享受折扣：</div></td>
		    <td>
		    	<input id="discount" data-dojo-type="dijit.form.ValidationTextBox" 
                 	data-dojo-props='name:"discount",${fieldAcl.isReadOnly("discount")},
                 		trim:true,
						value:"${cardCode?.discount}"
                '/>
            </td>    
		</tr>
		
	</table>
</g:form>
</body>