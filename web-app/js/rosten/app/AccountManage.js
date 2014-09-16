/**
 * @author rosten
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	account_search = function(){
		var content = {};
		
		switch(rosten.kernel.navigationEntity) {
		default:
			var purpose = registry.byId("s_purpose");
			if(purpose.get("value")!=""){
				content.purpose = purpose.get("value");
			}
			
			var project = registry.byId("s_project");
			if(project.get("value")!=""){
				content.project = project.get("value");
			}
			
			var category = registry.byId("s_category");
			if(category.get("value")!=""){
				content.category = category.get("value");
			}
			break;
		}
		rosten.kernel.refreshGrid(rosten.kernel.getGrid().defaultUrl, content);
	};
	account_resetSearch = function(){
		switch(rosten.kernel.navigationEntity) {
		default:
			registry.byId("s_purpose").set("value","");
			registry.byId("s_project").set("value","");
			registry.byId("s_category").set("value","");
			break;
		}	
		
		rosten.kernel.refreshGrid();
	};
	
	account_formatTitle = function(value,rowIndex){
		return "<a href=\"javascript:account_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	}
	account_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("account", rosten.webPath + "/account/accountShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	add_account = function() {
        var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("account", rosten.webPath + "/account/accountAdd?companyId=" + companyId + "&userid=" + userid);
    };
	change_account = function() {
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var unid = rosten.getGridUnid("single");
		if (unid == "")
			return;
		rosten.openNewWindow("account", rosten.webPath + "/account/accountShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
	};
	read_account = function() {
		change_account();
	};
	delete_account = function() {
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.readNoTime(rosten.webPath + "/account/accountDelete", content,rosten.deleteCallback);
		};
	};
	
	category_formatTitle = function(value,rowIndex){
		return "<a href=\"javascript:category_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	}
	category_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("category", rosten.webPath + "/account/categoryShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	add_category = function() {
        var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("category", rosten.webPath + "/account/categoryAdd?companyId=" + companyId + "&userid=" + userid);
    };
	change_category = function() {
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var unid = rosten.getGridUnid("single");
		if (unid == "")
			return;
		rosten.openNewWindow("category", rosten.webPath + "/account/categoryShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
	};
	read_category = function() {
		change_category();
	};
	delete_category = function() {
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.readNoTime(rosten.webPath + "/account/categoryDelete", content,rosten.deleteCallback);
		};
	};
	
	project_formatTitle = function(value,rowIndex){
		return "<a href=\"javascript:project_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	}
	project_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("project", rosten.webPath + "/account/projectShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	add_project = function() {
        var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("project", rosten.webPath + "/account/projectAdd?companyId=" + companyId + "&userid=" + userid);
    };
	change_project = function() {
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var unid = rosten.getGridUnid("single");
		if (unid == "")
			return;
		rosten.openNewWindow("project", rosten.webPath + "/account/projectShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
	};
	read_project = function() {
		change_project();
	};
	delete_project = function() {
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.readNoTime(rosten.webPath + "/account/projectDelete", content,rosten.deleteCallback);
		};
	};
    
	/*
	 * 此功能默认必须存在
	 */
	show_accountNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		
		switch (oString) {
		case "projectManage":
			var naviJson = {
				identifier : oString,
				actionBarSrc : rosten.webPath + "/accountAction/projectView",
				gridSrc : rosten.webPath + "/account/projectGrid?companyId=" + companyId + "&userId=" + userid
			};
			rosten.kernel.addRightContent(naviJson);
			break;
		case "categoryManage":
			var naviJson = {
				identifier : oString,
				actionBarSrc : rosten.webPath + "/accountAction/categoryView",
				gridSrc : rosten.webPath + "/account/categoryGrid?companyId=" + companyId + "&userId=" + userid
			};
			rosten.kernel.addRightContent(naviJson);
			break;
		case "accountManage":
			var naviJson = {
				identifier : oString,
				actionBarSrc : rosten.webPath + "/accountAction/accountView?userId=" + userid,
				searchSrc:rosten.webPath + "/account/searchView?companyId=" + companyId,
				gridSrc : rosten.webPath + "/account/accountGrid?companyId=" + companyId + "&userId=" + userid
			};
			rosten.kernel.addRightContent(naviJson);
			break;
		}
		
	}
	connect.connect("show_naviEntity", show_accountNaviEntity);
});
