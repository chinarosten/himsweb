<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>顾客信息</title>
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

		usual_consumer_cardcodeSave = function(){
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
		usual_cardCode_fieldCheck = function(){
			var cardcode = dijit.byId("cardcode");
			if(!cardcode.isValid()){
				rosten.alert("卡号不正确！").queryDlgClose = function(){
					cardcode.focus();
				};
				return false;
			}
			var cardType = dijit.byId("cardtype");
	    	if(!cardType.isValid()){
				rosten.alert("卡类型不正确！").queryDlgClose = function(){
					cardType.focus();
				};
				return false;
			}else{
				if(cardType.getValue()==""){
					rosten.alert("卡类型不正确!").queryDlgClose = function(){
						cardType.focus();
					};
					return false;
				}
			}
	    	var password = dijit.byId("password");
			if(!password.isValid()){
				rosten.alert("密码不正确！").queryDlgClose = function(){
					password.focus();
				};
				return false;
			}
			// 确认密码
			var checkPassword = dijit.byId("checkPassword");
			if(!checkPassword.isValid()){
				rosten.alert("确认密码不正确！").queryDlgClose = function(){
					checkPassword.focus();
				};
				return false;
			}
			if(password.getValue()!=checkPassword.getValue()){
				rosten.alert("密码与确认密码不一致！").queryDlgClose = function(){
					password.focus();
				};
				return false;
			}
			// 折扣
			var discount = dijit.byId("discount");
			if(!discount.isValid()){
				rosten.alert("护理折扣不正确！").queryDlgClose = function(){
					discount.focus();
				};
				return false;
			}
			// 折扣
			var productDiscount = dijit.byId("productDiscount");
			if(!productDiscount.isValid()){
				rosten.alert("产品折扣不正确！").queryDlgClose = function(){
					productDiscount.focus();
				};
				return false;
			}
			// 折扣
			var technicDiscount = dijit.byId("technicDiscount");
			if(!technicDiscount.isValid()){
				rosten.alert("仪器折扣不正确！").queryDlgClose = function(){
					technicDiscount.focus();
				};
				return false;
			}
			//余额
			var money = dijit.byId("money");
			if(!money.isValid()){
				rosten.alert("余额不正确！").queryDlgClose = function(){
					money.focus();
				};
				return false;
			}else{
				//获取卡类型，以及对应的最低金额
				var next = true;
				var lowMoney;
				rosten.getStoreItem(cardType.store,{"id":cardType.attr("value")},function(items){
					for (var i = 0; i < items.length; i++) {
						lowMoney = items[i].lowMoney;
						//创建时余额必须大于对应最低金额
						if (parseFloat(money.attr("value")) < parseFloat(lowMoney)) {
							next = false;
						}
						break;
					}
				});
				if (!next){
					rosten.alert("余额必须大于" + lowMoney + "！").queryDlgClose = function(){
						money.focus();
					};
					return false;
				}	
			}
			//消费次数
			var amount = dijit.byId("amount");
			if(!amount.isValid()){
				rosten.alert("消费次数不正确！").queryDlgClose = function(){
					amount.focus();
				};
				return false;
			}
			// 状态
			var status = dijit.byId("status");
			if(!status.isValid()){
				rosten.alert("状态不正确！").queryDlgClose = function(){
					status.focus();
				};
				return false;
			}
			// 期限
			var term = dijit.byId("term");
			if(!term.isValid()){
				rosten.alert("期限不正确！").queryDlgClose = function(){
					term.focus();
				};
				return false;
			}
			//积分
			var grade = dijit.byId("grade");
			if(!grade.isValid()){
				rosten.alert("积分不正确！").queryDlgClose = function(){
					grade.focus();
				};
				return false;
			}
		}
		
    </script>
</head>
<body>
<g:form id="cardCode_form" name="cardCode_form" url='[controller:"consumer",action:"cardCodeSave"]' class="rosten_form" >
	<table border="0" width="740" align="left">
		<tr>
		    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>客户卡号：</div></td>
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
		<tr>
		    <td><div align="right"><span style="color:red">*&nbsp;</span>确认密码：</div></td>
		    <td>
		    	<input id="checkPassword" data-dojo-type="dijit.form.ValidationTextBox" 
                 	data-dojo-props='name:"checkPassword",${fieldAcl.isReadOnly("checkPassword")},
                 		type:"password",
                 		trim:true,
                 		required:true,
						value:"${cardCode?.password}"
                '/>
		    </td>
		    <td><div align="right">产品折扣：</div></td>
		    <td>
		    	<input id="productDiscount" data-dojo-type="dijit.form.ValidationTextBox" 
                 	data-dojo-props='name:"productDiscount",${fieldAcl.isReadOnly("productDiscount")},
                 		trim:true,
						value:"${cardCode?.productDiscount}"
                '/>
		    </td>
		</tr>
		<tr>
		    <td><div align="right"><span style="color:red">*&nbsp;</span>余额：</div></td>
		    <td>
		    	<input id="money" data-dojo-type="dijit.form.ValidationTextBox" 
                 	data-dojo-props='name:"money",${fieldAcl.isReadOnly("money")},
                 		trim:true,
                 		required:true,
                 		invalidMessage:"金额必须为数字",
						value:"${cardCode?.money}"
                '/>
		    </td>
		    <td><div align="right">仪器折扣：</div></td>
		    <td>
		    	<input id="technicDiscount" data-dojo-type="dijit.form.ValidationTextBox" 
                 	data-dojo-props='name:"technicDiscount",${fieldAcl.isReadOnly("technicDiscount")},
                 		trim:true,
						value:"${cardCode?.technicDiscount}"
                '/>
		    </td>
		  </tr>
		  <tr>
					    
		    <td><div align="right">消费次数：</div></td>
		    <td>
		    	<input id="amount" data-dojo-type="dijit.form.ValidationTextBox" 
                 	data-dojo-props='name:"amount",${fieldAcl.isReadOnly("amount")},
                 		trim:true,
						value:"${cardCode?.amount}"
                '/>
			<td><div align="right">状态：</div></td>
		  	<td>
		  		<select id="status" data-dojo-type="dijit.form.FilteringSelect" 
                	data-dojo-props='name:"status",${fieldAcl.isReadOnly("status")},
					value:"${cardCode?.status}"
               '>
					<option value="正常">正常</option>
					<option value="已注销">已注销</option>
					<option value="已过期">已过期</option>
			   	</select>
		  	</td>	
				
		  </tr>
		  <tr>
		  	<td><div align="right">所属店名：</div></td>
		  	<td>
		  		<input id="shopName" data-dojo-type="dijit.form.ValidationTextBox" 
                 	data-dojo-props='name:"shopName",${fieldAcl.isReadOnly("shopName")},
                 		trim:true,
						value:"${shopName}"
                '/>
		  	</td>
		  	
		  	<td><div align="right">使用期限：</div></td>
		  	<td>
		  		<input id="term" data-dojo-type="dijit.form.ValidationTextBox" 
                 	data-dojo-props='name:"term",${fieldAcl.isReadOnly("term")},
                 		trim:true,
                 		required:true,
						value:"${cardCode?.getTermValue()}"
                '/>
		  	</td>
		  </tr>
		  <tr>
		  	<td><div align="right">当前积分：</div></td>
		  	<td>
		  		<input id="grade" data-dojo-type="dijit.form.ValidationTextBox" 
                 	data-dojo-props='name:"grade",${fieldAcl.isReadOnly("grade")},
                 		trim:true,
                 		required:true,
						value:"${cardCode?.grade}"
                '/>
		  	</td>
		  	<td><div align="right">办卡时间：</div></td>
		  	<td>
		  		<input data-dojo-type="dijit.form.DateTextBox" 
                  	data-dojo-props='${fieldAcl.isReadOnly("createdDate")},
                  		trim:true,
						value:"${cardCode?.getFormattedCreatedDate()}"
                 '/>
		  	</td>
		  </tr>
	</table>
</g:form>
</body>