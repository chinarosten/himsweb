/**
 * @author rosten
 * @created 2013-12-01
 */
define(["dojo/_base/declare", "dojo/_base/kernel", "dojo/dom-style", "rosten/widget/_Dialog"], function(declare, kernel, domStyle,_Dialog) {
	return declare("rosten.widget.AlertDialog", rosten.widget._Dialog, {
		height: "100px",
		width:"300px",
		title: "RostenDialog",
		mode:"CLOSE",
		message:"",
		msgNode:null,
		buildContent: function(node){
			var div = document.createElement("div");
			domStyle.set(div,{"height":"24px","fontSize":"12px"});
			node.appendChild(div);
			
			var imgNode = document.createElement("img");
			var imgSrc = kernel.moduleUrl("rosten", "widget/templates/alert_1.gif");
			if(dojo.isIE){
				imgNode.src = imgSrc;
			}else{
				imgNode.setAttribute("src", imgSrc);
			}
			domStyle.set(imgNode,{"float":"left"});
			div.appendChild(imgNode);
			
			this.msgNode = document.createElement("div");
			div.appendChild(this.msgNode);

			if(this.message!=""){
				this.msgNode.innerHTML = "&nbsp;&nbsp;" + this.message;
			}else{
				this.msgNode.innerHTML = "&nbsp;&nbsp;hello welcome!";
			}
		
		},
		refresh:function(oString){
			this.message = oString;
			this.msgNode.innerHTML = "&nbsp;&nbsp;" + oString;
		}
	});
});
