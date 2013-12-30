/**
 * @author rosten
 * @created 2013-12-01
 */
define(["dojo/_base/kernel"
		, "dojo/_base/lang"
		, "dijit/registry"
		, "dojo/dom-style"
		, "dojo/dom-construct"
		, "dojo/_base/connect"
		, "dojox/layout/ContentPane"
		,"rosten/kernel/kernel"
		,"rosten/util/general"
		, "rosten/kernel/behavior"], function(kernel, lang, registry, domStyle,domConstruct,connect,ContentPane,rostenKernel,general) {
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
            /*
             * 用于加载对应的js文件,此方法在后续开发过程中需要修改
             */
            console.log("loadjs file is :" + oString);
            if (oString == "系统管理" || oString == "平台管理") {
             	require(["rosten/app/SystemManage"]);
            } else if (oString == "系统配置") {
                require(["./ConfigManage"]);
                //require("rosten.js.Usual");
            } else if (oString == "个人办公") {
            	addMailNavigation();
                require(["rosten/app/Mail"]);
            } else if (oString == "员工管理") {
                require(["./SystemManage"]);
            } else if (oString == "业绩管理") {
                require(["./StaticManage"]);
            } else if (oString == "库存管理") {
                require(["./StockManage"]);
            }
        });
		
		rosten.kernel = new rostenKernel(naviJson);
        if (rosten.kernel.getMenuName() == "") {
            rosten.alert("获取后台数据出错！");
            return;
        } else {
            rosten.kernel.addUserInfo(data);
            returnToMain();
            //增加时获取后台session功能
            //setInterval("session_checkTimeOut()",60000*120 + 2000);
        }
    };
    addMailNavigation = function(){
    	if(registry.byId("mail_quick")==undefined){
    		console.log("mail_quick is null....");
    		
    		// var div = document.createElement('div');
    		var cp = new ContentPane({id:"mail_quick",executeScripts:true,renderStyles:true,region:"top",style:"padding:0px"});
    		// rosten.kernel.navigationContainer.domNode.appendChild(div);
	    	// domConstruct.place(cp.domNode,rosten.kernel.navigationContainer.domNode,"before");
		   	cp.attr("href",rosten.webPath + "/mail/quick");
		   	registry.byId("sideBar").addChild(cp);
    	}else{
    		console.log("mail_quick is existed....");
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

        rosten.reader(rosten.webPath + "/system/passwordChangeSubmit", content, function(data) {
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
    
    lang.mixin(rosten,main);
    return main;
});
