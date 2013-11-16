<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="Rosten" /></title>
        <script type="text/javascript">
			if(typeof(dojo)=="undefined"){
				var href = window.location.href;
	            var isDebug = "false";
	            if (href.indexOf("isDebug") != -1) {
	                isDebug = "true";
	            }
				var dojo_link = "${resource(dir:'js/dojo-release-1.6.1/dojo',file:'dojo.js')}"
				document.write("<script type=\"text/javascript\" src=\""+ dojo_link + "\" djConfig=\"parseOnLoad:true,isDebug:" + isDebug + ",usePlainJson:true,bindEncoding:'utf-8'\"><\/script>");
			}
		</script>
		<r:cssLoad dir="js/dojo-release-1.6.1/dojo/resources" file="dojo.css"/>
        <r:cssLoad dir="css/rosten" file="rosten.css" id="rostenCss"/>
        <r:cssLoad dir="js/rosten/widget/css" file="rostenGrid.css"/>
        <link rel="shortcut icon" href="${resource(dir:'images/rosten/share',file:'rosten_logo_R.ico')}" type="image/x-icon" />
        <r:jsLoad dir="js/rosten" file="rosten.js"/>
		<script type="text/javascript">rosten.init({webpath:"/himsweb",gridcss:true,loadKernel:true});</script>
        <g:layoutHead />
    </head>
    <body class="claro rosten">
        <g:layoutBody />
    </body>
</html>