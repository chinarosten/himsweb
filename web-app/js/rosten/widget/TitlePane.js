/**
 * @author rosten
 */
define(["dojo/_base/declare", 
		"dijit/TitlePane",
		"dojo/dom-style"
		], 
		function(declare,TitlePane,domStyle) {
    return declare("rosten.widget.TitlePane",TitlePane, {
    	
        moreText:"more",
		moreUrl:"",
        titleCount:"",
        showTitleCount:true,
		
        width: "",
        height: "",
        marginBottom: null,
								
		templateString: '<div class="dijitTitlePane">' +
						'    <div class="dijitTitlePaneTitle" data-dojo-attach-point="titleBarNode" style="cursor: default;padding-top:5px">' +
						        '<span dojoAttachEvent="onclick:toggle,onkeypress: _onTitleKey" tabindex="0"' +
						                'role="button" data-dojo-attach-point="focusNode,arrowNode" style="cursor: pointer;">' +
									'<img src="${_blankGif}" alt="" class="dijitArrowNode" role="presentation"/>' +
									'<span data-dojo-attach-point="arrowNodeInner" class="dijitArrowNodeInner"></span>' +
								 '</span>' +
						        '<span data-dojo-attach-point="titleNode" class="dijitTitlePaneTextNode"></span>' +
						        '<span data-dojo-attach-point="titleCountNode" class="dijitTitlePaneCountNode"></span>' +
								'<span style="float:right;cursor:pointer;" data-dojo-attach-point="moreNode" data-dojo-attach-event="onclick:_moreClick"> </span>' + 
						    '</div>' +
						    '<div class="dijitTitlePaneContentOuter" data-dojo-attach-point="hideNode">' +
						        '<div class="dijitReset" data-dojo-attach-point="wipeNode">' +
						            '<div class="dijitTitlePaneContentInner" data-dojo-attach-point="containerNode" role="region" tabindex="-1">' +
						            '</div>' +
						        '</div>' +
						    '</div>' +
						'</div>',
    	
    	postCreate: function(){
            rosten.widget.TitlePane.superclass.postCreate.apply(this, arguments);
            if(this.marginBottom!=null){
	            domStyle.set(this.domNode, {
	                "marginBottom": this.marginBottom
	            });
            }
			if(!this.toggleable){
				domStyle.set(this.arrowNode,"display","none");
			}
			if (this.width != "") {
                domStyle.set(this.domNode, {
                    "width": this.width
                });
            }
			if (this.height != "") {
                domStyle.set(this.containerNode, {
                    "height": this.height
                });
            }
			if(this.moreNode!=""){
				this.moreNode.innerHTML = this.moreText;
			}
			if(this.titleCount!=""){
				this.titleCountNode.innerHTML = this.titleCount;
			}
			if(!this.showTitleCount){
				domStyle.set(this.titleCountNode,"display","none");
			}
        },
    	changeWidth: function(){
            domStyle.set(this.domNode, {
                "width": this.width
            });
        },
		changeHeight: function(){
            domStyle.set(this.containerNode, {
                "height": this.height
            });
        },
        changeTitleCount:function(args){
        	this.titleCount = args;
        	this.titleCountNode.innerHTML = args;
        },
		_moreClick:function(){
			if(this.moreUrl!="" && this.moreNode!=""){
				window.open(this.moreUrl,"_blank");
			}
			
		}
    	
    });
});
