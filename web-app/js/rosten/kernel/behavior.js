/**
 * @author rosten
 */
define([
	"dojo/_base/kernel", 
	"dojo/dom-style", 
	"dojo/dom-class",
	"dojo/_base/lang", 
	"dijit/registry",
	"rosten/kernel/_kernel",
	"rosten/widget/ConfirmDialog", 
	"rosten/widget/AlertDialog", 
	"rosten/widget/ActionBar",
	"rosten/widget/RostenDialog",
	"rosten/widget/ShowDialog"], function(kernel, domStyle, domClass , lang,registry,_kernel,ConfirmDialog, AlertDialog, ActionBar, RostenDialog,ShowDialog) {
	
	var behavior = {};
	
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
                title : "Rosten_系统对话框",
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
                title : "Rosten_系统对话框",
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
    lang.mixin(_kernel,behavior);
    return behavior;
});
