/**
 * @author rosten
 */
define(["rosten/kernel/_kernel", "rosten/kernel/behavior"], function(_kernel,behavior) {

    rosten.cssinitcommon = function() {
        //此功能只添加css文件
        var _rosten = window.opener.rosten;
        var dojocss = _rosten.dojothemecss;
        var rostencss = _rosten.rostenthemecss;

        rosten.addDojoThemeCss(dojocss);
        rosten.addRostenCss(rostencss);
    };
    rosten.cssinit = function() {
        var _rosten = window.opener.rosten;
        var dojocss = _rosten.dojothemecss;
        var rostencss = _rosten.rostenthemecss;

        rosten.replaceDojoTheme(dojocss, false);
        rosten.replaceRostenTheme(rostencss);
    };
    /*
     * 关闭当前窗口，并刷新父文档视图
     */
    rosten.pagequit = function() {

        window.opener.rosten.kernel.refreshGrid();
        window.close();
    };
    page_quit = function(){
    	
    };
});
