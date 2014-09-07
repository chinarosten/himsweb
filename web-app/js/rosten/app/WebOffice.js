/**
 * @author rosten
 */
define(function() {
	
	checkIsIE = function(){
		var Sys = {};
		var ua = navigator.userAgent.toLowerCase()
		var s;
		(s = ua.match(/rv:([\d.]+)\) like gecko/)) ? Sys.ie = s[1] :
		(s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
		(s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
		(s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
		(s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
		(s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
		
		return Sys.ie;
	};
	var weboffice = {};
	
	var webOfficeId = "WebOffice1";
	
	if(!checkIsIE()){
		webOfficeId = "Control";
	}
	var webObj = document.getElementById(webOfficeId);
	
	/****************************************************
	 *
	 *		关闭页面时调用此函数，关闭文件
	 *
	 ****************************************************/
	weboffice_close = function() {
		try {
			webObj.Close();
		} catch(e) {
			//	alert("异常\r\nError:"+e+"\r\nError Code:"+e.number+"\r\nError Des:"+e.description);
		}
	};

	/****************************************************
	 *
	 *					新建文档
	 *
	 ****************************************************/
	weboffice_newDoc = function() {
		try {
			//目前默认为word,doctype:doc,wps,xls
			var doctype = "doc";
			webObj.LoadOriginalFile("", doctype);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};
	
	/****************************************************
	*
	*					上传文档
	*
	/****************************************************/
	weboffice_uploadDoc = function(content,url) {
		/*
		 * conent格式为：[{name:"id1",value:"value1"},{name:"id2",value:"value2"}]
		 */
		try{
			webObj.OptionFlag = 0x0080;
			webObj.HttpInit();			//初始化Http引擎
			webObj.SetTrackRevisions(0);
			
			// 添加相应的Post元素 
			for(var o in content){
				webObj.HttpAddPostString(content[o].name, content[o].value);
			}
			webObj.HttpAddPostCurrFile("wordFile","");		// 上传文件
			return webObj.HttpPost(url);	// 判断上传是否成功
			
		}catch(e){
			alert("异常\r\nError:"+e+"\r\nError Code:"+e.number+"\r\nError Des:"+e.description);
		}
	};
	
	/****************************************************
	 *
	 *					打开本地文件
	 *
	 /****************************************************/
	weboffice_docOpen = function() {
		try {
			webObj.LoadOriginalFile("open", "doc");
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					保存文档
	 *
	 /****************************************************/
	weboffice_docSave = function() {
		try {
			webObj.Save();
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					另存为文档
	 *
	 /****************************************************/
	weboffice_saveAsTo = function() {
		try {
			webObj.ShowDialog(84);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *				显示打印对话框
	 *
	 /***************************************************/
	weboffice_showPrintDialog = function() {
		try {
			webObj.PrintDoc(1);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					直接打印
	 *
	 ****************************************************/
	weboffice_print = function() {
		try {
			webObj.PrintDoc(0);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					禁止打印
	 *
	 ****************************************************/
	weboffice_notPrint = function() {
		try {
			webObj.SetSecurity(0x01);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					恢复允许打印
	 *
	 /****************************************************/
	weboffice_okPrint = function() {
		try {
			webObj.SetSecurity(0x01 + 0x8000);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}

	};

	/****************************************************
	 *
	 *					禁止保存
	 *
	 ****************************************************/
	weboffice_notSave = function() {
		try {
			webObj.SetSecurity(0x02);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}

	};

	/****************************************************
	 *
	 *					恢复允许保存
	 *
	 /****************************************************/
	weboffice_okSave = function() {
		try {
			webObj.SetSecurity(0x02 + 0x8000);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}

	};

	/****************************************************
	 *
	 *					禁止复制
	 *
	 /****************************************************/
	weboffice_notCopy = function() {
		try {
			webObj.SetSecurity(0x04);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					恢复允许复制
	 *
	 /****************************************************/
	weboffice_okCopy = function() {
		try {
			webObj.SetSecurity(0x04 + 0x8000);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					全屏
	 *
	 /****************************************************/
	weboffice_fullScreen = function() {
		try {
			webObj.FullScreen = true;
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *				 解除文档保护
	 *
	 ****************************************************/
	weboffice_unProtect = function(password) {
		try {
			webObj.ProtectDoc(0, 1, password);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *				设置文档保护
	 *
	 ****************************************************/
	weboffice_protectFull = function(password) {
		try {
			webObj.ProtectDoc(1, 1, password);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};
	/****************************************************
	 *
	 *					盖章
	 *
	 /****************************************************/
	weboffice_addSeal = function() {
		try {
			//通过Document->application->CommandBars 获取到菜单对象
			var vObj = webObj.GetDocumentObject().Application.CommandBars("电子印章");
			if (vObj)
				vObj.Controls("盖章").Execute();
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					修订文档
	 *
	 /****************************************************/
	weboffice_protectRevision = function() {
		try {
			webObj.SetTrackRevisions(1);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					隐藏修订
	 *
	 /****************************************************/
	weboffice_unShowRevisions = function() {
		try {
			webObj.ShowRevisions(0);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					显示当前修订
	 *
	 /****************************************************/
	weboffice_uhowRevisions = function() {
		try {
			webObj.ShowRevisions(1);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					退出修订状态
	 *
	 /****************************************************/
	weboffice_exitRevisions = function() {
		try {
			webObj.SetTrackRevisions(0);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					接受当前所有修订
	 *
	 /****************************************************/
	weboffice_acceptAllRevisions = function() {
		try {
			webObj.SetTrackRevisions(4);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					拒绝当前所有修订
	 *
	 /****************************************************/
	weboffice_unAcceptAllRevisions = function() {
		try {
			var vCount = webObj.GetRevCount();
			var strUserName;
			for (var i = 1; i <= vCount; i++) {
				strUserName = webObj.GetRevInfo(i, 0);
				
				webObj.AcceptRevision(strUserName, 1);
			}
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					获取修订相关信息
	 *
	 /****************************************************/
	weboffice_getRevAllInfo = function() {
		var vCount;
		vCount = webObj.GetRevCount();
		var vOpt = 0;
		var vDate;
		for (var i = 1; i <= vCount; i++) {
			vOpt = webObj.GetRevInfo(i, 2);
			if ("1" == vOpt) {
				vOpt = "插入";
			} else if ("2" == vOpt) {
				vOpt = "删除";
			} else {
				vOpt = "未知操作";
			}
			vDate = new String(webObj.GetRevInfo(i, 1));
			vDate = parseFloat(vDate);
			dateObj = new Date(vDate);
			alert(dateObj.getYear() + "年" + dateObj.getMonth() + 1 + "月" + dateObj.getDate() + "日" + dateObj.getHours() + "时" + dateObj.getMinutes() + "分" + dateObj.getSeconds() + "秒");
			alert("用户:" + webObj.GetRevInfo(i, 0) + "\r\n操作:" + vOpt + "\r\n内容:" + webObj.GetRevInfo(i, 3));
		}
	};

	/****************************************************
	 *
	 *					设置当前操作用户
	 *
	 /****************************************************/
	weboffice_setUserName = function(username) {
		try {
			webObj.SetCurrUserName(username);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					设置书签
	 *
	 /****************************************************/
	weboffice_addBookmark = function(bookname) {
		if (bookname == undefined) {
			webObj.SetFieldValue("rosten", "加入书签{rosten}", "::ADDMARK::");
		} else {
			webObj.SetFieldValue(bookname, "加入书签{" + bookname + "}", "::ADDMARK::");
		}

	};

	weboffice_taohong = function(bookname, companyname) {
		if (bookname == undefined) {
			webObj.SetFieldValue("rosten", "<a href='www.baidu.com' >Rosten信息技术有限公司</a>", "");
		} else {
			webObj.SetFieldValue(bookname, companyname, "");
		}
	};

	/****************************************************
	 *
	 *					填充模板
	 *
	 /****************************************************/
	weboffice_fillBookMarks = function(bookmarkpath) {
		try {
			if (bookmarkpath == undefined) {
				webObj.BookMarkOpt("/system/wordTemplate", 2);
			} else {
				webObj.BookMarkOpt(bookmarkpath, 2);
			}

		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					套红及数据交互
	 *
	 /****************************************************/
	weboffice_linkRed = function() {
		window.open("mark.html", "newwindow", 'height=768, width=1024, top=0, left=0, toolbar=yes,resizable=yes, menubar=yes,location=yes, status=yes');
	};

	//------------------------------------------------自带工具栏区域---------------------------------------------------------------
	/****************************************************
	 *
	 *	设置weboffice自带工具栏“新建文档”显示或隐藏
	 *
	 /****************************************************/
	weboffice_toolBar_newDoc = function(flag) {
		try {
			var vCurItem = webObj.HideMenuItem(0);
			//根据vCurItem判断当前按钮是否显示
			if ((vCurItem & 0x01) && !flag) {
				webObj.HideMenuItem(0x01);
				//Show it
			} else {
				webObj.HideMenuItem(0x01 + 0x8000);
				//Hide it
			}

		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *	设置weboffice自带工具栏“打开文档”显示或隐藏
	 *
	 /****************************************************/
	weboffice_toolBar_open = function(flag) {
		try {
			var vCurItem = webObj.HideMenuItem(0);
			//根据vCurItem判断当前按钮是否显示
			if ((vCurItem & 0x02) && !flag) {
				webObj.HideMenuItem(0x02);
				//Show it
			} else {
				webObj.HideMenuItem(0x02 + 0x8000);
				//Hide it
			}

		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *	设置weboffice自带工具栏“保存文档”显示或隐藏
	 *
	 /****************************************************/
	weboffice_toolBar_save = function(flag) {
		try {
			var vCurItem = webObj.HideMenuItem(0);
			//根据vCurItem判断当前按钮是否显示
			if ((vCurItem & 0x04) && !flag) {
				webObj.HideMenuItem(0x04);
				//Show it
			} else {
				webObj.HideMenuItem(0x04 + 0x8000);
				//Hide it
			}

		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *		设置weboffice自带工具栏显示或隐藏
	 *
	 /****************************************************/
	weboffice_toolBar = function(flag) {
		try {
			webObj.ShowToolBar = flag;
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	//-----------------------------------------------office2003----------------------------------------------------------------------------
	/****************************************************
	 *
	 *					隐藏office2003文件菜单
	 *
	 /****************************************************/
	weboffice_hideFileMenu = function() {
		try {
			webObj.SetToolBarButton2("Menu Bar", 1, 0);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					显示office2003文件菜单
	 *
	 /****************************************************/
	weboffice_showFileMenu = function() {
		try {
			webObj.SetToolBarButton2("Menu Bar", 1, 4);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					隐藏office2003编辑菜单
	 *
	 /****************************************************/
	weboffice_hideEditMenu = function() {
		try {
			webObj.SetToolBarButton2("Menu Bar", 2, 0);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					显示office2003编辑菜单
	 *
	 /****************************************************/
	weboffice_showEditMenu = function() {
		try {
			webObj.SetToolBarButton2("Menu Bar", 2, 4);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					隐藏office2003新建按钮
	 *
	 /****************************************************/
	weboffice_hideNewItem = function() {
		try {
			webObj.SetToolBarButton2("Standard", 1, 0);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					显示office2003新建按钮
	 *
	 /****************************************************/
	weboffice_showNewItem = function() {
		try {
			webObj.SetToolBarButton2("Standard", 1, 4);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					隐藏office2003打开按钮
	 *
	 /****************************************************/
	weboffice_hideOpenItem = function() {
		try {
			webObj.SetToolBarButton2("Standard", 2, 0);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					显示office2003打开按钮
	 *
	 /****************************************************/
	weboffice_showOpenItem = function() {
		try {
			webObj.SetToolBarButton2("Standard", 2, 4);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					隐藏office2003保存按钮
	 *
	 /****************************************************/
	weboffice_hideSaveItem = function() {
		try {
			webObj.SetToolBarButton2("Standard", 1, 0);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					显示office2003保存按钮
	 *
	 /****************************************************/
	weboffice_showSaveItem = function() {
		try {
			webObj.SetToolBarButton2("Standard", 1, 4);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	//---------------------------------------------------------------------------------------------------------------------------

	//------------------------------------------------office 2007----------------------------------------------------------------
	/****************************************************
	 *
	 *			Office2007菜单隐藏和恢复
	 *			----开始菜单隐藏
	 *
	 /****************************************************/
	weboffice_beginMenu = function() {
		try {
			webObj.HideMenuAction(1, 0x100000);
			webObj.HideMenuAction(5, 0);
			//激活设置
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *			Office2007菜单隐藏和恢复
	 *			---插入菜单隐藏
	 *
	 /****************************************************/
	weboffice_insertMenu = function() {
		try {
			webObj.HideMenuAction(1, 0x200000);
			webObj.HideMenuAction(5, 0);
			//激活设置

		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *			Office2007菜单隐藏和恢复
	 *			---页面菜单隐藏
	 *
	 /****************************************************/
	weboffice_pageMenu = function() {
		try {
			webObj.HideMenuAction(1, 0x400000);
			webObj.HideMenuAction(5, 0);
			//激活设置

		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *			Office2007菜单隐藏和恢复
	 *			--引用菜单隐藏
	 *
	 /****************************************************/
	weboffice_adducMenu = function() {
		try {
			webObj.HideMenuAction(1, 0x800000);
			webObj.HideMenuAction(5, 0);
			//激活设置

		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *			Office2007菜单隐藏和恢复
	 *			---邮件菜单隐藏
	 *
	 /****************************************************/
	weboffice_emailMenu = function() {
		try {
			webObj.HideMenuAction(1, 0x1000000);
			webObj.HideMenuAction(5, 0);
			//激活设置
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *			Office2007菜单隐藏和恢复
	 *			---审阅菜单隐藏
	 *
	 /****************************************************/
	weboffice_checkMenu = function() {
		try {
			webObj.HideMenuAction(1, 0x2000000);
			webObj.HideMenuAction(5, 0);
			//激活设置
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *			Office2007菜单隐藏和恢复
	 *			---视图菜单隐藏
	 *
	 /****************************************************/
	weboffice_viewMenu = function() {
		try {
			webObj.HideMenuAction(1, 0x4000000);
			webObj.HideMenuAction(5, 0);
			//激活设置
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *			Office2007菜单隐藏和恢复
	 *			---开发工具菜单隐藏
	 *
	 /****************************************************/
	weboffice_empolderMenu = function() {
		try {
			webObj.HideMenuAction(1, 0x8000000);
			webObj.HideMenuAction(5, 0);
			//激活设置
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *			Office2007菜单隐藏和恢复
	 *			---加载项菜单隐藏
	 *
	 /****************************************************/
	weboffice_loadMenu = function() {
		try {
			webObj.HideMenuAction(1, 0x10000000);
			webObj.HideMenuAction(5, 0);
			//激活设置
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *			Office2007菜单隐藏和恢复
	 *			---全部菜单隐藏
	 *
	 /****************************************************/
	weboffice_allHideMenu = function() {
		try {
			webObj.HideMenuAction(1, 0x100000 + 0x200000 + 0x400000 + 0x800000 + 0x1000000 + 0x2000000 + 0x4000000 + 0x8000000 + 0x10000000);
			webObj.HideMenuAction(5, 0);
			//激活设置
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *			Office2007菜单隐藏和恢复
	 *			---复制无效
	 *
	 /****************************************************/
	weboffice_nullityCopy = function() {
		try {
			webObj.HideMenuAction(1, 0x2000);
			webObj.HideMenuAction(5, 0);
			//激活设置
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *			Office2007菜单隐藏和恢复
	 *			---粘贴无效
	 *
	 /****************************************************/
	weboffice_nullityAffix = function() {
		try {
			webObj.HideMenuAction(1, 0x1000);
			webObj.HideMenuAction(5, 0);
			//激活设置
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *			Office2007菜单隐藏和恢复
	 *	---恢复至设置之前状态（菜单显示，复制，粘贴可用）
	 *
	 /****************************************************/
	weboffice_affixCopy = function() {
		try {
			webObj.HideMenuAction(6, 0);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *	---电子印章
	 *
	 /****************************************************/

	weboffice_hideSeal = function() {
		var obj;
		try {
			obj = new Object(webObj.GetDocumentObject());
			if (obj != null) {
				obj.Application.CommandBars("电子印章").Visible = !obj.CommandBars("电子印章").Visible;

			}
			delete obj;
		} catch(e) {
			alert("隐藏显示印章工具栏出错");
		}
	};

	weboffice_write2 = function() {
		var obj1;
		try {
			obj1 = new Object(webObj.GetDocumentObject());
			if (obj1 != null) {
				obj1.Application.CommandBars("电子印章").Controls("盖章").Execute();

			}
			delete obj1;
		} catch(e) {
			alert("盖章出错");
		}
	};

	//---------------------------------------------------------------------------------------------------------------------------

	/****************************************************
	 *
	 *					隐藏菜单
	 *
	 /****************************************************/
	weboffice_notMenu = function() {
		try {
			webObj.SetToolBarButton2("Menu Bar", 1, 8);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					显示菜单
	 *
	 /****************************************************/
	weboffice_okMenu = function() {
		try {
			webObj.SetToolBarButton2("Menu Bar", 1, 11);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					隐藏常用工具栏
	 *
	 /****************************************************/
	weboffice_notOfter = function() {
		try {
			webObj.SetToolBarButton2("Standard", 1, 8);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					显示常用工具栏
	 *
	 /****************************************************/
	weboffice_okOfter = function() {
		try {
			webObj.SetToolBarButton2("Standard", 1, 11);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					隐藏格式工具栏
	 *
	 /****************************************************/
	weboffice_notFormat = function() {
		try {
			webObj.SetToolBarButton2("Formatting", 1, 8);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};

	/****************************************************
	 *
	 *					显示格式工具栏
	 *
	 /****************************************************/
	weboffice_okFormat = function() {
		try {
			webObj.SetToolBarButton2("Formatting", 1, 11);
		} catch(e) {
			alert("异常\r\nError:" + e + "\r\nError Code:" + e.number + "\r\nError Des:" + e.description);
		}
	};
	/****************************************************
	 *
	 *	---隐藏office菜单或功能区
	 *
	 /*****************************************************/

	weboffice_hideAll = function(pcExcludeBar1, pcExcludeBar2, pcExcludeBar3, pcExcludeBar4) {
		webObj.HideMenuArea(pcExcludeBar1, pcExcludeBar2, pcExcludeBar3, pcExcludeBar4);
	};

	/****************************************************
	 *
	 *		控件初始化WebOffice方法
	 *
	 ****************************************************/
	weboffice_notifyCtrlReady = function() {
		webObj.SetWindowText("Rosten恒传技术", 0);
		webObj.OptionFlag |= 128;
		// 新建文档
		 weboffice_newDoc();
		 webObj.ShowToolBar = false;   //隐藏weboffice自带工具栏
		 //默认隐藏office工具栏
		 webObj.HideMenuArea("hideall","","","");
		 rosten.variable.wordMenu = false;
	};

	weboffice_saveBinaryFileFromBase64 = function() {
		var tempPath = webObj.GetTempFilePath();
		//获取临时文件路径
		var v = webObj.GetFileBase64("",0);
		webObj.SaveBinaryFileFromBase64(tempPath, v);

	};

	/****************************************************
	 *
	 *	---自定义工具栏添加按钮
	 *
	 /*****************************************************/
	weboffice_setCustomToolBtn = function(index, name) {
		webObj.SetCustomToolBtn(index, name);

	};
	/****************************************************
	 *
	 *	---WebOffice工具栏事件处理函数
	 *
	 /*****************************************************/
	weboffice_notifyToolBarClick = function(iIndex) {
		//alert(iIndex+"新加按钮触发的事件可在这里写自己的功能");
	};
	
	return weboffice;
});
