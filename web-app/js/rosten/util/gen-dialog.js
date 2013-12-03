/**
 * @author rosten
 */
define(["dojo/_base/declare",
		"dojo/_base/kernel", 
		"dojo/dom-style",
		"dojo/dom-attr",
		"dojo/_base/window",
		"dijit/registry",
		"dijit/Dialog",
		"rosten/widget/ShowDialog"], function(declare,kernel,domStyle,domAttr,win,registry,Dialog,ShowDialog) {
	return declare("rosten.util.gen-dialog", null, {
		createRostenShowDialog:function(src,args){
			var obj = {src:src};
			if(args){
				if(args.callback)obj.callback = args.callback;
				if(args.callbackargs) obj.callbackargs = args.callbackargs;
				if(args.onLoadFunction) obj.onLoadFunction = args.onLoadFunction;
			}
			if(this.rostenShowDialog) this.rostenShowDialog.destroy();
			this.rostenShowDialog = new ShowDialog(obj);
		},
		getRostenShowDialog:function(){
			if (this.rostenShowDialog){
				return this.rostenShowDialog;
			}
		},
		hideRostenShowDialog:function(){
			if (this.rostenShowDialog){
				console.log("showdialog start hide...");
				this.rostenShowDialog.hide();
				this.rostenShowDialog.destroy();
				console.log("showdialog hide is sucessful...");
			}
		},
		showWaitDialog_1: function(node,idname){
			var waitDialog = kernel.byId(idname);
			if(waitDialog){
				domStyle.set(waitDialog,"display","block");
			}else{
				var div = document.createElement("div");
				domAttr.set(div,"id",idname);
	            div.innerHTML = "<img align='middle' height='25px' src='jslib/rosten/src/share/wait_big.gif'>" +
	                "<span style='font-size:20px;color:#414141;vertical-align:middle;font-weight:bold;padding:15px 20px 10px 10px;margin:5px 5px'>请稍候,正在获取内容...</span>";
	            domStyle.set(div, {
	                "display": "block",
	                "vertical-align": "middle",
	                "height": "100%",
					"textAlign":"center"
	            });
	            node.appendChild(div);
			}
        },
        hideWaitDialog_1: function(idname){
           var waitDialog = kernel.byId(idname);
		   if(waitDialog){
		   	domStyle.set(waitDialog,"display","none");
		   }
        },
		showWaitDialog: function(waitDialogId){
            if (!waitDialogId) {
                var idName = "waitDialogId";
            }
            else {
                var idName = waitDialogId;
            }
            var dialog = registry.byId(idName);
            if (!dialog) {
                var div = document.createElement("div");
                var dialogContent = document.createElement("div");
                dialogContent.innerHTML = "<img align='middle' height='25px' src='jslib/rosten/src/share/wait_big.gif'>" +
                "<span style='font-size:20px;color:#414141;vertical-align:middle;font-weight:bold;padding:15px 20px 10px 10px;margin:5px 5px'>请稍候,正在获取内容...</span>";
                win.body().appendChild(div);
                
                dialog = new Dialog({
                    id: idName,
					autofocus:false
                }, div);
				domStyle.set(dialog.titleBar,"display","none");
                dialog.setContent(dialogContent);
                dialog.show();
            }
            else {
                dialog.show();
            }
        },
        hideWaitDialog: function(waitDialogId){
            if (!waitDialogId) {
                var idName = "waitDialogId";
            }
            else {
                var idName = waitDialogId;
            }
            var dialog = registry.byId(idName);
            if (dialog) {
                dialog.hide();
                console.log("hide the dialog......");
            }
        }
	});
});