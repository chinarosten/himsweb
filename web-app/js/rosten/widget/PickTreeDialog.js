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
		"rosten/widget/_Dialog",
		"rosten/widget/CheckBoxTree"], 
		function(declare,lang, ItemFileWriteStore,xhr,domStyle,domClass,connect,_Dialog,CheckBoxTree) {
	return declare("rosten.widget.PickTreeDialog", [_Dialog], {
		
		title: "Rosten_Tree_Dialog",
        url: null,
        rootLabel: "Rosten",
        showCheckBox: false,
        showRoot: true,
        query:{parentId:null},
        folderClass:null,
        
        height: "380px",
        width: "400px",
        returnData: [],
        urltype: "JSON", //NORMAL:Tree数据直接从URL获得；XML:Tree数据XML，需解析；部门/人员
        buildContent: function(node){
            if (this.urltype == "XML") {//通过io从remote获取xml，并解析生成tree；
                var args = {
                    url: this.url,
                    handleAs: "xml",
                    timeout: 5000,
                    load: lang.hitch(this, function(response, ioArgs){
                        var doc = response.documentElement;
                        var valid = doc.getElementsByTagName("valid")[0];
                        
                        var _rootLabel = doc.getElementsByTagName("title")[0];
                        this.rootLabel = _rootLabel.textContent || _rootLabel.text;
                        var data = {
                            "identifier": "id",
                            "label": "name",
                            "items": []
                        };
                        if ((valid.text || valid.textContent) == "OK") {
                            var datalist = doc.getElementsByTagName("datalist")[0];
                            var items = datalist.getElementsByTagName("Department");
                            
                            for (var i = 0; i < items.length; i++) {
                                var item = items[i];
                                var id = item.getAttribute("id");
                                var name = item.getAttribute("name");
                                
                                var level_1 = {
                                    id: id,
                                    name: name,
                                    type: "parent",
                                    children: []
                                };
                                if (this.showCheckBox) {
                                    level_1.checkbox = false;
                                }
                                var arr = item.getElementsByTagName("Person");
                                for (var k = 0; k < arr.length; k++) {
                                    var person = arr[k].text || arr[k].textContent;
                                    var person_id = arr[k].getAttribute("id");
                                    
                                    var _1 = {
                                        id: person_id,
                                        name: person,
										type:"child"
                                    };
                                    if (this.showCheckBox) {
                                        _1.checkbox = false;
                                    }
                                    level_1.children.push(_1);
                                }
                                
                                data.items.push(level_1);
                            }
                            console.log(data);
                            this.treeStore = new ItemFileWriteStore({
                                data: data
                            });
                            this._buildTree(node);
                        }
                        
                    }),
                    error: lang.hitch(this, function(response, ioArgs){
                        console.error(response);
                    })
                };
                xhr.get(args);
            }else {
                this.treeStore = new ItemFileWriteStore({
                    url: this.url
                });
                this._buildTree(node);
            }
        },
        _buildTree: function(node){
            var treepane = document.createElement("div");
            domStyle.set(treepane, {
                height: "295px",
                padding: "3px",
                marginBottom: "5px",
                border: "1px solid gray",
                overflow: "auto"
            });
            if(this.folderClass!=null){
            	domClass.add(treepane, this.folderClass);
            }
            node.appendChild(treepane);
            var treenode = document.createElement("div");
            treepane.appendChild(treenode);
            
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
                autoExpand:true,
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
            this.returnData.length = 0;
            this.returnData.push(item);
        },
        getData: function(){
            if (!this.showCheckBox) 
                return this.returnData;
            var data = [];
            var treeStore = this.tree.model.store;
            var _this = this;
            treeStore.fetch({
                query: {
                    checkbox: true
                },
                queryOptions: {
                    deep: true
                },
                onComplete: function(items, request){
                    for (var i = 0; i < items.length; i++) {
                        var item = items[i];
							data.push(item);
                    }
                }
                
                
            });
            return data;
        }
    });
});
