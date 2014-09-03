/**
 * @author rosten
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	dsj_search = function(){
		var content = {};
		
		switch(rosten.kernel.navigationEntity) {
		default:
			var serialNo = registry.byId("s_serialno");
			if(serialNo.get("value")!=""){
				content.serialNo = serialNo.get("value");
			}
			
			var subject = registry.byId("s_subject");
			if(subject.get("value")!=""){
				content.subject = subject.get("value");
			}
			
			var status = registry.byId("s_status");
			if(status.get("value")!=""){
				content.status = status.get("value");
			}
			break;
		}
		rosten.kernel.refreshGrid(rosten.kernel.getGrid().defaultUrl, content);
	};
	dsj_resetSearch = function(){
		switch(rosten.kernel.navigationEntity) {
		default:
			registry.byId("s_serialno").set("value","");
			registry.byId("s_subject").set("value","");
			registry.byId("s_status").set("value","");
			break;
		}	
		
		rosten.kernel.refreshGrid();
	};
	dsj_changeStatus = function(){
			
			
	};
	dsj_changeUser = function(){
			
	};
	dsj_formatTitle = function(value,rowIndex){
		return "<a href=\"javascript:dsj_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	}
	dsj_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("dsj", rosten.webPath + "/dsj/dsjShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	add_dsj = function() {
        var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("dsj", rosten.webPath + "/dsj/dsjAdd?companyId=" + companyId + "&userid=" + userid);
    };
	change_dsj = function() {
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var unid = rosten.getGridUnid("single");
		if (unid == "")
			return;
		rosten.openNewWindow("dsj", rosten.webPath + "/dsj/dsjShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
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
			rosten.readNoTime(rosten.webPath + "/dsj/dsjDelete", content,rosten.deleteCallback);
		};
	};
    
	/*
	 * 此功能默认必须存在
	 */
	show_dsjNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		
		switch (oString) {
		case "dsjConfigManage":
			rosten.kernel.setHref(rosten.webPath + "/dsj/dsjConfig", oString);
            break;
		case "myDsjManage":
			var naviJson = {
				identifier : oString,
				actionBarSrc : rosten.webPath + "/dsjAction/dsjView",
				searchSrc:rosten.webPath + "/dsj/searchView",
				gridSrc : rosten.webPath + "/dsj/dsjGrid?companyId=" + companyId + "&userId=" + userid + "&type=person"
			};
			rosten.kernel.addRightContent(naviJson);
			break;
		case "allDsjManage":
			var naviJson = {
				identifier : oString,
				actionBarSrc : rosten.webPath + "/dsjAction/allDsjView?userId=" + userid,
				searchSrc:rosten.webPath + "/dsj/searchView",
				gridSrc : rosten.webPath + "/dsj/dsjGrid?companyId=" + companyId + "&userId=" + userid + "&type=all"
			};
			rosten.kernel.addRightContent(naviJson);
			break;
		}
		
	}
	connect.connect("show_naviEntity", show_dsjNaviEntity);
});
