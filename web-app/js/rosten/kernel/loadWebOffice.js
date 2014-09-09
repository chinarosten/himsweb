var s = "";
var checkIsIE = function(){
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

if(checkIsIE()){
	//ie浏览器
	s += "<object id=WebOffice1 height=768 width='100%' style='LEFT: 0px; TOP: 0px'  classid='clsid:E77E049B-23FC-4DB8-B756-60529A35FAD5' codebase='weboffice_v6.0.5.0.cab#Version=6,0,5,0'>";
	s +="<param name='_ExtentX' value='6350'><param name='_ExtentY' value='6350'>";
	s +="</OBJECT>";
}else{
	s += '<object  id="Control" TYPE="application/x-itst-activex" ALIGN="baseline" BORDER="0" WIDTH="100%"HEIGHT="768px"';
	s += 'clsid="{E77E049B-23FC-4DB8-B756-60529A35FAD5}" event_NotifyCtrlReady="NotifyCtrlReady" event_NotifyToolBarClick="NotifyToolBarClick" event_NotifyWordEvent="NotifyWordEvent">';
	s += '</object>';
}
document.write(s)