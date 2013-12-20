/**
 * @author rosten
 */
define(["dojo/_base/declare",
		"dojo/_base/lang",
		"dojo/_base/xhr",
		"dojo/query",
		"dojo/dom-style",
		"dojo/_base/kernel", 
		"dojo/_base/connect",
		"dijit/registry",
		"dijit/form/CheckBox",
		"dijit/form/TextBox",
		"dijit/form/Button",
		"rosten/widget/_Dialog",
		"rosten/util/general"], function(declare,lang, xhr,query,kernel,domStyle,connect,registry,CheckBox,TextBox,Button,_Dialog,general) {
	return declare("rosten.widget.MultiSelectDialog", [_Dialog], {
		
		height: "370px",
        width: "350px",
        title: "Rosten_选择框",
        chkboxwidgets: [],
        defaultvalues: [],
        datasrc: "",
        canappend: false,
        extra_choice: null,
        single: false,
        choicelist: [],
        choicelist_node: null,
        general:new general(),
        constructor: function(arguments){
            this.chkboxwidgets = [];
            if (this.initialized) {
                var chkdomlist = query(".dijitCheckBox", this._dialog.domNode);
                for (var i = 0; i < chkdomlist.length; i++) {
                    this.chkboxwidgets.push(registry.getEnclosingWidget(chkdomlist[i]));
                }
            }
        },
        buildContent: function(node){
            var fieldset = document.createElement("fieldset");
            var fieldsetheight = "290px";
            var h1 = "255px";
            if (this.canappend) {
                fieldsetheight = "240px";
                h1 = "220px";
            }
            domStyle.set(fieldset, {
                "border": "1px solid gray",
                "margin": "2px",
                "padding": "3px",
                "fontSize":"12px",
                "height": fieldsetheight
            });
            node.appendChild(fieldset);
            var legend = document.createElement("legend");
            domStyle.set(legend, {
                color: "blue"
            });
            legend.innerHTML = "请选择以下选项：";
            fieldset.appendChild(legend);
            
            this.choicelist_node = document.createElement("div");
            this.choicelist_node.setAttribute("id", "choicelist");
            domStyle.set(this.choicelist_node, {
                "height": h1,
                "overflow": "auto"
            });
            
            fieldset.appendChild(this.choicelist_node);
            
            if (this.datasrc == "") {
                this._render(node);
                return;
            }
            
            var ioArgs = {
                url: this.datasrc,
                handleAs: "json",
                timeout: 2000,
                preventCache: true,
                sync: true,
                load: lang.hitch(this, function(response, args){
                    this.choicelist = [];
					for (var i = 0; i < response.length; i++) {
						this.choicelist.push(response[i].name + "|" + response[i].id);
					}	
                    this._render(node);
                }),
                error: lang.hitch(this, function(response, args){
                    console.debug(response);
                })
            };
            xhr.get(ioArgs);
        },
        
        _render: function(node){

            var _1 = this.chkboxwidgets;
            var _this = this;
            kernel.forEach(this.choicelist, function(item){
                if (item.indexOf("|") != -1) {
                    var arr = item.split("|");
                    var item_id = "picklist_" + arr[1];
                    var item_name = arr[0];
                }
                else {
                    var item_id = "picklist_" + item;
                    var item_name = item;
                }
                if (registry.byId(item_id)) {
                    registry.byId(item_id).destroy(); //当对话框反复调用时，可能会重复；
                }
                var _0 = document.createElement("div");
                domStyle.set(_0, "height", "20px");
                var params = {
                    id: item_id,
                    name: item_name
                };
                var chkbox = new CheckBox(params, document.createElement("div"));
                if (_this.single == true) {
                    connect.connect(chkbox, "onClick", _this, "choiceCheck");
                }
                _0.appendChild(chkbox.domNode);
                var label = document.createElement("label");
                label.innerHTML = item_name;
                label.setAttribute("for", item_id);
                _0.appendChild(label);
                _this.choicelist_node.appendChild(_0);
                _1.push(chkbox);
            });
	           
            for (var i = 0; i < this.defaultvalues.length; i++) {
                var dv = this.defaultvalues[i];
                for (var j = 0; j < this.chkboxwidgets.length; j++) {
                    var w = this.chkboxwidgets[j];
                    if (w.attr("name") == dv) {
                        w.attr("value", "on");
                    }
                }
            }
            if (this.canappend) {
                var otherfieldset = document.createElement("fieldset");
                domStyle.set(otherfieldset, {
                    "border": "1px solid gray",
                    "margin": "2px",
                    "padding": "3px",
                    "fontSize":"12px",
                    "height": "40px"
                });
                node.appendChild(otherfieldset);
                var otherlegend = document.createElement("legend");
                domStyle.set(otherlegend, {
                    color: "blue"
                });
                otherlegend.innerHTML = "其他选项：";
                otherfieldset.appendChild(otherlegend);
                
                var label = document.createElement("span");
                label.innerHTML = "新值：";
                otherfieldset.appendChild(label);
                
                var div1 = document.createElement("div");
                domStyle.set(div1, {
                    "fontSize": "12px",
                    height: "20px",
                    width: "220px"
                });
                otherfieldset.appendChild(div1);
                this.extra_choice = new TextBox({
                    style: {
                        height: "18px",
                        width: "200px"
                    }
                }, div1);
                var w = this.extra_choice;
                connect.connect(w, "onKeyPress", this, "onKeyPress");
                
                var div2 = document.createElement("div");
                otherfieldset.appendChild(div2);
                var appendBtn = new Button({
                    label: "添加"
                }, div2);
                connect.connect(appendBtn, "onClick", this, "appendChoice");
            }
        },
        initCheckData:function(arrayValue){
			kernel.forEach(this.chkboxwidgets, function(w){
				
				var _value = w.attr("name");
				if(this.general.isInArray(arrayValue,_value)){
					w.attr("value",true);
				}
				/*
				 * 采用id号进行匹配
				var tmp = w.attr("id");
				var _value;
				if (tmp.indexOf("picklist_") != -1) {
					_value = tmp.split("picklist_")[1];
				}else{
					_value = tmp;
				}
				if(rosten.isInArray(arrayValue,_value)){
					w.attr("value",true);
				}
				*/
	
			});	
		},
        getData: function(){
            var data = [];
            kernel.forEach(this.chkboxwidgets, function(w){
                if (w.attr("value") != false) {
                	var _object = {};
                	
                    var tmp = w.attr("id");
                    var tmp_1 = w.attr("name");
                    
                    _object.name = tmp_1;
                    if (tmp.indexOf("picklist_") != -1) {
                    	_object.id = tmp.split("picklist_")[1];
                        data.push(_object);
                    }
                    else {
                    	_object.id = tmp;
                        data.push(_object);
                    }
                }
            });
            return data;
        },
        appendChoice: function(){
            var item = this.extra_choice.attr("value");
            if (lang.trim(item) == "") {
                this.extra_choice.attr("value", "");
                return;
            }
            var deptlist_node = query("#choicelist", this._dialog.domNode)[0];
            var _0 = document.createElement("div");
            domStyle.set(_0, "height", "20px");
            if (item.indexOf("|") != -1) {
                var arr = item.split("|");
                var item_id = "picklist_" + arr[1];
                var item_name = arr[0];
            }
            else {
                var item_id = "picklist_" + item;
                var item_name = item;
            }
            var params = {
                id: item_id,
                name: item_name
            };
            var chkbox = new CheckBox(params, document.createElement("div"));
            if (this.single) {
                connect.connect(chkbox, "onClick", this, "choiceCheck");
                for (var k = 0; k < this.chkboxwidgets.length; k++) {
                    this.chkboxwidgets[k].attr("value", false);
                }
            }
            chkbox.attr("value", "on");
            _0.appendChild(chkbox.domNode);
            var label = document.createElement("label");
            label.innerHTML = item_name;
            label.setAttribute("for", item_id);
            _0.appendChild(label);
            deptlist_node.appendChild(_0);
            deptlist_node.scrollTop = deptlist_node.scrollHeight;
            this.chkboxwidgets.push(chkbox);
            this.extra_choice.attr("value", "");
            
        },
        onKeyPress: function(e){
            if (e.keyCode == "13") {
                this.appendChoice();
            }
        },
        choiceCheck: function(e){
            var w = registry.getEnclosingWidget(e.target);
            if (w.attr("value") == "on") {
                for (var i = 0; i < this.chkboxwidgets.length; i++) {
                    if (this.chkboxwidgets[i] != w) {
                        if (this.chkboxwidgets[i].attr("value") == "on") 
                            this.chkboxwidgets[i].attr("value", false);
                    }
                }
            }
        },
        refresh: function(){
            this.contentPane.innerHTML = "";
            this.chkboxwidgets = [];
            if (this.initialized) {
                var chkdomlist = query(".dijitCheckBox", this._dialog.domNode);
                for (var i = 0; i < chkdomlist.length; i++) {
                    this.chkboxwidgets.push(registry.getEnclosingWidget(chkdomlist[i]));
                }
            }
            this.buildContent(this.contentPane);
        },
		simpleRefresh:function(){
			this.choicelist_node.innerHTML = "";
			this.chkboxwidgets = [];
            if (this.initialized) {
                var chkdomlist = query(".dijitCheckBox", this._dialog.domNode);
                for (var i = 0; i < chkdomlist.length; i++) {
                    this.chkboxwidgets.push(registry.getEnclosingWidget(chkdomlist[i]));
                }
            }
			this._render(this.contentPane);

		}
    });
});
