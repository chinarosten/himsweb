/**
 * @author rosten
 * @created 2013-11-13
 * control version
 */
define([
	"dojo/_base/window", 
	"dojo/_base/kernel", 
	"dojo/_base/lang",
	"dojo/_base/xhr",
	"dojo/dom-construct", 
	"dojo/dom", 
	"dojo/has", 
	"dojo/dom-class"], function(win, kernel, lang, xhr, domConstruct, dom, has, domClass) {
	var rosten = {
		variable : {},
		dojoPath : "js", //以当前载入的html页面判断dojo所在目录
		dojothemecss : "claro", //采用dojo的默认样式
		rostenthemecss : "normal", //rosten定制css样式
		webPath : "/himsweb"	//根路径
	};
	rosten.setDojoVersion = function(dojoVersion) {
		rosten.dojoVersion = dojoVersion;
	};
	rosten.getDojoVersion = function() {
		return rosten.dojoVersion;
	};
	rosten.setDojoPath = function(dojoPath) {
		rosten.dojoPath = dojoPath;
	};
	rosten.getDojoPath = function() {
		return rosten.dojoPath;
	};
	rosten.setDojoThemeCss = function(themecss) {
		rosten.dojothemecss = themecss;
	};
	rosten.setRostenThemeCss = function(themecss) {
		rosten.rostenthemecss = themecss;
	};
	/*
	 * 异步后台获取------无时间限制
	 */
	rosten.readNoTime = rosten._readNoTime = function(url, content, callback, errorback, formname) {
		var ioArgs = {
			url : url,
			handleAs : "json",
			preventCache : true,
			content : content,
			encoding : "utf-8"
		};
		if (formname) {
			ioArgs.form = formname;
		}
		if (lang.isFunction(callback)) {
			ioArgs.load = callback;
		}
		if (lang.isFunction(errorback)) {
			ioArgs.error = errorback;
		} else {
			ioArgs.error = function(response, args) {
				console.log(response);
			};
		}
		xhr.post(ioArgs);
	};
	/*
	 * 异步后台获取------有3000毫秒默认限制
	 */
	rosten.read = rosten._read = function(url, content, callback, errorback, formname) {
		var ioArgs = {
			url : url,
			timeout : 3000,
			handleAs : "json",
			preventCache : true,
			content : content,
			encoding : "utf-8"
		};
		if (formname) {
			ioArgs.form = formname;
		}
		if (lang.isFunction(callback)) {
			ioArgs.load = callback;
		}
		if (lang.isFunction(errorback)) {
			ioArgs.error = errorback;
		} else {
			ioArgs.error = function(response, args) {
				console.log(response);
			};
		}
		xhr.post(ioArgs);
	};
	/*
	 * 同步后台获取内容-----有3000毫秒默认时间限制
	 */
	rosten.readSync = rosten._readSyncByForm = function(url, content, callback, errorback, formname) {
		var ioArgs = {
			url : url,
			timeout : 3000,
			handleAs : "json",
			preventCache : true,
			encoding : "utf-8",
			content : content
		};
		if (formname) {
			ioArgs.form = formname;
		}
		if (lang.isFunction(callback)) {
			ioArgs.load = callback;
		}
		if (lang.isFunction(errorback)) {
			ioArgs.error = errorback;
		} else {
			ioArgs.error = function(response, args) {
				console.log(response);
			};
		}
		xhr.post(ioArgs);
	};
	/*
	 * 同步后台获取内容-----无时间限制
	 */
	rosten.readSyncNoTime = rosten._readerNoTime = function(url, content, callback, errorback, formname) {
		var ioArgs = {
			url : url,
			sync : true,
			handleAs : "json",
			preventCache : true,
			content : content,
			encoding : "utf-8"
		};
		if (formname) {
			ioArgs.form = formname;
		}
		if (lang.isFunction(callback)) {
			ioArgs.load = callback;
		}
		if (lang.isFunction(errorback)) {
			ioArgs.error = errorback;
		} else {
			ioArgs.error = function(response, args) {
				console.log(response);
			};
		}
		xhr.post(ioArgs);
	};
	rosten.openBigWindow = function(url, wName) {
		if (url !== undefined && url !== null && url !== '') {

			var scrWidth = screen.availWidth;
			var scrHeight = screen.availHeight;
			if (!wName)
				wName = "_blankwindow";

			var self = window.open(url, wName, "resizable=yes,toolbar=yes, menubar=yes,   scrollbars=yes,location=yes, status=yes,top=0,left=0,width=" + scrWidth + ",height=" + scrHeight);
			self.resizeTo(scrWidth, scrHeight);
			self.moveTo(0, 0);
			self.focus();
			return false;
		}
	};
	rosten.openNewWindow = function(wName, url) {
		var width = screen.availWidth - 10;
		var height = screen.availHeight - 15;

		// var params = "height=" + height + ",width=" + width + ",toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=yes,border=0,top=0,left=0";
		var params = "";
		if (!wName)
			wName = "_blankwindow";

		// var docwin = window.open(url, wName, params);
		// return docwin;

		//当前只允许用户打开单个子窗口
		rosten.closeChildWin();
		rosten.variable.childWindow = window.open(url, wName, params);
		return rosten.variable.childWindow;
	};
	rosten.closeChildWin = function() {
		if (rosten.variable.childWindow != null && rosten.variable.childWindow.open) {
			rosten.variable.childWindow.close();
			rosten.variable.childWindow = null;
		}
	};
	rosten.expandTreeNode = function(treeObj) {
		if (!treeObj.isExpanded) {
			treeObj.rootNode.expand();
		}
		var children = treeObj.rootNode.getChildren();
		rosten._expandChildNode(children, treeObj);
	};
	rosten._expandChildNode = function(children, treeObj) {
		for (var i = 0; i < children.length; i++) {
			var node = children[i];
			if (node.isExpandable && !node.isExpanded) {
				treeObj._expandNode(node);
			}
			var childNodes = node.getChildren();
			if (childNodes.length > 0) {
				rosten._expandChildNode(childNodes, treeObj);
			}
		}
	};
	/*
	 * 更换rosten的theme
	 */
	rosten.replaceRostenTheme = function(cssname) {

		if (!domClass.contains(win.body(), "rosten")) {
			domClass.add(win.body(), "rosten");
		}
		rosten.addRostenCss(cssname);
	};
	/*
	 * 定制rosten的css样式
	 */
	rosten.addRostenCss = function(csspath) {
		if (csspath) {
			rosten.setRostenThemeCss(csspath);
		}
		var rostenCssNode = dom.byId("rostenThemeCss");
		if (rostenCssNode) {
			domConstruct.destroy(rostenCssNode);
		}
		var rostencss = rosten.webPath + "/css/rosten/themes/" + rosten.rostenthemecss + "/css/" + rosten.rostenthemecss + ".css";
		rosten.addCSSFile(rostencss, "rostenThemeCss");
	};
	/*
	 * 增加css样式
	 */
	rosten.addCSSFile = function(cssFile, idname) {
		var head = document.getElementsByTagName('head').item(0);
		var cssLink = document.createElement("link");
		if (idname) {
			cssLink.setAttribute("id", idname);
		}
		cssLink.setAttribute("rel", "stylesheet");
		cssLink.setAttribute("type", "text/css");
		if (has("ie")) {
			cssLink.href = cssFile;
		} else {
			cssLink.setAttribute("href", cssFile);
		}
		head.appendChild(cssLink);
	};
	/*
	 * 更换dojo缺省样式；model:true表示需要更换grid表格样式
	 */
	rosten.replaceDojoTheme = function(cssname, model) {
		var beforecss = rosten.dojothemecss;
		if (domClass.contains(win.body(), beforecss)) {
			domClass.remove(win.body(), beforecss);
		}
		if (!domClass.contains(win.body(), cssname)) {
			domClass.add(win.body(), cssname);
		}
		rosten.addDojoTheme(cssname);

		if (model && model == true) {
			rosten.addGridCss();
		}
	};
	/*
	 * 增加dojo的特定theme样式
	 */
	rosten.addDojoTheme = function(cssname) {
		if (cssname) {
			rosten.setDojoThemeCss(cssname);
		}
		var node = dom.byId("themeCss");
		if (node) {
			domConstruct.destroy(node);
		}
		var themeCss = kernel.moduleUrl("dijit.themes", rosten.dojothemecss + "/" + rosten.dojothemecss + ".css");
		rosten.addCSSFile(themeCss, "themeCss");
		console.log("luhangyu");
	};
	/*
	 * 添加表格css样式
	 * rosten.defthemecss为特定的表格grid样式
	 */
	rosten.addGridCss = function() {
		var gridCssNode = dom.byId("gridCss");
		if (gridCssNode) {
			domConstruct.destroy(gridCssNode);
		}
		var gridcss = kernel.moduleUrl("dojox", "grid/resources/Grid.css");
		rosten.addCSSFile(gridcss, "gridCss");

		var themeGridCssNode = dom.byId("themeGridCss");
		if (themeGridCssNode) {
			domConstruct.destroy(themeGridCssNode);
		}
		var themegridcss = kernel.moduleUrl("dojox", "grid/resources/" + rosten.dojothemecss + "Grid.css");
		rosten.addCSSFile(themegridcss, "themeGridCss");
	};
	rosten.loadBaseJs = function() {
		if ( typeof (dojo) == "undefined") {
			var href = window.location.href;
			var isDebug = "false";
			if (href.indexOf("isDebug") != -1) {
				isDebug = "true";
			}
			var dojolib = "/" + rosten.getDojoVersion() + "/dojo/dojo.js";
			document.write("<script type=\"text/javascript\" src=\"" + dojolib + "\" data-dojo-config=\"parseOnLoad:true,has:{'dojo-firebug':" + isDebug + "},async:true\"><\/script>");
		}
		rosten.addThemeCss(rosten.dojothemecss);
	};
	rosten.init = function(datajson) {
		if (datajson.webpath) {
			rosten.webPath = datajson.webpath;
		}
		var dojocss;
		if (datajson.dojocss) {
			dojocss = datajson.dojocss;
		} else {
			dojocss = rosten.dojothemecss;
		}
		if (datajson.gridcss) {
			rosten.replaceDojoTheme(dojocss, true);
		} else {
			rosten.replaceDojoTheme(dojocss, false);
		}
		if (datajson.rostencss) {
			rosten.addRostenCss(datajson.rostencss);
		}
		if (datajson.loadKernel) {
			require("rosten.kernel._kernel");
		}
	};

	return rosten;
});
