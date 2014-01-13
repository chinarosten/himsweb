/**
 * @author rosten
 */
define(["dojo/_base/declare", 
		"dijit/TitlePane",
		"dojo/dom-style"
		], 
		function(declare,TitlePane,domStyle) {
    return declare("rosten.widget.TitlePane",TitlePane, {
    	
    	// click tools ,so cancle the toggle
        _canToggle: true,
        
        width: "",
        height: "",
        marginBottom: "6px",
								
		templateString: '<div class="dijitTitlePane">' +
        '   <div style="vertical-align:middle" class="dijitTitlePaneTitle" data-dojo-attach-point="titleBarNode,focusNode" dojoAttachEvent="onclick:toggle,onkeypress: _onTitleKey" tabindex="0" waiRole="button" >' +
        '      <div data-dojo-attach-point="arrowNode" class="dijitInline dijitArrowNode  dijitTitlePaneTitleFocus" style="float:right">' +
        '         <span data-dojo-attach-point="arrowNodeInner" class="dijitArrowNodeInner"></span>' +
        '      </div>' +
        '      <div data-dojo-attach-point="titleNode" class="dijitTitlePaneTextNode  dijitTitlePaneTitleFocus"></div>' +
        '   </div>' +
        '   <div class="dijitTitlePaneContentOuter" data-dojo-attach-point="hideNode">' +
        '      <div class="dijitReset" dojoAttachPoint="wipeNode">' +
        '        <div class="dijitTitlePaneContentInner" data-dojo-attach-point="containerNode" waiRole="region" tabindex="-1"></div>' +
        '      </div>' +
        '   </div>' +
        '</div>',
    	
    	postCreate: function(){
            rosten.widget.TitlePane.superclass.postCreate.apply(this, arguments);
            domStyle.set(this.domNode, {
                "marginBottom": this.marginBottom
            });
            if (this.width != "") {
                domStyle.set(this.domNode, {
                    "width": this.width
                });
            }
            if (!this._canToggle) {
                domStyle.set(this.arrowNode, {
                    "display": "none"
                });
            }
            if (this.height != "") {
                domStyle.set(this.containerNode, {
                    "height": this.height
                });
            }
        },
    	toggle: function(){
            if (!this._canToggle) {
                console.log("can not toggle......");
                return;
            }
            else {
                rosten.widget.TitlePane.superclass.toggle.apply(this, arguments);
            }
        },
        cancelToggle: function(){
            this._canToggle = false;
            domStyle.set(this.arrowNode, {
                "display": "none"
            });
        },
    	doToggle: function(){
            this._canToggle = true;
            domStyle.set(this.arrowNode, {
                "display": ""
            });
        },
        changeWidth: function(){
            domStyle.set(this.domNode, {
                "width": this.width
            });
        }
    	
    });
});
