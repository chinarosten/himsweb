<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>部门管理</title>

	<script type="text/javascript">
		dojo.require("dijit.layout.BorderContainer");
		dojo.require("dojox.layout.ContentPane");
		dojo.require("dijit.Tree");
		dojo.require("dojo.data.ItemFileReadStore");
		dojo.require("dijit.form.SimpleTextarea");
		dojo.require("dijit.form.Button");
		dojo.require("dijit.Menu");
		dojo.require("dijit.MenuItem");

		var depart_treenode;

		treeOnLoad = function(){
			var menu = dijit.byId("depart_tree_menu");
			var tree = dijit.byId("depart_tree");
			menu.bindDomNode(tree.domNode);

			dojo.connect(menu,"_openMyself",tree,function(e){
				depart_treenode = dijit.getEnclosingWidget(e.target);
			});
		}
		dojo.addOnLoad(function(){
			if(dijit.byId("depart_tree_menu")) return;
			var menu = new dijit.Menu({
				id: 'depart_tree_menu'
			});
			menu.addChild(new dijit.MenuItem({
				label: "新建部门",
				disabled:false,
				iconClass:'docCreateIcon',
				onClick:function() {createSubDepart(depart_treenode)}
			}));
			menu.addChild(new dijit.MenuItem({
				label: "编辑部门",
				iconClass:"docOpenIcon",
				disabled:false,
				onClick:function(){editSubDepart(depart_treenode)}
			}));
			menu.addChild(new dijit.MenuItem({
				label: "删除部门",
				iconClass:"docDeleteIcon",
				disabled:false,
				onClick:function(){deleteSubDepart(depart_treenode)}
			}));
			
		});
		createSubDepart = function(selectedTreeNode){
			console.log(depart_treenode);
			
			var w = dijit.byId("departEditPane");
			var href = "${createLink(controller:'system',action:'departCreate')}";
			href = href + "?companyId=${company?.id}";
			if(!depart_treenode.item.root){
				href = href + "&parentId="+depart_treenode.item.id;
			}
			w.attr("href",href);
		}
		editSubDepart = function(selectedTreeNode){
			if(!selectedTreeNode.item.root){
				var w = dijit.byId("departEditPane");
				var tree = dijit.byId("depart_tree");
				
				if(tree.model==null) var store = tree.store;
				else store = tree.model.store;
				
				var href = "${createLink(controller:'system',action:'departShow')}";
				var href = href+"/"+selectedTreeNode.item.id;
				w.attr("href",href);
			}
		}
		deleteSubDepart = function(selectedTreeNode){
			var w = dijit.byId("departEditPane");
			var tree = dijit.byId("depart_tree");
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
			var tree = dijit.byId("depart_tree");
			if(tree){
				var store = new dojo.data.ItemFileReadStore({url:"${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}"});
				tree.destroy();
				var div = document.createElement("div");
				var treeModel = new dijit.tree.ForestStoreModel({ 
			    	store: store, // the data store that this model connects to 
			    	query: {parentId:null}, // filter multiple top level items 
			    	rootLabel: "部门层级", 
			    	childrenAttrs: ["children"] // children attributes used in data store. 
				}); 
				var tree = new dijit.Tree({
					id:"depart_tree",
					model: treeModel,
					onClick:function(item){
						if(item && !item.root){
							var w = dijit.byId("departEditPane");
							var href = "${createLink(controller:'system',action:'departShow')}";
							var href = href+"/"+item.id;
							w.attr("href",href);
						}
					},
					onLoad:treeOnLoad,
					autoExpand:true,
					openOnClick:false,openOnDblClick:true},div);
				var p = dijit.byId("departTreePane");
				p.domNode.appendChild(tree.domNode);
			}

		}
	</script>
</head>
<body>
	<g:set var="dataurl" scope="page"> ${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}</g:set>
	<div data-dojo-id="treeDataStore" data-dojo-type="dojo.data.ItemFileReadStore" data-dojo-props='url:"${dataurl}"'></div>

	<div data-dojo-type="dijit.layout.BorderContainer" data-dojo-props='style:"height:100%;padding:0"'>
		
		<div id="departTreePane" data-dojo-type="dojox.layout.ContentPane" data-dojo-props="region:'leading',splitter:true,style:'width:380px'">
			<div id="depart_tree" data-dojo-type="dijit.Tree" data-dojo-props='store:treeDataStore, query:{parentId:null},
				label:"部门层级",
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
		<div id="depart_tree_menu"></div>
		
		<div id="departEditPane" data-dojo-type="dojox.layout.ContentPane" 
			data-dojo-props="style:'padding:0px',renderStyles:true,region:'center'">
		</div>
	</div>

</body>
</html>