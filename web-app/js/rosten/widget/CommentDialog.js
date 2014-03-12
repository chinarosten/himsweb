/**
 * @author rosten
 */
define(["dojo/_base/declare",
		"dojo/dom-style",
		"dijit/form/SimpleTextarea",
		"rosten/widget/_Dialog"
		], 
		function(declare,domStyle,SimpleTextarea,_Dialog) {
	return declare("rosten.widget.CommentDialog", [_Dialog], {
		height:"180px",
		width:"500px",
		title: "填写意见",
		type:"",
		content:"",
		
        buildContent: function(node){
        	this.contentNode = new SimpleTextarea({
        		style:{height:"100px",width:"480px"},
        		placeHolder:"请输入少于500个字的意见",
        		maxLength:"1000",
        		trim:true
        	});
        	if(this.content!=""){
        		this.contentNode.set("value",this.content);
        	}
        	node.appendChild(this.contentNode.domNode);
        },
        refresh: function(obj){
	        this.contentPane.innerHTML = "";
	        if(obj.type){
	        	this.type = obj.type;
	        }
	        if(obj.content){
	        	this.content = obj.content;
	        }
            this.buildContent(this.contentPane);
        },
        getData: function(){
        	var data ={}
        	data.type = this.type;
        	data.content = this.contentNode.get("value");
            return data;
        }
        
    });
});
