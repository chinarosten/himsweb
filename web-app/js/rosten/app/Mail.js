/**
 * @author rosten
 */
define(["dojo/_base/kernel",
		"dojo/_base/connect",
		"dojo/dom",
		"dojo/dom-style",
		"dojo/dom-class",
		"dojo/date/locale",
		"dojo/date/stamp",
		"dijit/Tooltip",
		"dijit/registry",
		"dojo/query",
		"dojox/widget/FisheyeLite",
		"dojo/fx/easing",
		"dojo/date/locale",
		"dojo/date/stamp",
		"dojo/_base/lang"
		], function(
			kernel,
			connect,
			dom, 
			domStyle,
			domClass,
			dateLocale,
			dateStamp,
			Tooltip,
			registry,
			query,
			FisheyeLite,
			easing,
			dateLocal,
			dateStamp,
			lang
		) {
	mail_showInbox = function(name,id){
	    var mail_box = registry.byId("mail_inbox");
	    if(mail_box){
            mail_table.setQuery({
                type: "message",
                folder: id
            });
            
            mail_box.attr("title",name);
            mail_tabs.selectChild(mail_box);    
	    }else{
	        rosten.kernel.setHref(rosten.webPath + "/mail/mailBox", name,genIndex);
	    }
	};
    read_mail = function(){
        
    };
    delete_mail = function(){
        
    };
	function genIndex(){
		// summary:
		//		generate A-Z push buttons for navigating contact list
		var ci = dom.byId("contactIndex");
		
		function addChar(node,c, func, cls){
			// add specified character, when clicked will execute func
			var span = document.createElement("span");
			span.innerHTML = c;
			span.className = cls || "contactIndex";
			node.appendChild(span);
			new FisheyeLite(
				{
					properties: {fontSize: 1.5},
					easeIn: easing.linear,
					durationIn: 100,
					easeOut: easing.linear,
					durationOut: 100
				},
				span
			);
			connect.connect(span, "onclick", func || function(){ contactTable.setQuery({first: c+"*"}, {ignoreCase: true}) });
			connect.connect(span, "onclick", function(){
				query(">", ci).removeClass("contactIndexSelected");
				domClass.add(span, "contactIndexSelected");
			});
		}
	
		addChar(ci,"ALL", function(){contactTable.setQuery({})}, 'contactIndexAll' );
		for(var l = "A".charCodeAt(0); l <= "Z".charCodeAt(0); l++){
			addChar(ci,String.fromCharCode(l))
		}
		addChar(ci,"ALL", function(){contactTable.setQuery({})}, 'contactIndexAll' );
	}
	onMessageClick = function(cell){
		// summary:
		// when user clicks a row in the message list pane
		var item = cell.grid.getItem(cell.rowIndex),
			sender = this.store.getValue(item, "sender"),
			subject = this.store.getValue(item, "label"),
			sent = dateLocale.format(
					dateStamp.fromISOString(this.store.getValue(item, "sent")),
					{formatLength: "long", selector: "date"}),
			text = this.store.getValue(item, "text"),
			messageInner = "<span class='messageHeader'>发送人: " + sender + "<br>" +
			"主题: "+ subject + "<br>" +
			"日期: " + sent + "<br><br></span>" +
			text;
		registry.byId("mail_message").setContent(messageInner);
	};
	onMessageDbClick = function(cell){
		var item = cell.grid.getItem(cell.rowIndex),
			sender = this.store.getValue(item, "sender"),
			subject = this.store.getValue(item, "label"),
			sent = dateLocale.format(
					dateStamp.fromISOString(this.store.getValue(item, "sent")),
					{formatLength: "long", selector: "date"}),
			text = this.store.getValue(item, "text");
			
			var _message = new mail.NewMessage({id: "new"+paneId  });
			var newTab = _message.container;
			lang.mixin(newTab,
				{
					title: subject,
					closable: true,
					onClose: function(){
						return mail_tabs.selectChild(registry.byId("mail_inbox"));
					}
				}
			);
			paneId++;
			mail_tabs.addChild(newTab);
			mail_tabs.selectChild(newTab);
			_message.to.attr("value",sender);
			_message.subject.attr("value",subject);
			_message.content.attr("value",text);
			
	};
	showSendBar = function(){
		registry.byId('fakeSend').update({ indeterminate: true });
		registry.byId('sendDialog').show();
		setTimeout(function(){stopSendBar();}, 3000);
	};
	stopSendBar = function(){
		registry.byId('fakeSend').update({ indeterminate: false });
		registry.byId('sendDialog').hide();
		mail_tabs.selectedChildWidget.onClose = function(){return true;};  // don't want confirm message
		mail_tabs.closeChild(mail_tabs.selectedChildWidget);
	};
	testClose = function(pane,tab){
		confirm("您确定离开?");
		return mail_tabs.selectChild(registry.byId("mail_inbox"));
	};
	searchMessages = function(){
		// summary:
		//		do a custom search for messages across inbox folders
		var query = {type: "message"};
		var searchCriteria = mail_searchForm.attr('value');
		for(var key in searchCriteria){
			var val = searchCriteria[key];
			if(val){
				query[key] = "*" + val + "*";
			}
			mail_table.setQuery(query, {ignoreCase: true});
			registry.byId("mail_inbox").attr("title","搜索邮件");
		}
		
	};
});