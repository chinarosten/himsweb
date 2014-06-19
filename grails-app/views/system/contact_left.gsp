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
</head>
<body>
	<div class="contactShow">
	<ul>
		<g:each in="${departsList}">
			<li>
			<button onClick="top_showDepartInfor('${it.id}')" data-dojo-props='style:{width:"100px"}'
				data-dojo-type="dijit/form/Button">${it.departName }</button>
			</li>
		</g:each>
	</ul>
	</div>
</body>
</html>