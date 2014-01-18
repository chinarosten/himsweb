/**
 * @author rosten
 */
define(["dojo/dom",
        "dijit/registry",
        "rosten/widget/PickTreeDialog",
        "rosten/kernel/behavior"], function(dom,registry,PickTreeDialog) {
    
    
    selectSmsGroup = function(url) {
        var id = "sys_smsGroupDialog";
        var initValue = [];
        var allowgroupsName = registry.byId("sms_Group");
        if (allowgroupsName.attr("value") != "") {
            initValue.push(allowgroupsName.attr("value"));
        }
        rosten.selectDialog("群组选择", id, url, true, initValue);
        rosten[id].callback = function(data) {
            var _data = "";
            for (var k = 0; k < data.length; k++) {
                var item = data[k];
                if (_data == "") {
                    _data += item.name;
                } else {
                    _data += "," + item.name;
                }
            }
            registry.byId("sms_Group").attr("value", _data);
        };
    };
    sendsms = function(){
        var content = {};
        var isNext = false;
        var telephone = registry.byId("sms_telephone");
        if(telephone.attr("value")!=""){
            content.telephone = telephone.attr("value");
            isNext = true;
        }
        var smsGroup = registry.byId("sms_Group");
        if(smsGroup.attr("value")!=""){
            content.smsGroup = smsGroup.attr("value");
            isNext = true;
        }
        if(isNext==false){
            rosten.alert("手机号码,短信群组不能为空！");
            return;
        }
        var contentNode = registry.byId("content");
        if(contentNode.attr("value")!=""){
            content.content = contentNode.attr("value");
        }else{
            rosten.alert("发送内容不能为空！");
            return;
        }
        rosten.readSync(rosten.webPath + "/system/smsSave",content,function(data){
            if(data.result=="nouse"){
                rosten.alert("短消息功能尚未开通，请联系厂家！");
            }else if(data.result == "error"){
                rosten.alert("系统出错，请联系厂家！");
            }else{
                rosten.alert("发送成功！");
                rosten.kernel.hideRostenShowDialog();
            }
            
        });
        
    };

});
