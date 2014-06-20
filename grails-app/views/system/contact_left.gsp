<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>通讯录导航</title>
    <style type="text/css">
    	.contactShow ul{
    		list-style:none;padding:0px;
    		text-align:center;
    	}
    	.contactShow ul li{
    		list-style:none;
    		height:25px;
    		line-height:25px;
    		margin:10px;
    	}
	</style>
	<script type="text/javascript">
	</script>
</head>
<body>
	<div class="contactShow">
	<input  data-dojo-type="dijit/form/ValidationTextBox" id="contactShowId"  data-dojo-props='style:{display:"none"},value:"${contactShowId}"' />
	<ul>
		<g:each in="${departsList}">
			<li>
			<button onClick="top_showDepartInfor('${it.id}')" data-dojo-props=''
				data-dojo-type="dijit/form/Button"><div style="width:158px">${it.departName }</div></button>
			</li>
		</g:each>
	</ul>
	</div>
</body>
</html>