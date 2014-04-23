<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="Rosten" /></title>
        <link rel="shortcut icon" href="${resource(dir:'images/rosten/share',file:'rosten_logo_R.ico')}" type="image/x-icon" />
        <r:cssLoad dir="js/dojo/resources" file="dojo.css"/>
        <r:cssLoad dir="css/rosten" file="rosten.css" id="rostenCss"/>
        <r:cssLoad dir="js/dijit/themes/claro" file="claro.css"/>
        <script type="text/javascript">
        	dojoConfig = {
        		has : {
        			"dojo-firebug" : false,
        			"dojo-debug-messages" : true
        		},
        		parseOnLoad : true,
        		async : true
        	};
        </script>
        <r:jsLoad dir="js/dojo" file="dojo.js"/>
        <r:jsLoad dir="js/rosten" file="rosten.js"/>
        <g:layoutHead />
    </head>
    <body class="claro rosten">
        <g:layoutBody />
    </body>
</html>
