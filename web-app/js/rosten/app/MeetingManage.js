/**
 * @author rosten
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	
    
	/*
	 * 此功能默认必须存在
	 */
	show_meetingNaviEntity = function(oString) {
		
		
	}
	connect.connect("show_naviEntity", show_meetingNaviEntity);
});
