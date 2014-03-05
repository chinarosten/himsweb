/**
 * @author rosten
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	add_dsj = function() {
        var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("dsj", rosten.webPath + "/dsj/dsjAdd?companyId=" + companyId + "&userid=" + userid);
    };
	change_dsj = function() {
		var unid = rosten.getGridUnid("single");
		if (unid == "")
			return;
		rosten.openNewWindow("dsj", rosten.webPath + "/dsj/dsjShow/" + unid);
	};
	read_dsj = function() {
		change_dsj();
	};
	delete_dsj = function() {
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/dsj/dsjDelete", content,rosten.deleteCallback);
		};
	};
    
	/*
	 * 此功能默认必须存在
	 */
	show_dsjNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		
		switch (oString) {
		case "myDsjManage":
			var naviJson = {
				identifier : oString,
				actionBarSrc : rosten.webPath + "/dsjAction/dsjView",
				gridSrc : rosten.webPath + "/dsj/dsjGrid?companyId=" + companyId + "&userId=" + userid
			};
			rosten.kernel.addRightContent(naviJson);
			break;
		}
		
	}
	connect.connect("show_naviEntity", show_dsjNaviEntity);
});
