/**
 * @author rosten
 */
define([
	"dojo/_base/kernel", 
	"dojo/has",
	"dojo/dom-style", 
	"dojo/dom-class",
	"dojo/_base/lang", 
	"dijit/registry",
	"rosten/kernel/_kernel",
	"rosten/widget/ConfirmDialog", 
	"rosten/widget/AlertDialog", 
	"rosten/widget/ActionBar",
	"rosten/widget/RostenDialog",
	"rosten/widget/ShowDialog",
	"rosten/widget/CommentDialog"], function(kernel, has,domStyle, domClass , lang,registry,_kernel,ConfirmDialog, AlertDialog, ActionBar, RostenDialog,ShowDialog,CommentDialog) {
	
	var behavior = {};
	behavior.addAttachShow = function(node,jsonObj){
		var a = document.createElement("a");
		if (has("ie")) {
			a.href = rosten.webPath + "/system/downloadFile/" + jsonObj.fileId;
		}else{
			a.setAttribute("href", rosten.webPath + "/system/downloadFile/" + jsonObj.fileId);
		}
		a.setAttribute("style","margin-right:20px");
		a.setAttribute("dealId",jsonObj.fileId);
		a.innerHTML = jsonObj.fileName;
		node.appendChild(a);
	};
	behavior.addCommentDialog = function(obj){
		if (!_kernel.sys_commentDialog || !registry.byId("sys_commentDialog")) {
            _kernel.sys_commentDialog = new CommentDialog({
                title : "\u586b\u5199\u610f\u89c1",//填写意见
                id : "CommentDialog"
            });
        }
        _kernel.sys_commentDialog.queryDlgClose = function() {
        };
        _kernel.sys_commentDialog.open();

        _kernel.sys_commentDialog.refresh(obj);
        return _kernel.sys_commentDialog;
	};
	
    behavior.addRostenBar = function(parentNode, barUrl) {
        var barNode = kernel.create("div", null, parentNode, "first");
        domClass.add(barNode, "rosten_action");

        var actionBar = new ActionBar({
            id : "rosten_actionBar",
            actionBarSrc : barUrl
        });
        barNode.appendChild(actionBar.domNode);
    };
    behavior.confirm = function(oString) {
        if (!_kernel.sys_confirmDialog || !registry.byId("sys_confirmDialog")) {
            _kernel.sys_confirmDialog = new ConfirmDialog({
                title : "\u7cfb\u7edf\u5bf9\u8bdd\u6846",//系统对话框
                id : "sys_confirmDialog"
            });
        }
        _kernel.sys_confirmDialog.callback = function() {
        };
        _kernel.sys_confirmDialog.queryDlgClose = function() {
        };
        _kernel.sys_confirmDialog.open();

        _kernel.sys_confirmDialog.refresh(oString);
        return _kernel.sys_confirmDialog;
    };
    behavior.alert = function(oString) {
        if (!_kernel.sys_alertDialog || !registry.byId("sys_alertDialog")) {
            _kernel.sys_alertDialog = new AlertDialog({
                title : "\u7cfb\u7edf\u5bf9\u8bdd\u6846",//系统对话框
                id : "sys_alertDialog"
            });
        }
        _kernel.sys_alertDialog.queryDlgClose = function() {
        };
        _kernel.sys_alertDialog.open();
        _kernel.sys_alertDialog.refresh(oString);
        return _kernel.sys_alertDialog;
    };
	behavior.getStoreItem = function(store,args,callback){
		store.fetch({
			query:args,
			queryOptions: {
                deep: true
            },
			onComplete: callback
		});
	};
	behavior.toggleAction = function(obj,oBoolean){
		var widget = registry.getEnclosingWidget(obj);
		if(widget){
			widget.set("disabled",oBoolean);
		}
	};
    lang.mixin(_kernel,behavior);
    return behavior;
});
