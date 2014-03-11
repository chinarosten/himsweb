/**
 * @author rosten
 * @created 2013-12-01
 */
define(["dojo/_base/kernel"
		, "dojo/_base/lang"
		, "dijit/registry"
		, "dojo/dom"
		, "dojo/dom-style"
		, "dojo/dom-class"
		, "dojo/dom-construct"
		, "dojo/_base/connect"
		, "dojox/layout/ContentPane"
		,"rosten/kernel/kernel"
		,"rosten/util/general"
		, "rosten/app/Mail"
		, "rosten/kernel/behavior"], function(kernel, lang, registry, dom,domStyle,domClass,domConstruct,connect,ContentPane,rostenKernel,general) {
	var main = {};
	main._getGridUnid = function(rostenGrid,type){
		/*
		 * type:single ---单个
		 * type:multi ---多个
		 */
        var selectitems = rostenGrid.getSelected();
		
		if(selectitems.length<=0){
			rosten.alert("请先选择需要修改的条目！");
			return "";
		}
		var gridStore = rostenGrid.getStore();
		var item;
		var idArgs;
		if(type=="single"){
        	item = selectitems[0];
        	idArgs = gridStore.getValue(item, "id");
		}else if(type=="multi"){
			var unids = [];
			for(var i=0;i<selectitems.length;i++){
				item = selectitems[i];
				var _1 = gridStore.getValue(item, "id");
				unids.push(_1);
			}
			
			idArgs = new general().implodeArray(unids,",");
		}
       
		return idArgs;
	};
	main.getGridUnid = function(type){
		/*
		 * type:single ---单个
		 * type:multi ---多个
		 */
		var rostenGrid = rosten.kernel.getGrid();
		return main._getGridUnid(rostenGrid,type);
	};		
    initInstance = function(naviJson, data) {
        //载入缺省dojo的css样式
        if (data.cssStyle) {
            rosten.replaceDojoTheme(data.cssStyle, true);
        }
        //载入用户指定的css样式
        var rostencss;
        if (data.individuationcss) {
            rostencss = data.individuationcss;
        } else {
            rostencss = rosten.rostenthemecss;
        }
        rosten.replaceRostenTheme(rostencss);

        connect.subscribe("loadjsfile", null, function(oString) {
        	domStyle.set(registry.byId("home").domNode,"display","none");
    		domStyle.set(registry.byId("modelMain").domNode,"display","");
    		registry.byId("modelMain").resize();
    		
            /*
             * 用于加载对应的js文件,此方法在后续开发过程中需要修改
             */
            console.log("loadjs file is :" + oString);
            if (oString == "plat" || oString == "system") {
            	deleteMailNavigation();
             	require(["rosten/app/SystemManage"],function(){
             		if(oString=="plat"){
             			show_systemNaviEntity("companyManage");
             		}else{
             			show_systemNaviEntity("userManage");
             		}
             	});
            }else if (oString == "person") {
            	addMailNavigation();
            } else if (oString == "bbs") {
            	deleteMailNavigation();
            	require(["rosten/app/BbsManage"],function(){
            		if(rosten.variable.showStartBbs==undefined || rosten.variable.showStartBbs!=true){
            			show_bbsNaviEntity("mybbsManage");
            		}
            	});
            } else if (oString == "sendfile") {
            	deleteMailNavigation();
                require(["rosten/app/SendFileManage"],function(){
                	show_sendFileNaviEntity("mySendfileManage");
                });
            } else if (oString == "receivefile") {
            	deleteMailNavigation();
            	require(["rosten/app/ReceiveFileManage"],function(){
            		returnToMain();
            	});
            }else if(oString=="personconfig"){
                deleteMailNavigation();
                require(["rosten/app/SmsManage"],function(){
                	show_smsNaviEntity("gtaskManage");
                });
            }else if (oString == "meeting") {
            	deleteMailNavigation();
            	require(["rosten/app/MeetingManage"],function(){
            		show_meetingNaviEntity("myMeetingManage");
            	});
            }else if (oString == "dsj") {
            	deleteMailNavigation();
            	require(["rosten/app/DsjManage"],function(){
            		show_dsjNaviEntity("myDsjManage");
            	});
            }
        });
		
        connect.subscribe("loadspecmenu", null, function(oString) {
        	switch(oString){
        	case "home":
        		domStyle.set(registry.byId("home").domNode,"display","");
        		domStyle.set(registry.byId("modelMain").domNode,"display","none");
        		registry.byId("home").resize();
        		rosten.kernel.navigationMenu = "";
        		break;
        	case "sms":
        	    require(["rosten/app/SmsManage"],function(){
        	        rosten.kernel.createRostenShowDialog(rosten.webPath + "/system/smsAdd");
        	        
        	    });
        		break;
			case "question":
			     require(["rosten/app/QuestionManage"],function(){
                    rosten.kernel.createRostenShowDialog(rosten.webPath + "/system/questionAdd");
                    
                });
        		break;
        	}
        });
        
		rosten.kernel = new rostenKernel(naviJson);
		rosten.kernel.addUserInfo(data);
		
		//获取首页显示信息
		var userId = data["idnumber"];
		var companyId = data["companyid"];
		showStartInformation(userId,companyId);
		
        if (rosten.kernel.getMenuName() == "") {
            return;
        } else {
            returnToMain();
        }
        //增加时获取后台session功能
        //setInterval("session_checkTimeOut()",60000*120 + 2000);
    };
    showStartInformation = function(userId,companyId){
    	if(userId==undefined){
    		userId = rosten.kernel.getUserInforByKey("idnumber");
    		companyId = rosten.kernel.getUserInforByKey("companyid");
    	}
    	showStartBbs(userId,companyId);
    	showStartGtask(userId,companyId);
    	showStartMail(userId,companyId);
    };
    showStartGtask = function(userId,companyId){
    	rosten.read(rosten.webPath + "/start/getGtask", {userId:userId,companyId:companyId}, function(data) {
    		var node = registry.byId("home_gtask").containerNode;
        	node.innerHTML = "";
        	
        	var ul = document.createElement("ul");
        	for (var i = 0; i < data.length; i++) {
        		 var li = document.createElement("li");
        		 ul.appendChild(li);
        		 
        		 var type = document.createElement("span");
        		 type.innerHTML = data[i].type;
                 domClass.add(type,"type");
                 li.appendChild(type);
        		 
        		 var a = document.createElement("a");
                 var span = document.createElement("span");
                 span.innerHTML = data[i].content;
                 a.appendChild(span);
                 a.setAttribute("href", "javascript:openGtask('" + data[i].type + "','" + data[i].id +  "')");
                 li.appendChild(a);
        		 
                 var span_time = document.createElement("span");
                 span_time.innerHTML = data[i].date;
                 domClass.add(span_time,"time");
                 li.appendChild(span_time);
        	}
        	node.appendChild(ul);
        });
    };
    more_gtask = function(){
    	var key = rosten.kernel.getMenuKeyByCode("personconfig");
    	if(key!=null){
    		rosten.kernel._naviMenuShow(key);
    		require(["rosten/app/SmsManage"],function(){
    			//show_bbsNaviEntity("gtaskManage");
    		});
    	}else{
    		rosten.alert("未找到相对应的模块,请通知管理员");
    	}
    };
    openGtask = function(type,id){
    	switch(type){
    	case "【公告】":
    		var userid = rosten.kernel.getUserInforByKey("idnumber");
    		var companyId = rosten.kernel.getUserInforByKey("companyid");
    		rosten.openNewWindow("bbs", rosten.webPath + "/bbs/bbsShow/" + id + "?userid=" + userid + "&companyId=" + companyId);
    		break;
    	case "【发文】":
    		var userid = rosten.kernel.getUserInforByKey("idnumber");
    		var companyId = rosten.kernel.getUserInforByKey("companyid");
    		rosten.openNewWindow("sendFile", rosten.webPath + "/sendFile/sendFileShow/" + id + "?userid=" + userid + "&companyId=" + companyId);
    		break;
    	case "【大事记】":
    		var userid = rosten.kernel.getUserInforByKey("idnumber");
    		var companyId = rosten.kernel.getUserInforByKey("companyid");
    		rosten.openNewWindow("dsj", rosten.webPath + "/dsj/dsjShow/" + id + "?userid=" + userid + "&companyId=" + companyId);
    		break;
    	case "【会议通知】":
    		var userid = rosten.kernel.getUserInforByKey("idnumber");
    		var companyId = rosten.kernel.getUserInforByKey("companyid");
    		rosten.openNewWindow("meeting", rosten.webPath + "/meeting/meetingShow/" + id + "?userid=" + userid + "&companyId=" + companyId);
    		break;
    	}
    };
    showStartMail = function(userId,companyId){
    	rosten.read(rosten.webPath + "/mail/publishMail", {userId:userId,companyId:companyId}, function(data) {
    		var node = registry.byId("home_personMail").containerNode;
        	node.innerHTML = "";
        	
        	var ul = document.createElement("ul");
        	for (var i = 0; i < data.length; i++) {
        		 var li = document.createElement("li");
        		 ul.appendChild(li);
        		 
        		 var level = document.createElement("span");
        		 level.innerHTML = data[i].level;
                 domClass.add(level,"level");
                 if(data[i].level=="【紧急】"){
                	 domStyle.set(level,"color","red");
                 }
                 li.appendChild(level);
        		 
        		 var a = document.createElement("a");
                 var span = document.createElement("span");
                 span.innerHTML = data[i].subject;
                 a.appendChild(span);
                 a.setAttribute("href", "javascript:openMail('" + data[i].id + "')");
                 li.appendChild(a);
        		 
                 var span_time = document.createElement("span");
                 span_time.innerHTML = data[i].date;
                 domClass.add(span_time,"time");
                 li.appendChild(span_time);
        	}
        	node.appendChild(ul);
        });
    };
    openMail = function(id){
    	var key = rosten.kernel.getMenuKeyByCode("person");
    	if(key!=null){
    		rosten.kernel._naviMenuShow(key);
    		require(["rosten/app/Mail"],function(){
    			var tmpSbb = connect.subscribe("closeUnderlay", null, function(obj){
    				var store = mail_grid.getStore();
                	store.fetchItemByIdentity({
                		identity:id,
                		onItem:function(item){
                			onMessageOpen(store.getValue(item, "rowIndex")-1);
                		}
                	});
                connect.unsubscribe(tmpSbb);
    			});
    		});
    	}else{
    		rosten.alert("未找到相对应的模块,请通知管理员");
    	}
    };
    more_mail = function(){
    	var key = rosten.kernel.getMenuKeyByCode("person");
    	if(key!=null){
    		rosten.kernel._naviMenuShow(key);
    	}else{
    		rosten.alert("未找到相对应的模块,请通知管理员");
    	}
    };
    showStartBbs = function(userId,companyId){
        rosten.read(rosten.webPath + "/bbs/publishBbs", {userId:userId,companyId:companyId}, function(_data) {
        	addUlInformation("home_bbs","openBbs",_data);
        });
    };
    addUlInformation = function(idname,functionName,data){
    	var node = registry.byId(idname).containerNode;
    	node.innerHTML = "";
    	
    	var ul = document.createElement("ul");
    	for (var i = 0; i < data.length; i++) {
    		 var li = document.createElement("li");
    		 ul.appendChild(li);
    		 
    		 var a = document.createElement("a");
             var span = document.createElement("span");
             span.innerHTML = data[i].topic;
             a.appendChild(span);
             a.setAttribute("href", "javascript:" + functionName + "('" + data[i].id + "')");
             li.appendChild(a);
             
             if(data[i].isnew && (data[i].isnew=="true" || data[i].isnew == true)){
            	 var newspan = document.createElement("span");
            	 newspan.innerHTML = "&nbsp;";
                 domClass.add(newspan,"new");
                 li.appendChild(newspan);
             }
    		 
             var span_time = document.createElement("span");
             span_time.innerHTML = data[i].date;
             domClass.add(span_time,"time");
             li.appendChild(span_time);
    	}
    	node.appendChild(ul);
    };
    openBbs = function(id){
    	var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("bbs", rosten.webPath + "/bbs/bbsShowStart/" + id + "?userid=" + userid + "&companyId=" + companyId);
		rosten.read(rosten.webPath + "/bbs/hasReadBbs/" + id, {userId:userid,companyId:companyId}, function(_data) {
			showStartBbs(userid,companyId);
		});
    };
    more_bbs = function(){
    	var key = rosten.kernel.getMenuKeyByCode("bbs");
    	if(key!=null){
    		rosten.variable.showStartBbs = true;
    		rosten.kernel._naviMenuShow(key);
    		require(["rosten/app/BbsManage"],function(){
    			show_bbsNaviEntity("newbbsManage");
    			rosten.variable.showStartBbs = false;
    		});
    	}else{
    		rosten.alert("未找到相对应的模块,请通知管理员");
    	}
    };
    searchPersonByKeyPress = function(evt){
        if(evt.keyCode == 13){
            searchPerson();
        }
    };
    searchPerson = function(){
    	var inputnode = registry.byId("personSearchInput");
    	if(inputnode.attr("value")==""){
    		rosten.alert("请输入查询关键字");
    		return;
    	}
    	var companyId = rosten.kernel.getUserInforByKey("companyid");
    	rosten.read(rosten.webPath + "/system/serachPerson", {serchInput:inputnode.attr("value"),companyId:companyId}, function(data) {
    		var personSearch = dom.byId("personSearch");
    		personSearch.innerHTML = "";
    		
    		for (var i = 0; i < data.length; i++) {
    			var tr = document.createElement("tr");
    			
    			var td1 = document.createElement("td");
    			td1.innerHTML = data[i].username;
    			tr.appendChild(td1);
    			
    			var td2 = document.createElement("td");
    			td2.innerHTML = data[i].phone;
    			tr.appendChild(td2);
    			
    			var td3 = document.createElement("td");
    			td3.innerHTML = data[i].mobile;
    			tr.appendChild(td3);
    			
    			var td4 = document.createElement("td");
    			td4.innerHTML = data[i].email;
    			tr.appendChild(td4);
    			
    			personSearch.appendChild(tr);
    		}
    		inputnode.attr("value","");
		});
    };
    addMailNavigation = function(){
    	if(registry.byId("mail_quick")==undefined){
    		console.log("mail_quick is null....");
    		var cp = new ContentPane({id:"mail_quick",executeScripts:true,renderStyles:true,region:"top",style:"padding:0px;height:30px"});
		   	cp.attr("href",rosten.webPath + "/mail/quick");
		   	registry.byId("sideBar").addChild(cp);
    	}else{
    		console.log("mail_quick is existed....");
    	}
    };
    deleteMailNavigation = function(){
    	var mail_quick = registry.byId("mail_quick");
    	if(mail_quick){
    		registry.byId('sideBar').removeChild(mail_quick);
    		mail_quick.destroy();
    	}
    };
    returnToMain = function() {
        var showInformation = rosten.kernel.getUserInforByKey("logoname");
        if (showInformation == "")
            showInformation = rosten.variable.logoname;
            rosten.kernel.returnToMain("&nbsp;&nbsp;欢迎进入" + showInformation + "，当前您已成功登录！......");
    };
    quit = function() {
        var dialog = rosten.confirm("是否确定退出系统?");
        dialog.callback = function() {
            window.opener = null;
            window.open('', '_self');
            window.close();
        };
    };
    refreshSystem = function() {
        window.location.replace(rosten.webPath);
    };
    /*
     *  更换皮肤
     */
    changeSkin = function() {
        var unid = rosten.kernel.getUserInforByKey("idnumber");
        if (unid == "rostenadmin") {
            rosten.alert("超级用户不允许执行此项操作！");
            return;
        }
        rosten.openNewWindow("userUIConfig", rosten.webPath + "/system/changeSkin/" + unid);

    };
    /*
     * 添加到收藏夹
     */
    addBookmark = function() {

        var url = window.location.href;
        var title = rosten.kernel.getUserInforByKey("logoname");
        if (document.all) {
            try {
                window.external.addFavorite(url, title);
            } catch (e) {
                try {
                    window.external.addToFavoritesBar(url, title);
                } catch (e1) {
                }
            }
        } else if (window.external) {
            window.sidebar.addPanel(title, url, "");
        }
    };
    chgPassword = function() {
        var unid = rosten.kernel.getUserInforByKey("username");
        if (unid == "rostenadmin") {
            rosten.alert("超级用户不允许执行此项操作！");
            return;
        }
        returnToMain();
        rosten.kernel.createRostenShowDialog(rosten.webPath + "/system/passwordChangeShow", {
            onLoadFunction : function() {

            }
        });
        //  rosten.kernel.getRostenShowDialog().changePosition(200,100);
    };
    chgPasswordSubmit = function() {
        var password = registry.byId("password");
        if (!password.isValid()) {
            rosten.alert("当前密码不正确！").queryDlgClose = function() {
                password.focus();
            };
            return;
        }
        var newpassword = registry.byId("newpassword");
        if (!newpassword.isValid()) {
            rosten.alert("新密码不正确！").queryDlgClose = function() {
                newpassword.focus();
            };
            return;
        }
        var newpasswordcheck = registry.byId("newpasswordcheck");
        if (!newpasswordcheck.isValid()) {
            rosten.alert("确认密码不正确！").queryDlgClose = function() {
                newpasswordcheck.focus();
            };
            return;
        }
        if (newpassword.getValue() != newpasswordcheck.getValue()) {
            rosten.alert("新密码与确认密码不一致！").queryDlgClose = function() {
                newpassword.focus();
            };
            return;
        }
        var content = {};
        content.password = password.getValue();
        content.newpassword = newpassword.getValue();
        content.id = rosten.kernel.getUserInforByKey("idnumber");

        rosten.read(rosten.webPath + "/system/passwordChangeSubmit", content, function(data) {
            if (data.result == "true") {
                rosten.kernel.hideRostenShowDialog();
                rosten.alert("修改密码成功,重新登录后生效!");
            } else if (data.result == "error") {
                rosten.alert("当前密码错误,修改密码失败!");
            } else {
                rosten.alert("修改密码失败!");
            }
        });

    };
    /*
     * 系统必须存在的函数，java后台产生，用来显示右边主页面内容
     */
    show_naviEntity = function(oString) {
        console.log(oString);
    };
    freshGrid = function() {
        rosten.kernel.refreshGrid();
    };
    main.deleteCallback = function(data){
    	if (data.result == "true" || data.result == true) {
            rosten.alert("成功删除!");
            rosten.kernel.refreshGrid();
        } else {
            rosten.alert("删除失败!");
        }
    };
    lang.mixin(rosten,main);
    return main;
});
