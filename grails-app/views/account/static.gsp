<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>统计查询</title>
	<script type="text/javascript">

		require([
				"dijit/registry",
				"dijit/form/SimpleTextarea",
		 		"dijit/form/ValidationTextBox",
		 		"rosten/widget/ActionBar"
		     	],function(registry){
	     	
			

		});	

		
    </script>
</head>
<body>
<div class="rosten_action">
	<div data-dojo-type="rosten/widget/ActionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'accountAction',action:'accountStatic')}"'>
	</div>
</div>
<div style="text-Align:center;margin-top:10px;margin-left:10px" class="personSearch">
	<table border="0" width="600px" class="tab_css" align="left">
		<tr>
			<td width="300">项目名称</td>
			<td width="150">支出总金额 (元)</td>
			<td width="150">收入总金额 (元)</td>
		</tr>
		<g:each in="${resultList}">
			<tr>
				<td>${it.name }</td>
				<td>${it.zcmoney }</td>
				<td>${it.srmoney }</td>
			</tr>
		
		</g:each>
	</table>
</div>
</body>
</html>