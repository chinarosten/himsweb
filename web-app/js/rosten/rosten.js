/**
 * @author rosten
 * @created 2013-11-15
 * 
 */
(function() {
	rostenhas = {
		jsweb : "jsweb"
	};
	rostenhas.loadBaseJs = function(jsweb) {
		var href = window.location.href;
		var isDebug = "false";
		if (href.indexOf("isDebug") != -1) {
			isDebug = "true";
		}
		var _jsweb;
		if(jsweb){
			_jsweb = jsweb;
		}else{
			_jsweb = rostenhas.jsweb;
		}
		var dojolib = "/" + _jsweb + "/dojo/dojo.js";
		document.write("<script type=\"text/javascript\" src=\"" + dojolib + "\" data-dojo-config=\"parseOnLoad:true,has:{'dojo-firebug':" + isDebug + "},isDebug:1,async:true\"><\/script>");
	};
})();
