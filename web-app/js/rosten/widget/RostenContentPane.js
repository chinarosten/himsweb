define(["dojo/_base/declare",
		"dijit/_WidgetBase", 
		"dijit/_TemplatedMixin",
		"dojox/layout/ContentPane"
		], function(declare,_WidgetBase,_TemplatedMixin,ContentPane) {
	return declare("rosten.widget.RostenContentPane", [_WidgetBase, _TemplatedMixin], {
		templateString: '<div data-dojo-attach-point="containerNode"></div>',
        contentPane: null,
        src: "",
        postCreate: function(){
        	this.contentPane = new ContentPane({
                renderStyles: true,
                executeScripts:true,
                style:{"padding":"2px","paddingLeft":"0px","paddingRight":"0px"}
            });
            this.contentPane.set("href",this.src);
            this.containerNode.appendChild(this.contentPane.domNode);
        },
        destroy: function(){
            this.contentPane.destroyRecursive();
        }
        
	});
});