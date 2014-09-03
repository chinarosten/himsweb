<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>部门管理</title>

	<script type="text/javascript">
		require([
				"dojo/_base/kernel",
				"dijit/registry",
				"dojo/_base/connect",
				"dijit/Menu",
				"dijit/MenuItem",
				"dojo/data/ItemFileReadStore",
				"dijit/Tree",
				"dijit/tree/ForestStoreModel",
				"dijit/layout/BorderContainer",
				"dojox/layout/ContentPane",
				"dijit/form/SimpleTextarea",
				"dijit/form/Button"
			], function(kernel,registry,connect,Menu, MenuItem,ItemFileReadStore,Tree,ForestStoreModel){
			
			var depart_treenode;
			treeOnLoad = function(){
				var menu = registry.byId("depart_tree_menu");
				var tree = registry.byId("depart_tree");
				menu.bindDomNode(tree.domNode);

				connect.connect(menu,"_openMyself",tree,function(e){
					depart_treenode = registry.getEnclosingWidget(e.target);
				});
			}
			kernel.addOnLoad(function(){
				if(registry.byId("depart_tree_menu")) return;
				var menu = new Menu({
					id: 'depart_tree_menu',
					selector: ".dijitTreeNode"
				});
				menu.addChild(new MenuItem({
					label: "新建部门",
					disabled:false,
					iconClass:'docCreateIcon',
					onClick:function() {createSubDepart(depart_treenode)}
				}));
				menu.addChild(new MenuItem({
					label: "编辑部门",
					iconClass:"docOpenIcon",
					disabled:false,
					onClick:function(){editSubDepart(depart_treenode)}
				}));
				menu.addChild(new MenuItem({
					label: "删除部门",
					iconClass:"docDeleteIcon",
					disabled:false,
					onClick:function(){deleteSubDepart(depart_treenode)}
				}));
				
			});
			createSubDepart = function(selectedTreeNode){
				var w = registry.byId("departEditPane");
				var href = "${createLink(controller:'system',action:'departCreate')}";
				href = href + "?companyId=${company?.id}";
				if(!depart_treenode.item.root){
					href = href + "&parentId="+depart_treenode.item.id;
				}
				w.attr("href",href);
			}
			editSubDepart = function(selectedTreeNode){
				if(!selectedTreeNode.item.root){
					var w = registry.byId("departEditPane");
					var tree = registry.byId("depart_tree");
					
					if(tree.model==null) var store = tree.store;
					else store = tree.model.store;
					
					var href = "${createLink(controller:'system',action:'departShow')}";
					var href = href+"/"+selectedTreeNode.item.id;
					w.attr("href",href);
				}
			}
			deleteSubDepart = function(selectedTreeNode){
				var w = registry.byId("departEditPane");
				var tree = registry.byId("depart_tree");
				if(tree.model==null) var store = tree.store;
				else store = tree.model.store;
							
				rosten.confirm("您是否将删除所选中的部门？").callback = function(){
					var href = "${createLink(controller:'system',action:'departDelete')}";
					if(!selectedTreeNode.item.root){
						href = href + "/"+selectedTreeNode.item.id;
						w.attr("href",href);
					}
				}
			}
			refreshDepartTree = function(){
				var tree = registry.byId("depart_tree");
				if(tree){
					var store = new ItemFileReadStore({url:"${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}"});
					tree.destroy();
					var div = document.createElement("div");
					var treeModel = new ForestStoreModel({ 
				    	store: store, // the data store that this model connects to 
				    	query: {parentId:null}, // filter multiple top level items 
				    	//rootLabel: "部门层级", 
				    	childrenAttrs: ["children"] // children attributes used in data store. 
					}); 
					var tree = new Tree({
						id:"depart_tree",
						model: treeModel,
						onClick:function(item){
							if(item && !item.root){
								var w = registry.byId("departEditPane");
								var href = "${createLink(controller:'system',action:'departShow')}";
								var href = href+"/"+item.id;
								w.attr("href",href);
							}
						},
						onLoad:treeOnLoad,
						autoExpand:true,
						showRoot:false,
						openOnClick:false,openOnDblClick:true},div);
					var p = registry.byId("departTreePane");
					p.domNode.appendChild(tree.domNode);
				}
			}
			getItem = function(){
				var tree = registry.byId('depart_tree');
				if(tree.selectedItem){
					if(tree.selectedItem != tree.model.root){
						console.log(tree.selectedItem.name);
					}else{
						console.log("root 节点....");
					}
				}else{
					alert("请选择节点");
				}
				
			};
		});
	</script>
</head>
<body>
	<g:set var="dataurl" scope="page"> ${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}</g:set>
	<div data-dojo-id="treeDataStore" data-dojo-type="dojo/data/ItemFileReadStore" data-dojo-props='url:"${dataurl}"'></div>

	<div data-dojo-type="dijit/layout/BorderContainer" data-dojo-props='style:"height:100%;padding:0"'>
		
		<div id="departTreePane" data-dojo-type="dojox/layout/ContentPane" data-dojo-props="region:'leading',splitter:true,style:'width:260px'">
			<div id="depart_tree" data-dojo-type="dijit.Tree" data-dojo-props='store:treeDataStore, query:{parentId:null},
				showRoot:false,
				autoExpand:true, onLoad:function(){treeOnLoad()}'>
				<script type="dojo/method" data-dojo-event="onClick" data-dojo-args="item">
					if(item && !item.root){
						var w = dijit.byId("departEditPane");
						var href = "${createLink(controller:'system',action:'departShow')}";
						var href = href+"/"+item.id;
						w.attr("href",href);
					}
				</script>
			</div>
		</div>
		<div id="departEditPane" data-dojo-type="dojox/layout/ContentPane" 
			data-dojo-props="style:'padding:0px',renderStyles:true,region:'center'">
		</div>
	</div>

</body>
</html>