/**
 * @author rosten
 * @created 2013-12-01
 */
define(["dojo/_base/declare", "dojo/_base/kernel", "dojo/dom-style", "dojo/_base/connect", "dojo/query!css3", "dijit/registry", "dijit/form/Button", "rosten/widget/RostenDialog"], function(declare, kernel, domStyle, connect, query, registry, Button, RostenDialog) {

	return declare("rosten.widget._Dialog", null, {
		_dialog : null,
		height : "320px",
		width : "350px",
		id : "system_dialog",
		contentPane : null,
		controlPane : null,
		title : "Dialog_Title",
		callback : null,
		queryDlgClose : null,
		mode : "OKCANCEL",
		initialized : false,
		constructor : function(arguments) {
			for (var property in arguments) {
				this[property] = arguments[property];
			}
			this._dialog = registry.byId(this.id);

			if (!this._dialog) {
				this.contentPane = document.createElement("div");
				this.contentPane.setAttribute("id", "system_dialog_contentpane");

				this.controlPane = document.createElement("div");
				this.controlPane.setAttribute("id", "system_dialog_controlpane");

				this._dialog = new RostenDialog({
					id : this.id,
					refocus : false,
					// disableCloseButton:false,
					title : this.title
				}, document.createElement("div"));
				domStyle.set(this._dialog.domNode, "width", this.width);
				domStyle.set(this._dialog.domNode, "height", this.height);
				this._dialog.containerNode.appendChild(this.contentPane);
				this._dialog.containerNode.appendChild(this.controlPane);
				connect.connect(this._dialog, "hide", this, function() {
					// 为了使hide后显示界面在下一个界面出来前完全消失，采用摧毁方法
					this._dialog.destroy();
					/*
					 * 有时候会出现无法打开被屏蔽的后台页面 故进行特殊处理
					 */
					/*
					 * var _DialogUnderlay =
					 * dojo.query(".dijitDialogUnderlayWrapper");
					 * dojo.forEach(_DialogUnderlay, function(node){
					 * if(node.style.display!="none"){ node.style.display =
					 * "none"; } })
					 */
				});
			} else {
				this.contentPane = kernel.query("#system_dialog_contentpane",
				this._dialog.domNode)[0];
				this.controlPane = dojo.query("#system_dialog_controlpane",
				this._dialog.domNode)[0];
				this.initialized = true;
			}
		},
		open : function() {
			if (this.initialized == false) {
				this.buildContent(this.contentPane);
				this.buildControl(this.controlPane);
				this.initialized = true;
			}
			this._dialog.show();
		},
		close : function() {
			this._dialog.hide();
			this.queryDlgClose();
		},
		destroy : function() {
			if (this._dialog)
				this._dialog.destroy();
		},
		doAction : function() {
			this._dialog.hide();
			var data = this.getData();
			this.callback(data);
		},
		buildControl : function(node) {
			if (this.mode == "OKCANCEL") {
				var btn1 = new Button({
					label : "确定",
					showLabel : true,
					iconClass : "okIcon"
				}, document.createElement("div"));
				var btn2 = new Button({
					label : "取消",
					showLabel : true,
					iconClass : "cancelIcon"
				}, document.createElement("div"));

				connect.connect(btn2, "onClick", this, "close");
				connect.connect(btn1, "onClick", this, "doAction");
				node.appendChild(btn1.domNode);
				node.appendChild(btn2.domNode);
				domStyle.set(node, "marginRight", "auto");
				domStyle.set(node, "marginLeft", "auto");
				domStyle.set(node, "textAlign", "center");
			} else if (this.mode == "CLOSE") {
				var btn1 = new Button({
					label : "关闭",
					showLabel : true,
					iconClass : "okIcon"
				}, document.createElement("div"));
				connect.connect(btn1, "onClick", this, "close");
				node.appendChild(btn1.domNode);
				domStyle.set(node, "marginRight", "auto");
				domStyle.set(node, "marginLeft", "auto");
				domStyle.set(node, "textAlign", "center");
			}
		},
		getContentPane : function() {
			return this.contentPane;
		},
		getControlPane : function() {
			return this.controlPane;
		},
		buildContent : function(node) {
			node.innerHTML = "Loading....";
		},
		getData : function() {
		},
		callback : function() {
		},
		queryDlgClose : function() {
		},
		refresh : function() {
			this.contentPane = query("#system_dialog_contentpane",
			this._dialog.domNode)[0];
			this.contentPane.innerHTML = "";
			this.buildContent(this.contentPane);
		}
	});
});
