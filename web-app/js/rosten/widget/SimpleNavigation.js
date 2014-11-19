/**
 * @author rosten
 */
define(["dojo/_base/declare",
        "dojo/_base/kernel", 
		"dojo/_base/lang",
		"dojo/dom-class",
		"dojo/_base/xhr",
		"dijit/_WidgetBase", 
		"dijit/_TemplatedMixin",
		"dojox/collections/SortedList", 
		"rosten/util/general"], function(declare,kernel,lang,domClass,xhr,_WidgetBase,_TemplatedMixin,SortedList,general) {
	return declare("rosten.widget.SimpleNavigation", [_WidgetBase, _TemplatedMixin], {
		
		general:new general(),
		
		id: "",
		url: "", // back data--jsonString
		urlArgs:null,//传入后台参数
        templateString: '<div class="simpleNavigation"' +
        '	><div  data-dojo-attach-point="containerNode"' +
        '	>Loading...</div>' +
        '</div>',
        navigationData:null,//传入参数记录
        
        defaultentry:"",//缺省的导航信息
        
        ulnode:null,
        
        constructor: function(){
        },
		postCreate: function(){
            this.id = this.id != "" ? this.id : this.widgetId;
            this.navigationData = new SortedList();
            
            if (this.url != "") {
                this.refreshNavigation(this.url);
            }
        },
		refreshNavigation: function(url){
            this.containerNode.innerHTML = "";
            if (this.url != url) 
                this.url = url;
            var args = {
                url: this.url,
                sync: true,//同步
				preventCache: true,
                handleAs: "json",
                load: lang.hitch(this, function(data){	
                    this._setData(data);
                }),
                error: lang.hitch(this, function(data){
                    console.log("get Navigation data is error: ", data);
                    this.onDownloadError(data);
                })
            };
			if(this.urlArgs!=null) args.content = this.urlArgs;
            xhr.post(args);
        },
        onDownloadError:function(error){
            
        },
		_setData: function(/*JSON*/data){
        	console.log("navigation set data start");
        	if(this.navigationData!=null){
        		this.navigationData.clear();
        	}else{
        		this.navigationData = new SortedList();
        	}
			var ulnode = document.createElement("ul");
            for (var i = 0; i < data.length; i++) {
            	
            	//处理缺省导航信息----2014-9-10-------------------
            	if(i==0) this.defaultEntity = data[i].url;
            	if(data[i].isDefault == true || data[i].isDefault == "true"){
            		this.defaultEntity = data[i].url;
            	}
            	//------------------------------------------
            	
                var showName = data[i]["name"];
                var img = data[i]["img"];
                var href = data[i]["href"];
                var liId = this.general.stringLeft(this.general.stringRight(href,"\""),"\"");
                
				var linode = document.createElement("li");
				linode.setAttribute("liid",liId);
				 
                var linknode = document.createElement("A");
                linknode.setAttribute("href", href);
                
                var imgnode = document.createElement("img");
                var srcattr = document.createAttribute("src");
                srcattr.nodeValue = img;
                imgnode.setAttributeNode(srcattr);

                linknode.appendChild(imgnode);
                linknode.appendChild(document.createTextNode("\u00A0\u00A0" + showName));
                
                linode.appendChild(linknode);
                ulnode.appendChild(linode);
                
                this.navigationData.add(liId,showName);
                
                this.ulnode = ulnode;
                
            }
			this.containerNode.appendChild(ulnode);
            console.log("navigation set data end");
        },
        rendNavigationClass:function(str){
        	kernel.forEach(this.ulnode.childNodes, function(entry){
        		if(entry.getAttribute("liid")==str){
        			domClass.add(entry, "bgClass");
        		}else{
        			domClass.remove(entry, "bgClass");
        		}
        	});
        },
		getShowName:function(key){
			if(this.navigationData.contains(key)){
				var value = this.navigationData.item(key);
				return value;
			}else{
				return "";
			}
		}
	});
});
