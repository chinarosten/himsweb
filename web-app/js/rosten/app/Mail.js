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
	mail_showInbox = function(name,id){
		rosten.variable.mailNavigation = id;
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
   onMessageClick = function(cell){
        var store = mail_grid.getStore();
        var item = cell.grid.getItem(cell.rowIndex),
        sender = store.getValue(item, "sender"),
        subject = store.getValue(item, "subject"),
        sent = store.getValue(item, "sent"),
        text = store.getValue(item, "content"),
        messageInner = "<span class='messageHeader'>发送人: " + sender + "<br>" +
        "主题: "+ subject + "<br>" +
        "日期: " + sent + "<br><br></span>" + text;
        registry.byId("mail_message").setContent(messageInner);
    };

    read_mail = function(){
    	var unid = rosten._getGridUnid(mail_grid,"single");
    	if (unid == "")
            return;
    	var store = mail_grid.getStore();
    	var item = store.fetchItemByIdentity(unid);
    	onMessageOpen(store.getValue(item, "rowIndex"));	
    };
    delete_mail = function(){
        var unids = rosten._getGridUnid(mail_grid,"multi");
        if (unids == "")
            return;
        var content = {};
        content.id = unids;
        rosten.read(rosten.webPath + "/mail/mail_delete", content, function(data){
            if (data.result == "true" || data.result == true) {
                rosten.alert("成功移除到已删除文件夹!");
                mail_grid.refresh();
            } else {
                rosten.alert("删除失败!");
            }
        });
    };
    delete_mailByForm = function(e){
    	var actionBar = registry.getEnclosingWidget(e.target).getParent().getParent();
    	console.log(actionBar);
    	var content = {};
        content.id = actionBar.targetId;
        rosten.read(rosten.webPath + "/mail/mail_delete", content, function(data){
            if (data.result == "true" || data.result == true) {
                rosten.alert("成功移除到已删除文件夹!");
                cancel_mail();
            } else {
                rosten.alert("删除失败!");
            }
        });
    };
    destroy_mail = function(){
        var unids = rosten._getGridUnid(mail_grid,"multi");
        if (unids == "")
            return;
        var content = {};
        content.id = unids;
        rosten.read(rosten.webPath + "/mail/mail_destroy", content, function(data){
            if (data.result == "true" || data.result == true) {
                rosten.alert("成功删除!");
                mail_grid.refresh();
            } else {
                rosten.alert("删除失败!");
            }
        });
    };
    write_mail =function(){
    	var randomUuid = RandomUuid();
        var newMessage = new mail.NewMessage({id: randomUuid});
        var newTab = newMessage.container;
        lang.mixin(newTab,
            {
                title: "写邮件",
                closable: true,
                onClose: function(){
                	return cancel_mail_close(newMessage);
                }
            }
        );
        lang.mixin(newMessage.actionBar,{targetId:randomUuid});
        connect.connect(newMessage.sendButton,"onClick",function(){
        	save_mail_common(newMessage,"send");
        });
        mail_tabs.addChild(newTab);
        mail_tabs.selectChild(newTab);
    };
    send_mail = function(e){
    	var actionBar = registry.getEnclosingWidget(e.target).getParent().getParent();
    	var messageNode = registry.byId(actionBar.targetId);
    	save_mail_common(messageNode,"send");
    	
    };
    save_mail = function(e){
    	var actionBar = registry.getEnclosingWidget(e.target).getParent().getParent();
    	var messageNode = registry.byId(actionBar.targetId);
    	save_mail_common(messageNode,"save");
    };
    save_mail_common = function(messageNode,type){
    	var typeName = "保存";
    	if(type=="send") typeName = "发送";
    	
    	if(messageNode.to.attr("value")==""){
            rosten.alert("请正确填写收件人！");
            return;
        }
        if(messageNode.subject.attr("value")==""){
            rosten.alert("请正确填写主题！");
            return;
        }
        showSendBar();
        var content = {};
        content.id = messageNode.attr("id");
        content.to = messageNode.to.attr("value");
        content.subject = messageNode.subject.attr("value");
        content.content = messageNode.content.get("value");
        rosten.read(rosten.webPath + "/mail/mail_" + type, content, function(data){
            stopSendBar();
            if (data.result == "true" || data.result == true) {
                rosten.alert(typeName + "成功!");
                if(type == "send"){
                	cancel_mail(messageNode);
                }
            } else if(data.result == "noUser"){
                rosten.alert("收件人不存在，请检查！");
            } else {
                rosten.alert(typeName + "失败!");
            }
        },function(data){
            stopSendBar();
            rosten.alert("服务器出错！");
        });
    };
    cancel_mail_close = function(messageNode){
    	mail_tabs.selectChild(registry.byId("mail_inbox"));
    	mail_grid.refresh();
    	messageNode.destroyRecursive();
    	return true;
    };
    cancel_mail = function(){
    	mail_tabs.closeChild(mail_tabs.selectedChildWidget);
    };
    receive_mail = function(){
        //回复
    };
    repeat_mail = function(){
        //转发
    
    };
    resend_mail = function(){
        //再次编辑发送
    };
    edit_mail = function(){
        
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
        var sender = store.getValue(item, "sender"),
        to = store.getValue(item, "to"),
        subject = store.getValue(item, "subject"),
        sent = store.getValue(item, "sent"),
        text = store.getValue(item, "content");
        
        _message = new mail.showMessage({id: id,mailnavigation:rosten.variable.mailNavigation });
        var newTab = _message.container;
        lang.mixin(newTab,
            {
                title: subject,
                closable: true,
                onClose: function(){
                	return cancel_mail_close(_message);
                }
            }
        );
        lang.mixin(_message.actionBar,{targetId:id});
        mail_tabs.addChild(newTab);
        mail_tabs.selectChild(newTab);
        
        _message.sender.innerHTML = sender;
        _message.to.innerHTML = to;
        _message.subject.innerHTML = subject;
        _message.sent.innerHTML = sent;
        _message.content.innerHTML = text;
            
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