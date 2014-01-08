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
		"dojo/_base/lang",
		"dojox/uuid/generateRandomUuid"
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
			lang,
			RandomUuid
		) {
	rosten.variable.paneId = 1;
	mail_showInbox = function(name,id){
	    var mail_box = registry.byId("mail_inbox");
	    if(mail_box){
	        
	        mail_box.attr("title",name);
	        mail_actionBar.refresh(rosten.webPath + "/mailAction/inbox/" + id);
            mail_grid.refresh(rosten.webPath + "/mail/inboxGrid/" + id,{refreshHeader:false});
            
            mail_tabs.selectChild(mail_box); 
               
	    }else{
	        rosten.kernel.setHref(rosten.webPath + "/mail/mailBox", name,genIndex);
	    }
	};
    read_mail = function(){
    	var unid = rosten.getGridUnid("single");
    	if (unid == "")
            return;
    	var store = mail_grid.getStore();
    	var item = store.fetchItemByIdentity(unid);
    	onMessageOpen(store.getValue(item, "rowIndex"));	
    };
    delete_mail = function(){
    	var unids = rosten.getGridUnid("multi");
        if (unids == "")
            return;
        var content = {};
        content.id = unids;
        rosten.read(rosten.webPath + "/mail/mail_delete", content, function(data){
        	if (data.result == "true" || data.result == true) {
                rosten.alert("成功移除到已删除文件夹!");
                rosten.kernel.refreshGrid();
            } else {
                rosten.alert("删除失败!");
            }
        });
    };
    write_mail =function(){
        var newMessage = new mail.NewMessage({id: RandomUuid()});
        var newTab = newMessage.container;
        lang.mixin(newTab,
            {
                title: "写邮件",
                closable: true,
                onClose: function(){return mail_tabs.selectChild(registry.byId("mail_inbox"));}
            }
        );
        connect.connect(newMessage.sendButton,"onClick",function(){
            if(newMessage.to.attr("value")==""){
                rosten.alert("请正确填写收件人！");
                return;
            }
            if(newMessage.subject.attr("value")==""){
                rosten.alert("请正确填写主题！");
                return;
            }
            showSendBar();
            var content = {};
            content.to = newMessage.to.attr("value");
            content.subject = newMessage.subject.attr("value");
            content.content = newMessage.content.attr("value");
            rosten.read(rosten.webPath + "/mail/mail_save", content, function(data){
                stopSendBar();
                if (data.result == "true" || data.result == true) {
                    rosten.alert("发送成功!");
                    mail_tabs.closeChild(mail_tabs.selectedChildWidget);
                } else if(data.result == "noUser"){
                    rosten.alert("收件人不才存在，请检查！");
                } else {
                    rosten.alert("发送失败!");
                }
            },function(data){
                stopSendBar();
                rosten.alert("服务器出错！");
            });
        });
        mail_tabs.addChild(newTab);
        mail_tabs.selectChild(newTab);
    };
    formatSubject = function(value, rowIndex) {
		return "<a href=\"javascript:onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	onMessageOpen = function(rowIndex){
        var item = mail_grid.getGrid().getItem(rowIndex);
        var store = mail_grid.getStore();
        var id = store.getValue(item, "id");
        var _message = registry.byId(id);
        if(_message){
        	mail_tabs.selectChild(_message.container);
        	return;
        }
        sender = store.getValue(item, "sender"),
        subject = store.getValue(item, "subject"),
        sent = dateLocale.format(
                dateStamp.fromISOString(store.getValue(item, "sent")),
                {formatLength: "long", selector: "date"}),
        text = store.getValue(item, "text");
        
        _message = new mail.NewMessage({id: id });
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
        mail_tabs.addChild(newTab);
        mail_tabs.selectChild(newTab);
        _message.msgId.attr("value",id);
        _message.to.attr("value",sender);
        _message.subject.attr("value",subject);
        _message.content.attr("value",text);
            
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
			connect.connect(span, "onclick", func || function(){ contactTable.setQuery({first: c+"*"}, {ignoreCase: true}); });
			connect.connect(span, "onclick", function(){
				query(">", ci).removeClass("contactIndexSelected");
				domClass.add(span, "contactIndexSelected");
			});
		}
	
		addChar(ci,"ALL", function(){contactTable.setQuery({});}, 'contactIndexAll' );
		for(var l = "A".charCodeAt(0); l <= "Z".charCodeAt(0); l++){
			addChar(ci,String.fromCharCode(l));
		}
		addChar(ci,"ALL", function(){contactTable.setQuery({});}, 'contactIndexAll' );
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
	
	showSendBar = function(){
		registry.byId('fakeSend').update({ indeterminate: true });
		registry.byId('sendDialog').show();
		// setTimeout(function(){stopSendBar();}, 3000);
	};
	stopSendBar = function(){
		registry.byId('fakeSend').update({ indeterminate: false });
		registry.byId('sendDialog').hide();
		// mail_tabs.selectedChildWidget.onClose = function(){return true;};  // don't want confirm message
		// mail_tabs.closeChild(mail_tabs.selectedChildWidget);
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