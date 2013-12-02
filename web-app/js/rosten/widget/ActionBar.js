/**
 * @author rosten
 */
define(["dojo/_base/declare", 
		"dojo/_base/kernel", 
		"dojo/_base/lang", 
		"dojo/_base/xhr", 
		"dojo/dom-style",
		"dijit/_WidgetBase", 
		"dijit/_TemplatedMixin", 
		"dijit/form/Button", 
		"dijit/Toolbar", 
		"dojo/_base/connect", 
		"rosten/kernel/_kernel"], 
			function(declare, 
				kernel, lang, xhr, domStyle,_WidgetBase, _TemplatedMixin, Button, Toolbar, connect, _kernel) {
    return declare("rosten.widget.ActionBar",[_WidgetBase, _TemplatedMixin], {
        id : "",
        actionBarSrc : "",
        actionTextHeight : 12,

        templateString : '<div dojoAttachPoint="containerNode" class="ActionBarOuter">actionBar is Loading...</div>',

        constructor : function() {
        },
        postCreate : function() {
            this.id = this.id != "" ? this.id : this.widgetId;

            this.containerNode.innerHTML = "";
            this.toolBar = new Toolbar({});
            this.containerNode.appendChild(this.toolBar.domNode);
            if (this.actionBarSrc == "") {
                this.containerNode.innerHTML = "remote actionBar data source required...";
            } else {
                this._getData(this.actionBarSrc);
            }
        },
        refresh : function(src) {
            console.log("ActionBar refresh");
            if (src && src != "") {
                this.actionBarSrc = src;
            }
            this.toolBar.destroyDescendants();
            this._getData(this.actionBarSrc);
            console.log("ActionBar refresh is end");
        },
        _getData : function(/*String*/url) {
            //          console.debug(url);
            var args = {
                url : url,
                handleAs : "json",
                load : lang.hitch(this, function(data) {
                    this._setListData(data);
                }),

                error : lang.hitch(this, function(data) {
                    console.log("ActionBar error occurred: ", data);
                    _kernel.errordeal(this.containerNode, "无法初始化操作条数据...");
                })
            };
            xhr.get(args);
        },
        _setListData : function(data) {

            for (var i = 0; i < data.length; i++) {
                var object = data[i];
                var button = new Button({
                    label : object.name,
                    iconClass : "actionBarIcon"
                });

                domStyle.set(button.domNode, "fontSize", this.actionTextHeight + "px");
                domStyle.set(button.iconNode, {
                    backgroundImage : "url('" + (object.img) + "')"
                });

                /*
                 * 兼容类似object.action值为"rosten.pagequit"时的特殊处理
                 * 此版本只兼容包含一个"rosten."号的情况
                 */
                if (object.action.indexOf(".") != -1) {
                    connect.connect(button, "onClick", rosten, rosten.stringRight(object.action, "."));
                } else {
                    connect.connect(button, "onClick", object.action);
                }
                this.toolBar.addChild(button);
            }

        }
    });

});
