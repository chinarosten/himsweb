/**
 * @author rosten
 */
define(["dojo/_base/declare",
		"dojo/_base/connect",
		"dijit/_WidgetBase", 
		"dijit/_TemplatedMixin",
		"dojox/layout/ContentPane",
		"rosten/widget/RostenDialog"], function(declare,connect,_WidgetBase,_TemplatedMixin,ContentPane,RostenDialog) {
	return declare("rosten.widget.ShowDialog1", [_WidgetBase, _TemplatedMixin], {
		templateString: '<div dojoAttachPoint="containerNode"></div>',
        dialog: null,
        dlgContentPane: null,
        src: "",
        position_absolute_x: 200,
        position_absolute_y: 200,
        callback: null,
        callbackargs: null,
        onLoadFunction: null,
        postCreate: function(){
            this.dialog = new RostenDialog({
                id: "rosten_showDialog",
                renderStyles: true
            }, this.containerNode.domNode);
            
            if (this.src == "") {
                this.dialog.setContent("error:can not get the data......");
            }
            else {
                this.dlgContentPane = new ContentPane({
                    id: "rosten_dialogContent",
                    renderStyles: true
                });
                this.dlgContentPane.setHref(this.src);
                this.dialog.setContent(this.dlgContentPane.domNode);
                this.dlgContentPane.startup();
                if (this.onLoadFunction != null) {
                    connect.connect(this.dlgContentPane, "onLoad", this.onLoadFunction);
                }
            }
            this.show();
        },
        hide: function(){
            this.dialog.hide();
        },
        show: function(){
        	this.dialog.show();
        	 //以下位置暂时不启用
            this.dialog.domNode.style.left = this.position_absolute_x + "px";
            this.dialog.domNode.style.top = this.position_absolute_y + "px";
            //position
//            this.dialog._position();
//			this.dialog.layout();
        },
        getGrid: function(){
            return this.dialog;
        },
        changePosition: function(x, y){
            this.position_absolute_x = x;
            this.position_absolute_y = y;
            this.dialog.domNode.style.left = this.position_absolute_x + "px";
            this.dialog.domNode.style.top = this.position_absolute_y + "px";
            
        },
        destroy: function(){
            //		this.dialog.destroy();
            this.dialog.destroyRecursive();
            this.dlgContentPane.destroyRecursive();
        }
        
	});
});