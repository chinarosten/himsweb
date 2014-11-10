/**
 * @author rosten
 */
define(["dojo/_base/declare",
		"dojo/_base/kernel",
		"dojo/_base/lang",
		"dojo/_base/xhr",
		"dojo/dom-style",
		"dojo/dom-class",
		"dojo/data/ItemFileWriteStore",
		"dojo/_base/connect", 
		"dojo/number",
		"dijit/_WidgetBase", 
		"dijit/_TemplatedMixin",
		"dijit/_WidgetsInTemplateMixin",
		"dojo/text!./templates/RostenGrid.html",
		"dijit/form/TextBox",
		"dojox/collections/SortedList", 
		"dojox/grid/_CheckBoxSelector",
		"dojox/grid/DataGrid",
		"rosten/kernel/_kernel",
		"rosten/kernel/behavior",
		"rosten/util/gen-dialog",
		"rosten/util/general"], 
		function(declare,
		         kernel,
		         lang, 
		         xhr,
		         domStyle,
		         domClass,
		         ItemFileWriteStore,
		         connect,
		         number,
		         _WidgetBase,
		         _TemplatedMixin,
		         _WidgetsInTemplateMixin,
		         template,
		         TextBox,
		         SortedList,
		         _CheckBoxSelector,
		         DataGrid,
		         _kernel,
		         behavior,
		         gendialog,
		         general) {
	return declare("rosten.widget.RostenGrid", [_WidgetBase, _TemplatedMixin,_WidgetsInTemplateMixin], {
//		widgetsInTemplate: true, //解析RostenGrid.html中的dojoType等dojo特有信息，false不会解析
		templateString: template,
		id: "",
        url: "",
        defaultUrl:"",//供搜索时使用，默认无搜索时defaultUrl与url同值
        urlContent:null,//url传入时，同时传入的参数
        store: null,
        structure: null,
        grid: null,
        query: "",
        sortStatus:false,
		showRowSelector:"false",//值为new时，采用dojox.grid_CheckBoxSelector方式展示,需要修改.dojoxGridRowbarInner中的background-image为none
		rowSelector:"20px",//采用dojox.grid_CheckBoxSelector方式展示时，此项功能废弃
		
        gridHeight: -1,//网格高度,格式为：数字型，系统会自动补齐，例如：100px
        autoGridRow:-1,//grid显示条目数,-1时autoHeight为auto
        
        pageSize: 15,//每页条目数,控制获取后台条目数
        showPageControl: true, // 是否载入页面控制信息
        emptymsg: "\u76ee\u524d\u6682\u65e0\u6570\u636e\uff01",//目前暂无数据！
        
        imgSrc:"images/rosten/share/wait.gif",
        
        pageControl: {
            page: 1,
            total: 0,
            totalpages: 0,
            start: 0,
			count:0	//每页条目数
        },
        
        connectArray:[],//关联connect句柄
        
		_gridUtil:null,//控制loading界面变量
		constructor:function(){
			this.refreshHeader = true;
			this.refreshPageControl = true;
			this.refreshData = true;
		},
		postCreate: function(){
			this.pageControl.count = this.pageSize;
 			this.id = this.id != "" ? this.id : this.widgetId;
 			this.defaultUrl= this.url;
            if (this.gridHeight > 0) {
                domStyle.set(this._gridNode, "height", this.gridHeight + "px");
            }
            this.query = {
                id: '*'
            };
            this.pageControl.start = (this.pageControl.page - 1) * this.pageControl.count + 1;
            this.gotoPage(1);
        },
		refresh:function(url,content){
			console.log("grid refresh is start");
			if(url && url!=""){
				this.url = url;
			}
			if(content){
				console.log("change refresh content...");
				this.pageControl.page = 1;
				this.gotoPage(this.pageControl.page,content);
			}else{
				this.gotoPage(this.pageControl.page);
			}
			console.log("grid refresh is end");
		},
		getGrid :function(){
			return this.grid;
		},
		getStore:function(){
			return this.store;
		},
		getSelection :function(){
			return this.grid.selection;
		},
		getSelected: function(){
            return this.grid.selection.getSelected();
        },
        clearSelected: function(){
            this.grid.selection.clear();
        },
        onRowDblClick: function(){
            console.log("onRowDblClick");
        },
        onCellClick:function(){
            console.log("onCellClick");
        },
        _gotoPage:function(pagenum){
        	var content = {refreshHeader:false,refreshPageControl:false,refreshData:true};
        	this.gotoPage(pagenum,false,content);
        },
		gotoPage: function(pagenum){
			//显示loading进度条
//			this._openLoading();
            var content = {};
            if (this.showPageControl) {
                content.showAllData = false;
                
                domStyle.set(this.loadingimg, "display", "inline");
                this.pageControl.page = pagenum;
                this.pageControl.start = (this.pageControl.page - 1) * this.pageControl.count + 1;
                content.showPageNum = this.pageControl.page;		//showPageNum:当前显示第几页
                content.perPageNum = this.pageControl.count;		//perPageNum：每页显示的条目数
                
                this._renderLink();
            }else {
                domStyle.set(this.pageControl, "display", "none");
                content.showAllData = true;
            }
            
            //用户传入参数
            if(arguments[1]!=false){
            	//更换搜索条件
            	this.urlContent = arguments[1];
            }
            
            //后台内部刷新参数
            if(arguments[2]==undefined){
            	//默认刷新grid所有信息
				content.refreshHeader = this.refreshHeader;
				content.refreshData = this.refreshData;
				content.refreshPageControl = this.refreshPageControl;
				
			}else{
				
				//2014-9-19增加搜索存储参数------------
//				for (var name in arguments[1]) {
//					content[name] = arguments[1][name];
//				}
				//-------------------------------
				if(content.refreshHeader==undefined){
					content.refreshHeader = this.refreshHeader;
				}
				if(content.refreshData==undefined){
					content.refreshData = this.refreshData;
				}
				if(content.refreshPageControl==undefined){
					content.refreshPageControl = this.refreshPageControl;
				}
			}
            
			if(this.urlContent!=null){
				lang.mixin(content,this.urlContent);
			}
            var args = {
                url: this.url,
                handleAs: "json",
                // timeout: 2000,
                preventCache: true,
                content:content,
                encoding:"utf-8",
                load: lang.hitch(this, function(response, ioArgs){
                    this._parseData(response);
                    connect.publish("closeUnderlay", [this]);
                }),
                error: lang.hitch(this, function(response, ioArgs){
					//this._closeLoading();
					_kernel.errordeal(this.containerNode,"\u65e0\u6cd5\u521d\u59cb\u5316\u8868\u683c\u5185\u5bb9\u6570\u636e...");//无法初始化数据
					this.onDownloadError(response);
                })
            };
            xhr.post(args);
        },
        onDownloadError:function(error){
            
        },
		_parseData: function(response){
            if (response.gridHeader) 
                this._parseGridHeader(response.gridHeader);
            if (response.gridData) 
                this._praseGridData(response.gridData);
            if (response.pageControl) 
                this._prasePageControl(response.pageControl);
        },
		_prasePageControl: function(data){
            //设置页面控制信息
            if (!data || !data.total) 
                return;
            this.pageControl.total = parseInt(data.total);
	
            if (this.pageControl.total % this.pageControl.count == 0) {
                this.pageControl.totalpages = this.pageControl.total / this.pageControl.count;
            }
            else {
                this.pageControl.totalpages = parseInt(this.pageControl.total / this.pageControl.count) + 1;
            }
            this._renderLink();
            this.totalCount.innerHTML = this.pageControl.total;
            this.totalPages.innerHTML = this.pageControl.totalpages;
            
            this.gotopage.setAttribute("value", this.pageControl.page);
            if (this.showPageControl) {
                domStyle.set(this.loadingimg, "display", "none");
            }
        },
        _checkMobile:function(){
            var ua = navigator.userAgent.toLowerCase();
            if(ua.indexOf("windows nt")!=-1) {  
                return false;
            }else{
                return true;
            }
        },
        _parseGridHeader: function(data){
            var isWin = this._checkMobile();
        	kernel.forEach(data,function(item){
        		if(item.formatter){
        			item.formatter = eval(item.formatter);
        		}
        		if(isWin && item.width=="auto"){
        		    item.width="180px";
        		}
        	});
			if (this.showRowSelector == "new") {
				var view = [];
				view.push({type:"dojox.grid._CheckBoxSelector"});
				
				var _1 = {};
				_1.cells = [];
				_1.cells.push(data);
				
				view.push(_1);
				this.structure = view;
				
			}else{
				this.structure = data;
			}	
        },
        _praseGridData: function(data){

        	this.store = new ItemFileWriteStore({
        		data:data
        	});
			
            if (this.grid == null) {
                var gridParms = {
                    store: this.store,
                    structure: this.structure,
                    query: this.query,
					canSort: lang.hitch(this,this.sortFunc),
                    autoRender:true,
					autoHeight:true,
//					columnReordering:true,//是否允许拖拉列,true表示允许
					loadingMessage:"\u7cfb\u7edf\u6b63\u5728\u8f7d\u5165\u6570\u636e\uff0c\u8bf7\u7a0d\u5019\uff01"//系统正在载入数据，请稍候！
                };
				
				if(this.showRowSelector=="true"){
					gridParms.rowSelector = this.rowSelector;
				}
                if (this.autoGridRow != -1) {
                    gridParms.autoHeight = this.autoGridRow;
                }
                this.grid = new DataGrid(gridParms, this._gridNode);
//                this.connect(window, "resize", this,"resize");	
				//关闭loading进度条
//				this._closeLoading();
                this.grid.startup();
//                this.resize();
                this.connectArray.push(connect.connect(this.grid, "onRowDblClick", this, "onRowDblClick"));
                this.connectArray.push(connect.connect(this.grid, "onCellClick", this, "onCellClick"));
            }
            else {
                this.grid.setStore(this.store);
                this.grid.selection.clear();
				//this._closeLoading();
                this.resize();
				
            }
        },
        destroyConnect:function(){
        	kernel.forEach(this.connectArray, connect.disconnect);
        },
        _openLoading:function(){
			domStyle.set(this._gridData,"display","none");
			if(this._gridUtil==null){
				this._gridUtil = new gendialog();
			}
			this._gridUtil.showWaitDialog_1(this.containerNode,"rosten_gridDialog");
		},
		_closeLoading:function(){
			domStyle.set(this._gridData,"display","block");
			if(this._gridUtil==null){
				this._gridUtil = new gendialog();
			}
			this._gridUtil.hideWaitDialog_1("rosten_gridDialog");
		},
		sortFunc: function(idx){
            return this.sortStatus;
        },
        _resize: function(){
            if (this._resTimer) {
                clearTimeout(this._resTimer);
                this._resTimer = null;
            }
            this._resTimer = setTimeout(lang.hitch(this, this.resize), 10);
        },
        resize: function(){
            if (this.gridHeight > 0 ) {
                domStyle.set(this._gridNode, "height", this.gridHeight + "px");
            }
            if (this.grid) {
                this.grid.resize();
            }
        },
        pageKeyPress: function(event){
            if (event.keyCode == 13) {
                var _9 = number.format(this.gotopage.get("value"));
                if (_9 != null) {
                    if (_9 > this.pageControl.totalpages) {
                        _9 = this.pageControl.totalpages;
                    }
                    if (_9 < 1) {
                        _9 = 1;
                    }
                    this.pageControl.page = parseInt(_9);
                    this._gotoPage(this.pageControl.page);
                }else{
					this.gotopage.setAttribute("value", this.pageControl.page);
					behavior.alert("\u8bf7\u6b63\u786e\u8f93\u5165\u9875\u7801\u6570\u5b57\uff01");//请正确输入页码数字！
				}
            }
        },
        prevPage: function(){
            console.log("prev Page!");
            if (this.pageControl.page > 1) {
                this.pageControl.page = this.pageControl.page - 1;
                this._gotoPage(this.pageControl.page);
            }
        },
        nextPage: function(){
            console.log("next Page!");
            if (this.pageControl.page < this.pageControl.totalpages) {
                this.pageControl.page = this.pageControl.page + 1;
                this._gotoPage(this.pageControl.page);
            }
        },
        firstPage: function(){
            console.log("first Page!");
			if(this.pageControl.page>1){
				this.pageControl.page = 1;
            	this._gotoPage(1);
			}
        },
        lastPage: function(){
            console.log("last Page!");
			if(this.pageControl.totalpages > this.pageControl.page){
				this.pageControl.page = this.pageControl.totalpages;
            	this._gotoPage(this.pageControl.page);
			}
            
        },
        _renderLink: function(){
            if (this.pageControl.page == 1) {
                domClass.remove(this.firstButton, "firstpagelink");
                domClass.add(this.firstButton, "firstpagelinkdisabled");
                domClass.remove(this.prevButton, "prevpagelink");
                domClass.add(this.prevButton, "prevpagelinkdisabled");
            }
            else {
                domClass.remove(this.firstButton, "firstpagelinkdisabled");
                domClass.add(this.firstButton, "firstpagelink");
                domClass.remove(this.prevButton, "prevpagelinkdisabled");
                domClass.add(this.prevButton, "prevpagelink");
            }
            if (this.pageControl.page >= this.pageControl.totalpages) {
                domClass.remove(this.lastButton, "lastpagelink");
                domClass.add(this.lastButton, "lastpagelinkdisabled");
                domClass.remove(this.nextButton, "nextpagelink");
                domClass.add(this.nextButton, "nextpagelinkdisabled");
            }
            else {
                domClass.remove(this.lastButton, "lastpagelinkdisabled");
                domClass.add(this.lastButton, "lastpagelink");
                domClass.remove(this.nextButton, "nextpagelinkdisabled");
                domClass.add(this.nextButton, "nextpagelink");
            }
        }
	});
});
