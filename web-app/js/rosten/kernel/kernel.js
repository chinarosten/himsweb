/**
 * @author rosten
 * @created 2013-11-27
 * control version
 */
define(["dojo/_base/kernel", 
        "dojo/_base/declare", 
        "dojo/dom", 
        "dijit/registry", 
        "dojo/_base/lang", 
        "dojo/_base/xhr", 
        "dojo/dom-style",
        "dojo/dom-class",
        "dojo/_base/connect",
        "dojox/collections/SortedList", 
        "rosten/kernel/_kernel",
        "rosten/widget/SimpleNavigation",
        "rosten/widget/ActionBar",
        "rosten/widget/RostenGrid",
        "rosten/widget/RostenTree",
        "rosten/util/general", 
        "rosten/widget/ShowDialog"], 
        function(kernel,declare, dom, registry, lang, xhr, domStyle,domClass,connect,SortedList, _kernel,simpleNavigation,ActionBar,RostenGrid,RostenTree,general,ShowDialog) {

    return declare("rosten.kernel.kernel", null, {
        constructor : function(naviJson) {
            this.general = new general();
            this.navigationContainer = registry.byId("navigationContainer");
            this.navigation = registry.byId("navigation");
            this.contentBody = registry.byId("contentBody");

            this.rostenNavigation = null;
            this.rostenActionBar = null;
            this.rostenGrid = null;
            this.rostenShowDialog = null;
            this.contentBodyDefault = "";
            //主体内容默认显示内容

            this.navigationMenuSrc = "", //获取导航条菜单路径
            this.navigationMenu = "";
            //当前导航条菜单信息，默认为导航条菜单数据存储中的模块id号
            this.navigationMenuData = new SortedList();
            //导航条菜单数据存储，格式为（模块id号：模块名称&导航条路径）
            this.navigationSrc = "", //获取左边导航条信息路径，此路径默认存储与导航条菜单数据中的导航条路径
            this.navigationEntity = "";
            //当前左边导航条信息
            this.navigationType = "stand";
            //默认当前左边导航条为普通展示类型，系统提供tree与stand两种类型
            //当前登陆人信息
            this.userInfo = new SortedList();

            this.connectArray = [];
            //connect关联方法集合

            if (naviJson != undefined && naviJson != "") {
                //从后台获取数据，初始化导航信息，包括导航菜单以及左边导航条
                if (naviJson.type)
                    this.navigationType = naviJson.type;

                if (naviJson.naviMenuSrc) {
                    this.createNaviMenu(naviJson.naviMenuSrc);
                }
            }
        },
        getUserInforByKey : function(key) {
            if (this.userInfo.contains(key)) {
                var value = this.userInfo.item(key);
                return value;
            } else {
                return "";
            }
        },
        getMenuValue : function(key) {
            if (this.navigationMenuData.contains(key)) {
                var menuValue = this.navigationMenuData.item(key);
                return this.general.stringLeft(menuValue, "&");
            } else {
                return "";
            }
        },
        getMenuName : function() {
            return this.navigationMenu;
        },
        getMenuPath : function(key) {
            if (this.navigationMenuData.contains(key)) {
                var menuValue = this.navigationMenuData.item(key);
                if(menuValue.indexOf("@")!=-1){
                    return this.general.stringLeft(this.general.stringRight(menuValue,"&"),"@");
                }else{
                    return this.general.stringRight(menuValue, "&");                    
                }
            } else {
                return "";
            }
        },
        getMenuType:function(key){
            if (this.navigationMenuData.contains(key)) {
                var menuValue = this.navigationMenuData.item(key);
                
                if(menuValue.indexOf("@") != -1){
                    return this.general.stringRightBack(menuValue, "@");
                }else{
                    return this.navigationType;
                }
            } else {
                return this.navigationType;
            }
        },
        addUserInfo : function(data) {
            this.userInfo.clear();
            for (var name in data) {
                this.userInfo.add(name, data[name]);
            }
        },
        addRightContent : function(dataJson) {
            if (!dataJson.identifier)
                return;
            if (this.navigationEntity == dataJson.identifier)
                return;
            this.destory();

            if (!dataJson.actionBarSrc)
                return;
            this.createActionBar({
                actionBarSrc : dataJson.actionBarSrc
            });

            this.createGridNavi(dataJson.identifier);

            if (!dataJson.gridSrc)
                return;
            this.createGrid({
                //              gridHeight: 530,    //暂时不需要固定高度
                url : dataJson.gridSrc,
                showRowSelector : "new"
            });
            this.navigationEntity = dataJson.identifier;
        },
        _addMenu : function(obj) {
            var ul = dom.byId("naviMenuUL");
            ul.innerHTML = "";

            if (obj.count > 0) {
                for (var i = 0; i < obj.count; i++) {
                	var _key = obj.getKey(i);
                    var getKey = obj.item(_key);
                    if (getKey == "default")
                        break;

                    var getValue = this.getMenuValue(getKey);

                    var li = document.createElement("li");
                    var a = document.createElement("a");
                    var span = document.createElement("span");

                    span.innerHTML = getValue;
                    a.appendChild(span);
                    a.setAttribute("href", "javascript:rosten.kernel._naviMenuShow('" + getKey + "')");
                    li.appendChild(a);
                    ul.appendChild(li);
                }

            }
        },
        _naviMenuCallback : function(data) {
            this.navigationMenuData.clear();
            var navigationSerial = new SortedList();
            for (var name in data) {
            	var _name = this.general.stringLeft(name,"&");
            	var serialNo = this.general.stringRight(name,"&");
            	
                this.navigationMenuData.add(_name, data[name]);
                navigationSerial.add(serialNo,_name);
            }
            //添加导航菜单
            this._addMenu(navigationSerial);

            //添加导航条信息
            if (this.navigationMenuData.containsKey("default")) {

                //定位到default菜单
                var navigationMenu = this.navigationMenuData.entry("default");
                this._naviMenuShow(navigationMenu);
            }

        },
        _naviMenuShow : function(idkey) {
            var _url = this.getMenuPath(idkey);
            if (_url.indexOf("js:") != -1) {
                var _array = [];
                _array.push(this.general.stringRight(_url, ":"));
                connect.publish("loadspecmenu", _array);
                return;
            }
            if (this.navigationMenu != idkey) {
                var menuType = this.getMenuType(idkey);
                
                if (menuType == "stand") {
                    this.createSimpleNavi({
                        url : this.getMenuPath(idkey),
                        urlArgs : {
                            id : idkey
                        }
                    }, idkey);
                } else if (menuType == "tree") {
                    this.createNavigation({
                        url : this.getMenuPath(idkey),
                        treeLabel : this.getMenuValue(idkey),
                        defaultentry : "yes"
                    },idkey);
                }
                
                var _array = [];
                _array.push(this.getMenuValue(this.navigationMenu));
                connect.publish("loadjsfile", _array);
            
                this.returnToMain();
            } else {
                console.log("当前不需要变换导航条.......");
            }
        },
        createSimpleNavi : function(src, naviMenuName) {
            this.createNavigationTitle(this.getMenuValue(naviMenuName));
            this.navigationMenu = naviMenuName;
            if (this.rostenNavigation && this.rostenNavigation != null) {
                this.rostenNavigation.destroy();
            }
            src.id = "rosten_navigation";
            this.rostenNavigation = new simpleNavigation(src);
            this.navigation.attr("content", this.rostenNavigation.domNode);
        },
        createNavigationTitle : function(titleName) {
            //初始化导航信息
            this.navigation.attr("title", titleName);
        },
        createNaviMenu : function(src) {
            this.navigationMenuSrc = src;
            // 同步后台获取菜单数据
            var ioArgs = {
                url : src,
                timeout : 3000,
                handleAs : "json",
                preventCache : true,
                sync : true,
                load : lang.hitch(this, function(response) {
                    if (response == "error") {
                        return;
                    }
                    this._naviMenuCallback(response);
                }),
                error : function(response, args) {
                    console.log(response);
                }
            };
            xhr.get(ioArgs);
        },
        createGridNavi:function(oString){
            var gridNavi = document.createElement("div");
            
            domClass.add(gridNavi,"verticalAlign");
            domStyle.set(gridNavi,{"marginTop":"5px","marginBottom":"5px","height":"25px"});
                        
            var table = document.createElement("table");
            gridNavi.appendChild(table);
            
            var tbody = document.createElement("tbody");
            table.appendChild(tbody);
            
            var tr = document.createElement("tr");
            tbody.appendChild(tr);
            
            var td = document.createElement("td");
            tr.appendChild(td);
            
            var img_1 = document.createElement("img");
            img_1.src = "images/rosten/share/icon_line.gif";
            td.appendChild(img_1);
            
            var img_2 = document.createElement("img");
            img_2.src = "images/rosten/share/icon_navigator.gif";
            td.appendChild(img_2);
            
            var td2 = document.createElement("td");
            tr.appendChild(td2);
            
            var div = document.createElement("div");
            div.innerHTML = this.getMenuValue(this.navigationMenu) + " >> " + this.rostenNavigation.getShowName(oString);
            
            domStyle.set(div,{"marginLeft":"5px"});
            td2.appendChild(div);
            
            this.contentBody.domNode.appendChild(gridNavi);
            
        },
        createNavigation: function(src,naviMenuName){
            this.createNavigationTitle(this.getMenuValue(naviMenuName));
            this.navigationMenu = naviMenuName;
            if (this.rostenNavigation && this.rostenNavigation != null) {
                this.rostenNavigation.destroy();
            }
            src.id = "rosten_navigation";
            
            this.rostenNavigation = new RostenTree(src);
            this.navigation.attr("content", this.rostenNavigation.domNode);
        },
        createActionBar:function(src){
            if(this.rostenActionBar && this.rostenActionBar!=null){
                this.rostenActionBar.destroyRecursive();
            }
            src.id = "rosten_actionBar";
            this.rostenActionBar = new ActionBar(src);
            this.contentBody.domNode.appendChild(this.rostenActionBar.domNode);
        },
        createGrid:function(src){
            if(this.rostenGrid && this.rostenGrid!=null){
                this.rostenGrid.destroyRecursive();
            }
            src.id = "rosten_grid";
            this.rostenGrid = new RostenGrid(src);
            this.contentBody.domNode.appendChild(this.rostenGrid.domNode);
        },
        getnavigationTitle :function(){
            return this.navigationTitle;
        },
        getNavigation:function(){
            return this.rostenNavigation;
        },
        getContentBody:function(){
            return this.contentBody;
        },
        getActionBar:function(){
            return this.rostenActionBar;
        },
        getGrid:function(){
            return this.rostenGrid;
        },
        refreshActionBar:function(src){
            this.rostenActionBar.refresh(src);
        },
        refreshGrid:function(src,content){
            if(this.rostenGrid){this.rostenGrid.refresh(src,content);}
        },
        refresh:function(obj){
            if(obj && obj!=""){
                this.refreshActionBar(obj.actionUrl);
                this.refreshGrid(obj.gridUrl);
            }else{
                this.refreshActionBar();
                this.refreshGrid();
            }
        },
        destoryActionBar:function(){
            if(this.rostenActionBar){
                this.rostenActionBar.destory();
            }
        },
        destoryNavigation:function(){
            if(this.rostenNavigation){
                this.rostenNavigation.destory();
                this.navigationEntity = "";
            }
        },
        destoryGrid:function(){
            if(this.rostenGrid){
                this.rostenGrid.destory();
            }
        },
        destory:function(){
            this.destoryConnect();
            this.contentBody.destroyDescendants();
            this.navigationEntity = "";
        },
        returnToMain:function(oString){
            this.destory();
            if(oString){
                this.contentBodyDefault = oString;
            }else{
                var showInformation = this.getUserInforByKey("logoname");
                if(showInformation=="") showInformation = _kernel.variable.logoname;
                this.contentBodyDefault = "&nbsp;&nbsp;欢迎进入" + showInformation + "，当前您已成功登录！......";
            }
            var div = document.createElement("div");
            domStyle.set(div,{"marginLeft":"10px","marginTop":"5px"});
            div.innerHTML = this.contentBodyDefault;
            this.contentBody.attr("content",div);
            this.navigationEntity = ""; 
        },
        createRostenShowDialog:function(src,args){
            var obj = {src:src};
            if(args){
                if(args.callback)obj.callback = args.callback;
                if(args.callbackargs) obj.callbackargs = args.callbackargs;
                if(args.onLoadFunction) obj.onLoadFunction = args.onLoadFunction;
            }
            if(this.rostenShowDialog) this.rostenShowDialog.destroy();
            this.rostenShowDialog = new ShowDialog(obj);
        },
        getRostenShowDialog:function(){
            if (this.rostenShowDialog){
                return this.rostenShowDialog;
            }
        },
        hideRostenShowDialog:function(){
            if (this.rostenShowDialog){
                console.log("showdialog start hide...");
                this.rostenShowDialog.hide();
                this.rostenShowDialog.destroy();
                console.log("showdialog hide is sucessful...");
            }
        },
        addConnect:function(connect){
            if(connect!=undefined){
                this.connectArray.push(connect);
            }
        },
        destoryConnect:function(){
            kernel.forEach(this.connectArray, connect.disconnect);
        },
        setRightContent:function(data,isShowName){
            this.destoryConnect();
            this.contentBody.attr("content",data);
            if(isShowName){
                if(isShowName!="" && isShowName!=this.navigationEntity){
                    this.navigationEntity = isShowName;
                }
            }
            
        },
        setHref:function(href,isShowName,connectFunc){
            this.destoryConnect();
            this.contentBody.attr("href", href);
            if(isShowName){
                if(isShowName!="" && isShowName!=this.navigationEntity){
                    this.navigationEntity = isShowName;
                }
            }
            if(connectFunc){
                this.connectArray.push(connect.connect(this.contentBody,"onLoad",connectFunc));
            }
        }
    });
});
