/**
 * @author rosten
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	meeting_changeStatus = function(){
			
			
	};
	meeting_changeUser = function(){
			
	};
	meeting_formatTitle =function(value,rowIndex){
		return "<a href=\"javascript:meeting_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	meeting_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("meeting", rosten.webPath + "/meeting/meetingShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	add_meeting = function() {
        var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("meeting", rosten.webPath + "/meeting/meetingAdd?companyId=" + companyId + "&userid=" + userid);
    };
	change_meeting = function() {
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var unid = rosten.getGridUnid("single");
		if (unid == "")
			return;
		rosten.openNewWindow("meeting", rosten.webPath + "/meeting/meetingShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
	};
	read_meeting = function() {
		change_meeting();
	};
	delete_meeting = function() {
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/meeting/meetingDelete", content,rosten.deleteCallback);
		};
	};
    
	/*
	 * 此功能默认必须存在
	 */
	show_meetingNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		
		switch (oString) {
		case "meetingConfigManage":
			rosten.kernel.setHref(rosten.webPath + "/meeting/meetingConfig", oString);
            break;
		case "myMeetingManage":
			var naviJson = {
				identifier : oString,
				actionBarSrc : rosten.webPath + "/meetingAction/meetingView",
				gridSrc : rosten.webPath + "/meeting/meetingGrid?companyId=" + companyId + "&userId=" + userid+ "&type=person"
			};
			rosten.kernel.addRightContent(naviJson);
			break;
		case "allMeetingManage":
			var naviJson = {
				identifier : oString,
				actionBarSrc : rosten.webPath + "/meetingAction/allMeetingView",
				gridSrc : rosten.webPath + "/meeting/meetingGrid?companyId=" + companyId + "&userId=" + userid + "&type=all"
			};
			rosten.kernel.addRightContent(naviJson);
			break;
		}
	}
	connect.connect("show_naviEntity", show_meetingNaviEntity);
});
