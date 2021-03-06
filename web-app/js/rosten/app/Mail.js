/**
 * @author rosten
 */
define(["dojo/_base/kernel",
		"dojo/_base/connect",
		"dojo/dom",
		"dojo/_base/xhr",
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
		"dojox/uuid/generateRandomUuid",
		"rosten/util/general"
		], function(
			kernel,
			connect,
			dom, 
			xhr,
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
			RandomUuid,
			General
		) {
	
	var general = new General();
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
                registry.byId("mail_message").attr("content","");
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
                registry.byId("mail_message").attr("content","");
            } else {
                rosten.alert("删除失败!");
            }
        });
    };
    receive_mailFromWeb = function(){
        rosten.alert("接收外网邮件将花费一段时间按，请耐心等待...").queryDlgClose = function(){
            var userid = rosten.kernel.getUserInforByKey("idnumber");
            var companyId = rosten.kernel.getUserInforByKey("companyid");
            showSendBar();
            rosten.readNoTime(rosten.webPath + "/mail/receiveMail", {userId:userid,companyId:companyId}, function(data){
                stopSendBar();
                if (data.result == "true" || data.result == true) {
                    rosten.alert("收信成功!");
                    mail_grid.refresh();
                }else if(data.result=="noTurnOn"){
                    rosten.alert("您尚未开通外网邮箱!");
                }else {
                    rosten.alert("收信失败!");
                }
            });
        };
    };
    write_mail =function(){
        var randomUuid = RandomUuid();
    	var departid = general.stringTrim(randomUuid,"-");
        var newMessage = new mail.NewMessage({id: randomUuid,departid:departid});
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
        newMessage.to.focus();
        rosten.variable.mailTargetNode = newMessage.to;
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
        
        //增加对附件的添加功能
        if(messageNode.attachFile.hasChildNodes()){
        	content.files = "";
        	var fileNodes = messageNode.attachFile.getElementsByTagName("a");
        	for(var i = 0; i < fileNodes.length; i ++){
        		if(content.files==""){
        			content.files = fileNodes[i].getAttribute("dealId");
        		}else{
        			content.files = content.files + "," + fileNodes[i].getAttribute("dealId");
        		}
        	}
        }
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
    	registry.byId("mail_message").attr("content","");
    	messageNode.destroyRecursive();
    	return true;
    };
    cancel_mail = function(){
    	mail_tabs.closeChild(mail_tabs.selectedChildWidget);
    };
    _getMailBodyInfo = function(mailid,node,str,number){
        var ioArgs = {
            url : rosten.webPath + "/mail/getMailBody/" + mailid,
            sync : true,
            handleAs : "text",
            preventCache : true,
            encoding : "utf-8",
            load : function(data) {
                if(number){
                    var _blank = "";
                    for(var i = 0; i < number; i ++){
                        _blank += "<br>"; 
                    }
                    node.attr("value",_blank + str + data);
                }else{
                    node.attr("value","<br>" + str + data);
                }
            },
            error:function(response, args){
            }
        };
        xhr.get(ioArgs);
    };
    receive_mail = function(e){
        //回复
    	var actionBar = registry.getEnclosingWidget(e.target).getParent().getParent();
    	var id = actionBar.targetId;
    	var messageNode = registry.byId(id);
    	
    	var sender = messageNode.sender.innerHTML,
    		to = messageNode.to.innerHTML,
    		subject = messageNode.subject.innerHTML,
    		sent = messageNode.sent.innerHTML,
    		// content = messageNode.content.innerHTML,
//    		files = messageNode.fileNode.innerHTML;
    		files = messageNode.fileNodeStr.innerHTML;
    	
    	//新增内容
    	var randomUuid = RandomUuid();
        var departid = general.stringTrim(randomUuid,"-");
    	var _Message = new mail.NewMessage({id: randomUuid,departid:departid});
    	var newTab = _Message.container;
    	
    	lang.mixin(newTab,
            {
                title: "回复：" + subject,
                closable: true,
                onClose: function(){
                	return cancel_mail_close(_Message);
                }
            }
        );
    	lang.mixin(_Message.actionBar,{targetId:randomUuid});
    	connect.connect(_Message.sendButton,"onClick",function(){
        	save_mail_common(_Message,"send");
        });
    	
    	_Message.to.attr("value",sender);
//    	_Message.attachFile.innerHTML = files;
    	_attachFileDeal(_Message.attachFile,files,true);
    	
    	_Message.subject.attr("value","回复：" + subject);
    	var addContent = "<hr noshade size=\"1\">在" + sent + ", \" " + sender + " \"写道：<br>" ;
    	
    	_getMailBodyInfo(id,_Message.content,addContent,6);
    	
    	// _Message.content.attr("value","<br><br><br><br><br><br>" + addContent + content);
    	
    	mail_tabs.addChild(newTab);
    	mail_tabs.selectChild(newTab);
    	
    	_Message.to.focus();
        rosten.variable.mailTargetNode = _Message.to;
    };
    repeat_mail = function(e){
        //转发
    	var actionBar = registry.getEnclosingWidget(e.target).getParent().getParent();
    	var id = actionBar.targetId;
    	var messageNode = registry.byId(id);
    	
    	var sender = messageNode.sender.innerHTML,
    		to = messageNode.to.innerHTML,
    		subject = messageNode.subject.innerHTML,
    		sent = messageNode.sent.innerHTML,
    		// content = messageNode.content.innerHTML,
//    		files = messageNode.fileNode.innerHTML;
    		files = messageNode.fileNodeStr.innerHTML;
    	
    	//新增内容
    	var randomUuid = RandomUuid();
        var departid = general.stringTrim(randomUuid,"-");
    	var _Message = new mail.NewMessage({id: randomUuid,departid:departid});
    	var newTab = _Message.container;
    	
    	lang.mixin(newTab,
            {
                title: "转发：" + subject,
                closable: true,
                onClose: function(){
                	return cancel_mail_close(_Message);
                }
            }
        );
    	lang.mixin(_Message.actionBar,{targetId:randomUuid});
    	connect.connect(_Message.sendButton,"onClick",function(){
        	save_mail_common(_Message,"send");
        });
    	
    	_Message.subject.attr("value","转发：" + subject);
//    	_Message.attachFile.innerHTML = files;
    	_attachFileDeal(_Message.attachFile,files,true);
    	
    	var addContent = "<hr noshade size=\"1\">在" + sent + ", \" " + sender + " \"写道：<br>" ;
    	// _Message.content.attr("value","<br><br><br><br><br><br>" + addContent + content);
    	_getMailBodyInfo(id,_Message.content,addContent,6);
    	
    	mail_tabs.addChild(newTab);
    	mail_tabs.selectChild(newTab);
    	
    	_Message.to.focus();
        rosten.variable.mailTargetNode = _Message.to;
    };
    edit_mail = function(e){
    	var actionBar = registry.getEnclosingWidget(e.target).getParent().getParent();
    	var id = actionBar.targetId;
    	var messageNode = registry.byId(id);
    	
    	var sender = messageNode.sender.innerHTML,
    		to = messageNode.to.innerHTML,
    		subject = messageNode.subject.innerHTML,
    		sent = messageNode.sent.innerHTML,
    		// content = messageNode.content.innerHTML,
    		files = messageNode.fileNodeStr.innerHTML;
    	
    	//摧毁当前内容
    	mail_tabs.removeChild(mail_tabs.selectedChildWidget);
    	messageNode.destroyRecursive();
    	
    	//新增内容
        var departid = general.stringTrim(id,"-");
    	var _Message = new mail.NewMessage({id: id,departid:departid});
    	var newTab = _Message.container;
    	
    	lang.mixin(newTab,
            {
                title: subject,
                closable: true,
                onClose: function(){
                	return cancel_mail_close(_Message);
                }
            }
        );
    	lang.mixin(_Message.actionBar,{targetId:id});
    	connect.connect(_Message.sendButton,"onClick",function(){
        	save_mail_common(_Message,"send");
        });
    	
    	_Message.to.attr("value",to);
    	_Message.subject.attr("value",subject);
    	// _Message.content.attr("value",content);
    	_getMailBodyInfo(id,_Message.content,"");
    	
    	//_Message.attachFile.innerHTML = files;
    	
    	_attachFileDeal(_Message.attachFile,files,true);
    	
    	mail_tabs.addChild(newTab);
    	mail_tabs.selectChild(newTab);
    	
    	_Message.to.focus();
        rosten.variable.mailTargetNode = _Message.to;
    };
    _attachFileDeal = function(node,fileStr,isShow){
    	if(fileStr && fileStr!=""){
        	var filesArray = general.splitString(fileStr,",");
        	for (var i = 0; i < filesArray.length; i++) {
        		var jsonObj = {fileId:general.stringLeft(filesArray[i],"&"),fileName:general.stringRight(filesArray[i],";")};
        		console.log(jsonObj);
        		mail_addAttachShow(node,jsonObj,isShow);
        	}
        }
    };
    formatSubject = function(value, rowIndex) {
		return "<a href=\"javascript:onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	formatEmailStatus = function(value){
		if(value && value!=""){
			var imgs = general.splitString(value,",");
			var _values="";
			for(var i = 0; i < imgs.length; i ++){
				if(_values==""){
					_values = "<img style=\"margin-left:4px\" src=\"" + rosten.webPath + "/" + imgs[i] + "\" />";
				}else{
					_values += "<img style=\"margin-left:4px\" src=\"" + rosten.webPath + "/" + imgs[i] + "\" />";
				}
			}
			return _values;
		}else{
			return "";
		}
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
        text = store.getValue(item, "content"),
        attachList = store.getValue(item, "attachList");
        
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
        _message.fileNodeStr.innerHTML = attachList;
        
        if(attachList && attachList!=""){
        	domStyle.set(_message.fileNodeTr,"display","");
        	var attachListArray = general.splitString(attachList,",");
        	for (var i = 0; i < attachListArray.length; i++) {
        		var jsonObj = {fileId:general.stringLeft(attachListArray[i],"&"),fileName:general.stringRight(attachListArray[i],"&")};
        		mail_addAttachShow(_message.fileNode,jsonObj);
        	}
        }
        
        //标记邮件为已读状态
        rosten.read(rosten.webPath + "/mail/mail_changeEmailStatus", {id:id},function(){
        	//刷新是侯爷信息
        	showStartMail(rosten.kernel.getUserInforByKey("idnumber"));
        });
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
					properties: {fontSize: 2},
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
	mail_addDepart = function(item,store){
		var content = {};
		content.id = store.getValue(item, "id");
		rosten.read(rosten.webPath + "/mail/mail_getDepartChild", content, function(data){
			for (var i = 0; i < data.length; i++) {
				store.fetchItemByIdentity({
					identity:data[i].id,
					onItem: function(node){
						if(!node){
							var parent = {parent: item,attribute: 'children'};
							var newItem = {id: data[i].id,name: data[i].name}; 
							if(data[i].parentId){
							    newItem.parentId = data[i].parentId;
							}
							if(data[i].children){
                                newItem.children = data[i].children;
                            }
                            if(data[i].username){
                                newItem.username = data[i].username;
                            }
                            if(data[i].type){
                                newItem.type = data[i].type;
                            }
							store.newItem(newItem, parent);//给父节点添加子节点	
						}
					}	
				});
			}	
        });
	};
	mail_addAttachShow = function(node,jsonObj,isConnect){
		var ul;
		var uls = node.getElementsByTagName("ul")
		if(uls && uls.length>0){
			ul = uls[0]
		}else{
			ul = document.createElement("ul");
			node.appendChild(ul);
			
		}
		var li = document.createElement("li");
		ul.appendChild(li);
		
		var a = document.createElement("a");
		a.setAttribute("href", rosten.webPath + "/mail/downloadFile/" + jsonObj.fileId);
		a.setAttribute("class","xtname");
		a.setAttribute("dealId",jsonObj.fileId);
		a.innerHTML = jsonObj.fileName;
		li.appendChild(a);
		
		var span = document.createElement("span");
		span.setAttribute("class","xtclose");
		span.setAttribute("style","display:none");
		li.appendChild(span);
		
		if(isConnect){
			connect.connect(li,"onmouseover",function(){
				domStyle.set(span,"display","block");
	        });
			connect.connect(li,"onmouseout",function(){
				domStyle.set(span,"display","none");
	        });
			connect.connect(span,"onclick",function(){
				//删除附件以及页面显示信息
				rosten.readNoTime(rosten.webPath + "/mail/deleteAttach", {fileId:jsonObj.fileId}, function(data){
					if(data.result==true || data.result=="true"){
						ul.removeChild(li);
					}
				});
	        });
		}
		
	};
});