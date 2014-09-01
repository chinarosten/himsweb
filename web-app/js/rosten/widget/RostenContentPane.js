/**
 * @author rosten
 */
define(["dojo/_base/declare",
		"dojo/_base/connect",
		"dijit/_WidgetBase", 
		"dijit/_TemplatedMixin",
		"dojox/layout/ContentPane",
		], function(declare,connect,_WidgetBase,_TemplatedMixin,ContentPane) {
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
            this.contentPane.setHref(this.src);
            this.containerNode.appendChild(this.contentPane.domNode);
        },
        destroy: function(){
            this.contentPane.destroyRecursive();
        }
        
	});
});