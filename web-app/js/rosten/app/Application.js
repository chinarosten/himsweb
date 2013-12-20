/**
 * @author rosten
 */
define(["dojo/_base/lang",
		"dijit/registry",
		"rosten/widget/MultiSelectDialog",
		"rosten/kernel/_kernel"], function(lang,registry,MultiSelectDialog) {
			
	var application = {};
    application.cssinitcommon = function() {
        //此功能只添加css文件
        var _rosten = window.opener.rosten;
        var dojocss = _rosten.dojothemecss;
        var rostencss = _rosten.rostenthemecss;

        rosten.addDojoThemeCss(dojocss);
        rosten.addRostenCss(rostencss);
    };
    application.cssinit = function() {
        var _rosten = window.opener.rosten;
        var dojocss = _rosten.dojothemecss;
        var rostencss = _rosten.rostenthemecss;

        rosten.replaceDojoTheme(dojocss, false);
        rosten.replaceRostenTheme(rostencss);
    };
    /*
     * 关闭当前窗口，并刷新父文档视图
     */
    application.pagequit = function() {
        window.opener.rosten.kernel.refreshGrid();
        window.close();
    };
    application.selectDialog = function(dialogTitle,id,url,flag,defaultValue,reload){
		/*
		 * dialogTitle:dialog中的titile
		 * id:dialog的id号需唯一
		 * url:url
		 * flag：是否多选，true为多选，默认为false
		 * reload:是否重新载入
		 * defaultValue：对话框中显示的值,为[]数组
		 */
		if (!(rosten[id] && registry.byId(id))) {
			rosten[id] = new MultiSelectDialog({
				title:dialogTitle,
		        id: id,
				single:!flag,
				datasrc:url
			});
			if(defaultValue!=undefined){
				rosten[id].defaultvalues = defaultValue;
			}
			rosten[id].open();
		}else{
			rosten[id].single = !flag;
			if(defaultValue!=undefined){
				rosten[id].defaultvalues = defaultValue;
			}
			rosten[id].open();
			if(reload!=undefined && reload==true){
				rosten[id].datasrc = url;
				rosten[id].refresh();
			}else{
				rosten[id].simpleRefresh();
			}
		}
		
	};
    
    
    lang.mixin(rosten,application);
    
    return application;
});
