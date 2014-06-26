/**
 * @author rosten
 * @date 2014-6-24
 */
define(["dojo/_base/declare", 
        "dojo/dom-style",
        "dojo/dom-class",
        "dojo/_base/xhr", 
        "dojo/dom-construct",
        "dojo/_base/connect",
        "dojo/_base/lang", 
        "dijit/_WidgetBase", 
        "dijit/Tree",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/data/ItemFileWriteStore",
        "dijit/tree/ForestStoreModel"
        ], 
   function(declare,domStyle,domClass,xhr,domConstruct,connect,lang,_WidgetBase,Tree,_WidgetBase,_TemplatedMixin,ItemFileWriteStore,ForestStoreModel) {
       
   declare("rosten.widget.LinkWidget", _WidgetBase, {
       
       linkName:"",
       buildRendering: function(){
            this.domNode = document.createElement("span");
            
            this.a = document.createElement("a");
            this.a.innerHTML = this.linkName;
            this.a.href = "#";
            domStyle.set(this.a,{"display":"none","marginLeft":"5px"});
            this.domNode.appendChild(this.a);
            this.inherited(arguments);
       }
   }); 
   declare("rosten.widget.LinkTreeNode", Tree._TreeNode, {
	   	linkName:"",
        linkFunction:null,
        linkShowType:"",
        
        postCreate: function(){
            this.inherited(arguments);
            
            if(this.item && !this.item.root && (this.item.type && this.linkShowType==this.item.type)){
                this.linkWidget = new rosten.widget.LinkWidget({linkName:this.linkName,linkFunction:this.linkFunction});
                connect.connect(this.linkWidget.a,"onclick",this,function(){
	            	this.linkFunction(this.item);
	            });
                this.contentNode.appendChild(this.linkWidget.domNode);
                // in real application this is done by template modification tree node class
                domConstruct.place(this.labelNode, this.linkWidget.domNode, "first");
            }
        }
    });   
   
   declare("rosten.widget.LinkTree",Tree, {
       linkShowType:"",
       linkName:"",
       linkFunction:null,
       
       focusNode: function(node){
            this.inherited(arguments);
            this.dndController.setSelection([node]);
        },

        _createTreeNode: function(args){
            args.linkName = this.linkName;
            args.linkFunction = this.linkFunction;
            args.linkShowType = this.linkShowType;
            return new rosten.widget.LinkTreeNode(args);
        }
  }); 
  return declare("rosten.widget.RostenLinkTree",[_WidgetBase, _TemplatedMixin] ,{
       
      templateString: "<div data-dojo-attach-point=\"containerNode\"></div>",
      id:"",
      store: null,
      url:"",
      
      linkName:"add",
      linkFunction:null,
      linkShowType:"depart",
      
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
                
                if(this.store==null){
                	this.store = new ItemFileWriteStore({
                        data: response
                    });
                }
                
                var model = new ForestStoreModel({
                    store: this.store,
                    query: {type: '*'},
                    childrenAttrs: ["children"]
                });
                
                var _treeArgs ={
                    model:model,
                    showRoot: false,
                    openOnClick: true,
                    persist:false,
                    linkName:this.linkName,
                    linkFunction:this.linkFunction,
                    linkShowType:this.linkShowType
                };
                this.tree = new rosten.widget.LinkTree(_treeArgs, treenode);
                
                connect.connect(this.tree.dndController,"onMouseOver",this,function(widget, evt){
                	//判断是否异步加载的数据，如异步加载，则增加widget
            		this._addLinkWidget(widget);
                	if(widget.linkWidget){
                		domStyle.set(widget.linkWidget.a,"display","");
                	}
                });
                this.tree.dndController.onMouseOut = function(widget, evt){
                	if(widget.linkWidget){
                    	domStyle.set(widget.linkWidget.a,"display","none");
                   	}
                };
                connect.connect(this.tree,"onClick",this,function(item){
                	this.onClick(item,this.store);
                });
                connect.connect(this.tree,"onOpen",this,function(item){
                	this.onOpen(item,this.store);
                });
                this.tree.startup();
                
            })
            
            
        });
      },
      _addLinkWidget:function(node){
    	  if(node.linkWidget) return;
    	  if(node.item && !node.item.root && (node.item.type && node.linkShowType==node.item.type)){
    		  node.linkWidget = new rosten.widget.LinkWidget({linkName:node.linkName,linkFunction:node.linkFunction});
              connect.connect(node.linkWidget.a,"onclick",node,function(){
            	  node.linkFunction(node.item);
	            });
              node.contentNode.appendChild(node.linkWidget.domNode);
              // in real application this is done by template modification tree node class
              domConstruct.place(node.labelNode, node.linkWidget.domNode, "first");
          }  
      },
	  onClick:function(item,store){
	  	
	  },
	  onOpen:function(item,store){
	  	
	  }
  });      
});