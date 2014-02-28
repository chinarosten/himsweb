/**
 * @author rosten
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	add_sendFileLabel = function() {
        var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.kernel.setHref(rosten.webPath +"/sendFile/sendFileLabelAdd?companyId=" + companyId + "&userid=" + userid,"addSendFile");
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
	sendfile_add = function(){
		
	};
	sendfile_submit = function(){
		
	};
	sendFile_addWord = function(){
		if(kernel.isIE){
			rosten.openNewWindow("sendFile_addWord", rosten.webPath + "/sendFile/addWord");
		}else{
			rosten.alert("当前版本正文只支持IE浏览器！");
		}
	};
	add_sendFile = function() {
        var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.kernel.setHref(rosten.webPath +"/sendFile/sendFileAdd?companyId=" + companyId + "&userid=" + userid,"addSendFile");
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
				gridSrc : rosten.webPath + "/sendFile/sendFileGrid?companyId=" + companyId + "&userId=" + userid
			};
			rosten.kernel.addRightContent(naviJson);
			break;
		}
	}
	connect.connect("show_naviEntity", show_sendFileNaviEntity);
});
