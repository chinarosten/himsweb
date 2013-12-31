/**
 * @author rosten
 */
define(["dojo/_base/declare",
        "dojo/_base/kernel",
		"dojo/_base/lang",
		"dojo/_base/xhr",
		"dojo/dom-construct",
		"dojo/dom-class",
		"dojo/_base/connect",
		"dijit/_WidgetBase", 
		"dijit/_TemplatedMixin",
		"dijit/Tree",
		"dojo/data/ItemFileReadStore",
		"dijit/tree/ForestStoreModel"], 
		function(declare,kernel,lang, xhr,domConstruct,domClass,connect,_WidgetBase,_TemplatedMixin,Tree,ItemFileReadStore,ForestStoreModel) {
	return declare("rosten.widget.RostenTree", [_WidgetBase, _TemplatedMixin], {
		
		templateString: "<div data-dojo-attach-point=\"containerNode\"></div>",
        id:"",
        store: null,
        treeLabel:"",
        url:"",
        defaultentry: "",
        postCreate: function(){
            this.id = this.id != "" ? this.id : this.widgetId;
            
            var _url = this.url;
            xhr.get({
                url: _url,
                handleAs: "json",
                preventCache: true,
                load: lang.hitch(this, function(response, ioArgs){
                    var treenode = domConstruct.create("div");
                    this.containerNode.appendChild(treenode);
                    domClass.add(this.containerNode, "tree");
                    
                    this.store = new ItemFileReadStore({
                        data: response
                    });
                    var model = new ForestStoreModel({
                    	rootId:"rostenTreeRoot",
                        store: this.store,
                        query: {type: '*'},
                        childrenAttrs: ["children"]
                    });
                    var _treeArgs ={
                        model:model,
                        openOnClick: true,
                        persist:false
                    };
                    if(this.treeLabel!=""){
                        _treeArgs.showRoot = true;
                        _treeArgs.label = this.treeLabel;
                    }else{
                        _treeArgs.showRoot = false;
                    }
                    
                    var _tree = new Tree(_treeArgs, treenode);
                    _tree.startup();
                    connect.connect(_tree, "onClick", this,"onclick");
                    
                    if (this.defaultentry != "") {
                        var fetchArgs = {
                            query: {expand: "yes"},
                            queryOptions: {deep: true,ignoreCase: true},
                            onComplete: lang.hitch(this, function(items, request){
                                if (items.length > 0) {
                                    kernel.forEach(items,function(item){
                                        var expandNode = _tree.getNodesByItem(item);
                                        var node = expandNode[0];
                                        if (node.isExpandable && !node.isExpanded) {
                                            _tree._expandNode(node);
                                        }   
                                    });
                                    //默认最后一个item为显示条目
                                    var entryItem = items[items.length-1];
                                    var id = model.getIdentity(entryItem);
                                    var entryNode = _tree._itemNodesMap[id][0];             
                                    _tree.focusNode(entryNode);
                                    _tree.set("paths", [["rostenTreeRoot",id]]);
                                    this.onclick(entryItem,entryNode);
                                }   
                            })  
                        };
                        
                        this.store.fetch(fetchArgs);
                    }
                    
                }),
                error: function(response, ioArgs){
                    console.log("error:" + response);
                    console.dir(response);
                }
            });
        },
        onclick:function(item,node){
            var action = this.store.getValue(item,"action");
            var name = this.store.getValue(item,"name");
            var id = this.store.getValue(item,"id");
            lang.hitch(null,action,name,id)();
        }
	});
});
