/**
 * @author rosten
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	formatterDownloadFile = function(value,rowIndex){
		return "<a href=\"" + rosten.webPath + "/system/downloadFile/" + value + "\">下载</a>";
	};
	add_downloadFile = function() {
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("downloadFile", rosten.webPath + "/downloadFile/downloadFileAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	change_downloadFile = function() {
		var unid = rosten.getGridUnid("single");
		if (unid == "")
			return;
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("downloadFile", rosten.webPath + "/downloadFile/downloadFileShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
	};
	read_downloadFile = function() {
		change_downloadFile();
	};
	delete_downloadFile = function() {
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/downloadFile/downloadFileDelete", content,rosten.deleteCallback);
		};
	};
	
	/*
	 * 此功能默认必须存在
	 */
	show_publicNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		
		switch (oString) {
		case "downloadFileManage":
            var naviJson = {
                identifier : oString,
                actionBarSrc : rosten.webPath + "/publiccAction/downloadFileView?userId=" + userid,
                gridSrc : rosten.webPath + "/publicc/downloadFileGrid?companyId=" + companyId
            };
            rosten.kernel.addRightContent(naviJson);

            var rostenGrid = rosten.kernel.getGrid();
            rostenGrid.onRowDblClick = change_downloadFile;
            break;
		}
		
	}
	connect.connect("show_naviEntity", show_publicNaviEntity);
});
