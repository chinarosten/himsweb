/**
 * @author rosten
 */
define(["dojo/_base/declare",
		"dojo/_base/lang",
		"dojo/_base/xhr",
		"dijit/_WidgetBase", 
		"dijit/_TemplatedMixin",
		"dojox/collections/SortedList", 
		"rosten/uitl/General"], function(declare,lang, xhr,_WidgetBase,_TemplatedMixin,SortedList,General) {
	return declare("rosten.widget.SimpleNavigation", [_WidgetBase, _TemplatedMixin], {
		widgetsInTemplate: true, //解析RostenGrid.html中的dojoType等dojo特有信息，false不会解析
		templateString: dojo.cache("rosten.widget", "templates/RostenGrid.html"),
		
		
		
		
		
		
		
		
	});
});
