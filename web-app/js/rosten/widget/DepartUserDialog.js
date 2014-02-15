/**
 * @author rosten
 */
define(["dojo/_base/declare",
		"dojo/_base/lang",
		"dojo/data/ItemFileWriteStore",
		"dojo/_base/xhr",
		"dojo/dom-style",
		"dojo/dom-class",
		"dojo/_base/connect",
		"dojo/_base/window",
		"dojo/dom-construct",
		"dijit/form/ComboBox",
		"dijit/form/TextBox",
		"dijit/form/Button",
		"dijit/form/CheckBox",
		"dijit/form/MultiSelect",
		"rosten/widget/_Dialog",
		"rosten/widget/CheckBoxTree",
		"rosten/util/general"], 
		function(declare,lang, ItemFileWriteStore,xhr,domStyle,domClass,connect,win,domConstruct,ComboBox,TextBox,Button,CheckBox,MultiSelect,_Dialog,CheckBoxTree,General) {
	return declare("rosten.widget.DepartUserDialog", [_Dialog], {
		general:new General(),
	
		title: "人员选择",
        url: null,
        rootLabel: "Rosten",
        showCheckBox: true,
        showRoot: false,
        query:{parentId:null},
        folderClass:null,
        type:"multile",	//type为:multile多选;single:单选
        
        height: "565px",
        width: "600px",
        returnData: [],
        
        treeLable:null,
        
        buildContent: function(node){
        	domClass.add(node,"departUser");
        	
        	if(this.type!="multile"){
        		this.showCheckBox = false;
        		this.height="530px";
        		domStyle.set(this._dialog.domNode, "height", this.height);
        	}
        	
        	this.selectUtil = new this.general.selectObject();
            this.treeStore = new ItemFileWriteStore({url: this.url});
            this._buildSearchTable(node);
            this._buildTreeTable(node);
        },
        _buildSearchTable:function(node){
        	var table = document.createElement("table");
        	domClass.add(table,"fieldlist");
        	
        	var tr = document.createElement("tr");
        	table.appendChild(tr);
        	
        	var td1 = document.createElement("td");
        	var span1 = document.createElement("span");
        	span1.innerHTML = "部门名称";
        	td1.appendChild(span1);
        	
        	this.searchDepart = new ComboBox({
        		style:{width:"150px",marginLeft:"5px"},
        		store: this.treeStore,
        		query:this.query,
        		searchAttr:"name"
        	},document.createElement("div"));
        	td1.appendChild(this.searchDepart.domNode);
        	tr.appendChild(td1);
        	
        	var td2 = document.createElement("td");
        	
        	var span2 = document.createElement("span");
        	span2.innerHTML = "人员名称";
        	td2.appendChild(span2);
        	
        	this.searchUser = new TextBox({style:{width:"150px",marginLeft:"5px"}});
        	td2.appendChild(this.searchUser.domNode);
        	tr.appendChild(td2);
        	
        	var td3 = document.createElement("td");
        	
        	this.searchButton = new Button({
				label:"查询"
			});
			td3.appendChild(this.searchButton.domNode);
        	connect.connect(this.searchButton,"onClick" ,this,"searchFresh");
        	
        	tr.appendChild(td3);
        	
        	node.appendChild(table);
        },
        searchFresh:function(){
        	var depart = this.searchDepart.attr("value");
        	var user = this.searchUser.attr("value");
        	
        	//从后台重新获取相关数据
        	var url;
        	if(depart!="" || user!=""){
        		url = this.url + "&depart=" + depart + "&user=" + user;
        	}else{
        		url = this.url;
        	}
        	this.treeStore = new ItemFileWriteStore({url: url});
        	this.treenode.innerHTML = "";
            this._buildTree(this.treenode);
            
            if(depart!="" || user!=""){
            	this.tree.expandAll();
            }
			
			this.searchDepart.attr("value","");
			this.searchUser.attr("value","");        	
        	
        },
        _buildTreeTable:function(node){
        	var table = document.createElement("table");
        	domClass.add(table,"fieldlist");
        	
        	var tbody = document.createElement("tbody");
        	
        	var tr = document.createElement("tr");
        	var td1 = document.createElement("td");
        	td1.setAttribute("width","45%");
        	
        	var tabBox = document.createElement("div");
        	domClass.add(tabBox,"tab_box");
        	domStyle.set(tabBox,"height","400px");
        	td1.appendChild(tabBox);
        	
        	if(this.treeLable!=null){
        		var h3 = document.createElement("h3");
        		h3.innerHTML = this.treeLable;
        		tabBox.appendChild(h3);
        	}
        	
        	this.treenode = document.createElement("div");
        	domClass.add(this.treenode,"titlelist");
        	if(this.folderClass!=null){
            	domClass.add(this.treenode, this.folderClass);
            }
        	this._buildTree(this.treenode);
        	
        	tabBox.appendChild(this.treenode);
        	tr.appendChild(td1);
        	
        	var td2 = document.createElement("td");
        	td2.setAttribute("width","10%");
        	
        	var buttonChoose = document.createElement("div");
        	domClass.add(buttonChoose,"btn_choose");
        	
        	if(this.type=="multile"){
        		var allRightBtn = document.createElement("button");
            	domClass.add(allRightBtn,"allRight");
            	buttonChoose.appendChild(allRightBtn);
            	connect.connect(allRightBtn, "onclick", this, function(){
    				 this._getStoreItem(this.treeStore,{},function(items){
    				 	for (var i = 0; i < items.length; i++) {
                            var item = items[i];
                            if(item.type=="user" && !this.selectUtil.selectIsExitItem(this.searchResult.domNode,item.id)){
                            	this._addOption(this.searchResult.domNode,item);
                            }
    				        //清空选择信息
                        	this.tree.model.updateCheckbox(item,false);
                        }
    				 });	
            	});
            	buttonChoose.appendChild(document.createElement("br"));
        	}
        	
        	var rightBtn = document.createElement("button");
        	domClass.add(rightBtn,"right");
        	buttonChoose.appendChild(rightBtn);
        	connect.connect(rightBtn, "onclick", this, function(){
        		if(this.type=="multile"){
        			this._getStoreItem(this.treeStore,{checkbox: true},function(items){
    					for (var i = 0; i < items.length; i++) {
                            var item = items[i];
                            if(item.type=="user" && !this.selectUtil.selectIsExitItem(this.searchResult.domNode,item.id)){
                            	this._addOption(this.searchResult.domNode,item);
                            }
    				        //清空选择信息
                        	this.tree.model.updateCheckbox(item,false);
                        }
                        
    				});
        		}else{
        			if(this.returnData.length>0){
        				domConstruct.empty(this.searchResult.domNode);
                    	this._addOption(this.searchResult.domNode,this.returnData[0]);
        			}
        		}
				        		
        	});
        	buttonChoose.appendChild(document.createElement("br")); 
        	
        	var leftBtn = document.createElement("button");
        	domClass.add(leftBtn,"left");
        	buttonChoose.appendChild(leftBtn);
        	connect.connect(leftBtn, "onclick", this, function(){
				 var _node = this.searchResult.domNode;
				 for (var i = _node.length - 1; i>=0; i--) {
				 	if (_node.options[i].value==this.searchResult.get("value")) {
		                _node.remove(i);
		            }
				 }
				      		
        	});
        	buttonChoose.appendChild(document.createElement("br")); 
        	
        	if(this.type=="multile"){
        		var allLeftBtn = document.createElement("button");
            	domClass.add(allLeftBtn,"allLeft");
            	buttonChoose.appendChild(allLeftBtn);
            	connect.connect(allLeftBtn, "onclick", this, function(){
    				 domConstruct.empty(this.searchResult.domNode);
            	});
            	buttonChoose.appendChild(document.createElement("br")); 
        	}
        	
        	td2.appendChild(buttonChoose);
        	
        	tr.appendChild(td2);
        	
        	var td3 = document.createElement("td");
        	td3.setAttribute("width","45%");
        	
        	var tabBox1 = document.createElement("div");
        	domClass.add(tabBox1,"tab_box");
        	domStyle.set(tabBox1,"height","400px");
        	
        	this.searchResult = new MultiSelect({
        		style:{height:"400px",width:"100%",border:"none"}
        	},document.createElement("div"));
        	
        	tabBox1.appendChild(this.searchResult.domNode);
        	
        	td3.appendChild(tabBox1);
        	tr.appendChild(td3);
        	
        	tbody.appendChild(tr);
        	table.appendChild(tbody);
        	
        	if(this.type=="multile"){
        		var tfoot = document.createElement("tfoot");
            	var footTr = document.createElement("tr");
            	var footTd = document.createElement("td");
            	footTd.setAttribute("colspan","3");
            	
            	//全选
            	this.allselectnode = new CheckBox({},document.createElement("div"));
            	footTd.appendChild(this.allselectnode.domNode);
            	
            	var allSelectSpan = document.createElement("span");
            	allSelectSpan.innerHTML = "全选";
            	footTd.appendChild(allSelectSpan);
            	
            	//反选
            	this.noselectnode = new CheckBox({},document.createElement("div"));
            	footTd.appendChild(this.noselectnode.domNode);
            	
            	var noSelectSpan = document.createElement("span");
            	noSelectSpan.innerHTML = "反选";
            	footTd.appendChild(noSelectSpan);
            	
            	connect.connect(this.allselectnode, "onClick", this, function(){
            		this.allselectnode.set("checked",true);
            		this.noselectnode.set("checked",false);
            		//选中所有结点
            		this._getStoreItem(this.treeStore,this.query,function(items){
    				 	for (var i = 0; i < items.length; i++) {
                            var item = items[i];
                        	this.tree.model.updateCheckbox(item,true);
                        }
    				 });	
    	       	});
            	
            	connect.connect(this.noselectnode, "onClick", this, function(){
            		this.noselectnode.set("checked",true);
            		this.allselectnode.set("checked",false);
            		//清空所有结点
            		this._getStoreItem(this.treeStore,this.query,function(items){
    				 	for (var i = 0; i < items.length; i++) {
                            var item = items[i];
                        	this.tree.model.updateCheckbox(item,false);
                        }
    				 });
    	       	});
            	
            	footTr.appendChild(footTd);
            	tfoot.appendChild(footTr);
            	table.appendChild(tfoot);
        	}
        	
        	
        	node.appendChild(table);
        },
        _buildTree: function(node){
        	var treenode = document.createElement("div");
            node.appendChild(treenode);
          
            //根据treestore生成tree；
            var treeModel = new CheckBoxTree.model({
                store: this.treeStore,
                query: this.query,
                childrenAttrs: ["children"],
                rootLabel: this.rootLabel,
                checkboxAll: this.showCheckBox,
                checkboxRoot: false,
                checkboxState: false
            });
            var _openOnClick= true;
            if (!this.showCheckBox){
            	_openOnClick = false;
            }
            var _treeArgs = {
                model: treeModel,
                openOnClick: _openOnClick,
                autoExpand:false,
                persist: false,
                showRoot: this.showRoot
            };
            this.tree = new CheckBoxTree.tree(_treeArgs, treenode);
            if (!this.showCheckBox) {
                connect.connect(this.tree, "onClick", this, "onclick");
            }
            
            this.tree.startup();
        },
        refresh: function(){
	        this.contentPane.innerHTML = "";
            this.buildContent(this.contentPane);
        },
        onclick: function(item, node){
        	if(item.type=="user"){
        		domConstruct.empty(this.searchResult.domNode);
            	this._addOption(this.searchResult.domNode,item);
            	
            	this.returnData.length = 0;
            	this.returnData.push(item);
        	}else{
        		this.returnData.length = 0;
        	}
        },
        _addOption:function(node,item){
        	var c = win.doc.createElement('option');
	        c.innerHTML = item.name;
	        c.value = item.id;
	        c.departId = item.departId;
	        node.appendChild(c);
        },
        getData: function(){
        	var data = this.selectUtil.getAllValue(this.searchResult.domNode);
            return data;
        },
        _getStoreItem : function(store,args,callback){
			store.fetch({
				query:args,
				queryOptions: {
	                deep: true
	            },
				onComplete: lang.hitch(this,callback)
			});
		}
    });
});
