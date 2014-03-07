/**
 * @author rosten
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	sendFile_formatTitle = function(value,rowIndex){
		return "<a href=\"javascript:sendFile_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	}
	sendFile_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("sendfile", rosten.webPath + "/sendFile/sendFileShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	sendFileLabel_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:sendFileLabel_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	sendFileLabel_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("sendfileLabel", rosten.webPath + "/sendFile/sendFileLabelShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	delete_sendFileLabel = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/sendFile/sendFileLabelDelete", content,rosten.deleteCallback);
		};
	};
	add_sendFileLabel = function() {
        var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
//        rosten.kernel.setHref(rosten.webPath +"/sendFile/sendFileLabelAdd?companyId=" + companyId + "&userid=" + userid,"addSendFile");
        rosten.openNewWindow("sendfileLabel", rosten.webPath + "/sendFile/sendFileLabelAdd?companyId=" + companyId + "&userid=" + userid);
    };
	
	returnToView = function(e){
		var actionBar = registry.getEnclosingWidget(e.target).getParent().getParent();
		switch(rosten.kernel.navigationEntity){
		case "addSendFile":
			show_sendFileNaviEntity("mySendfileManage");
			break;
		case "addSendFileLabel":
			show_sendFileNaviEntity("sendfileLabelManage");
			break;
		
		}
		
	};
	
	add_sendFile = function() {
        var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
//        rosten.kernel.setHref(rosten.webPath +"/sendFile/sendFileAdd?companyId=" + companyId + "&userid=" + userid,"addSendFile");
        rosten.openNewWindow("sendfile", rosten.webPath + "/sendFile/sendFileAdd?companyId=" + companyId + "&userid=" + userid);
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
		case "sendFileConfigManage":
			rosten.kernel.setHref(rosten.webPath + "/sendFile/sendFileConfigView", oString);
            break;
		case "sendfileLabelManage":
			var naviJson = {
				identifier : oString,
				actionBarSrc : rosten.webPath + "/sendFileAction/sendFileLabelView",
				gridSrc : rosten.webPath + "/sendFile/sendFileLabelGrid?companyId=" + companyId + "&userId=" + userid
			};
			rosten.kernel.addRightContent(naviJson);
			break;
		case "mySendfileManage":
			var naviJson = {
				identifier : oString,
				actionBarSrc : rosten.webPath + "/sendFileAction/sendFileView",
				gridSrc : rosten.webPath + "/sendFile/sendFileGrid?companyId=" + companyId + "&userId=" + userid+ "&type=person"
			};
			rosten.kernel.addRightContent(naviJson);
			break;
		case "allSendFileManage":
			var naviJson = {
				identifier : oString,
				actionBarSrc : rosten.webPath + "/sendFileAction/allSendFileView",
				gridSrc : rosten.webPath + "/sendFile/sendFileGrid?companyId=" + companyId + "&userId=" + userid + "&type=all"
			};
			rosten.kernel.addRightContent(naviJson);
			break;
		}
	}
	connect.connect("show_naviEntity", show_sendFileNaviEntity);
});
