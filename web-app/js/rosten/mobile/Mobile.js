/**
 * @author rosten
 * 移动设备专用js
 */
define(["dijit/registry",
		"dojo/store/Observable",
		"dojo/store/Memory",
		"dojox/mobile/ListItem",
		"dojox/mobile/parser",
		"rosten/kernel/_kernel"], function(registry,Observable,Memory,ListItem) {
	
	initUserInfor = function(path,data){
		rosten.webPath = path;
		rosten.userInfor = data;
	};
	openBbs = function(){
		
		var id = this.get("bbsId");
		var itemList = this;
		
		rosten.readSync(rosten.webPath + "/bbs/getBbsJson",{id:id},function(data){
			document.getElementById("topic").innerHTML=data.topic;
			document.getElementById("category").innerHTML=data.category;
			document.getElementById("content").innerHTML=data.content;
			document.getElementById("date").innerHTML=data.publishDate;
			itemList.transitionTo("bbs_infor");
		});
	};
	getBbs = function(){
		rosten.readSync(rosten.webPath + "/bbs/publishBbs",{userId:rosten.userInfor.userid,companyId:rosten.userInfor.company},function(data){
			var bbsView = registry.byId("list_start");
			for (var i = 0; i < data.length; i++) {
				var listItem = new ListItem({
					variableHeight:true,
					transition:"slide",
					bbsId:data[i].id,
					onClick:openBbs,
					moveTo:"#",
					style:{fontSize:"10px"}
                });
				var table = document.createElement("table");
				var tr = document.createElement("tr");
				
				var td = document.createElement("td");
				var img = document.createElement("img");
				img.setAttribute("src",rosten.webPath + "/images/rosten/share/a_bbs.png");
				td.appendChild(img);
				tr.appendChild(td);
				
				var td1 = document.createElement("td");
//				var a = document.createElement("a");
//				a.setAttribute("class","lnk");
//				a.setAttribute("href", "javascript:void(0)");
//				a.setAttribute("onclick", "openBbs('" + listItem.get("id") + "','" + data[i].id + "')");
//				a.innerHTML = data[i].topic ;
//				td1.appendChild(a);
				var a = document.createElement("span");
				a.setAttribute("class","lnk");
				a.innerHTML = data[i].topic ;
				td1.appendChild(a);
				
				var span = document.createElement("span");
				span.setAttribute("style","font-size:10px;color:#000000");
				span.innerHTML = "<br><br>" + data[i].date;
				
				td1.appendChild(span);
				tr.appendChild(td1);
				
				table.appendChild(tr);
				listItem.domNode.appendChild(table);
				bbsView.addChild(listItem);
			}
		});
	};
	getUserMobiles = function(){
		rosten.readSync(rosten.webPath + "/mobile/getUserMobiles",{company:rosten.userInfor.company},function(data){
	      	mobile_store = Observable(new Memory({idProperty:"label", data: data}));
	      	var listMobile = registry.byId("list_mobile");
	      	listMobile.setStore(mobile_store,{});
	      	
	      	var childrens = listMobile.getChildren();
	      	for (var i = 0; i < childrens.length; i++) {
	      		childrens[i].onClick = getMobileInfor;
	      	}
	      	
		});
		
	};
	function getMobileInfor(){
		var id = this.get("userId");
		var itemList = this;
		rosten.readSync(rosten.webPath + "/mobile/getUserInfor",{id:id},function(data){
			registry.byId("mobile_header").set("label",data.chinaName);
			document.getElementById("mobile_depart").innerHTML=data.depart;
			document.getElementById("mobile_telephone").innerHTML=data.telephone;
			itemList.transitionTo("mobile_infor");
		});
	};
	
	telephoneCall = function(){
		var telephone = document.getElementById("mobile_telephone").innerHTML;
		if(telephone!="" && window.androidPhone){
			window.androidPhone.call(telephone);
		}
	};
	
});
