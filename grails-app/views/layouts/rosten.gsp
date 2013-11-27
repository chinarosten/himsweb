<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="Rosten" /></title>
        <link rel="shortcut icon" href="${resource(dir:'images/rosten/share',file:'rosten_logo_R.ico')}" type="image/x-icon" />
        <r:jsLoad dir="js/rosten" file="rosten.js"/>
        <r:jsLoad dir="js/dojo" file="dojo.js"/>
		<r:cssLoad dir="js/dojo/resources" file="dojo.css"/>
        <r:cssLoad dir="css/rosten" file="rosten.css" id="rostenCss"/>
        <r:cssLoad dir="js/rosten/widget/css" file="rostenGrid.css"/>
		<script type="text/javascript">
			require(["rosten/kernel/_kernel"],function(rosten){
				rosten.init({webpath:"/himsweb",gridcss:true,loadKernel:true});
			});
		</script>
        <g:layoutHead />
    </head>
    
    <body class="claro rosten">
        <g:layoutBody />
    </body>
</html>
