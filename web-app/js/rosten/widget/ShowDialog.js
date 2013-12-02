/**
 * @author rosten
 */
define(["dojo/_base/declare",
		"dojo/_base/connect",
		"dijit/_WidgetBase", 
		"dijit/_TemplatedMixin",
		"rosten/widget/RostenDialog"], function(declare,connect,_WidgetBase,_TemplatedMixin,RostenDialog) {
	return declare("rosten.widget.ShowDialog", [_WidgetBase, _TemplatedMixin], {
		templateString: '<div dojoAttachPoint="containerNode"></div>',
        dialog: null,
        src: "",
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
                this.dialog.setHref(this.src);
                if (this.onLoadFunction != null) {
                    connect.connect(this.dialog, "onLoad", this.onLoadFunction);
                }
            }
            this.show();
        },
        hide: function(){
            this.dialog.hide();
        },
        show: function(){
            this.dialog.show();
        },
        getGrid: function(){
            return this.dialog;
        },
        destroy: function(){
            this.dialog.destroyRecursive();
        }
	});
});