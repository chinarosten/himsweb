/**
 * @author rosten
 */
define([ "dojo/_base/connect", "dijit/registry", "rosten/kernel/behavior" ], function(
		connect, registry) {
	
	returnToView = function(){
		
	};
	sendFile_addWord = function(){
		
	};
	add_sendFile = function() {
        var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.kernel.setHref(rosten.webPath +"/sendFile/sendFileAdd?companyId=" + companyId + "&userid=" + userid);
    };
	change_sendFile = function() {
		var unid = rosten.getGridUnid("single");
		if (unid == "")
			return;
		rosten.openNewWindow("sendFile", rosten.webPath
				+ "/sendFile/sendFileShow/" + unid);
	};
	read_sendFile = function() {
		change_sendFile();
	};
	delete_sendFile = function() {
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/sendFile/sendFileDelete", content,
					rosten.deleteCallback);
		};
	};
    
	/*
	 * 此功能默认必须存在
	 */
	show_sendFileNaviEntity = function(oString) {
		
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		
		switch (oString) {
		case "sendfile":
			var naviJson = {
				identifier : oString,
				actionBarSrc : rosten.webPath + "/sendFileAction/sendFileView",
				gridSrc : rosten.webPath + "/sendFile/sendFileGrid?companyId=" + companyId + "&userId=" + userid
			};
			rosten.kernel.addRightContent(naviJson);
			break;
		}
	}
	connect.connect("show_naviEntity", show_sendFileNaviEntity);
});
