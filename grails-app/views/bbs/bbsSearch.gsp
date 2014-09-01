<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<style type="text/css">
	.searchtab { width:100%; margin-bottom:2px; }
	.searchtab table { border-collapse:collapse; width: 100%; border:#dbdbdb 1px solid; border-bottom:none; text-align:left; }
	.searchtab table tr { background:#fff; }
	.searchtab table td, .searchtab table th { border-bottom: 1px solid #CED2D6; color: #000; vertical-align:middle; word-break: keep-all; }
	.searchtab table td { padding:4px 3px 4px 5px; }
	.searchtab table th { font-weight:normal; text-align:right; padding:4px 5px 4px 5px; }
	.searchtab tbody input { border:1px solid #B9CDE7; }
	.searchtab table tfoot tr { background:#EFEFEF; }
	.searchtab table .bz { float:left; }
	.searchtab table .btn { float:left; }
</style>
<script type="text/javascript">	
require([ "dijit/registry"],
	function(registry){
	
	closesearch = function(){
		var content = {};

		var serialNo = registry.byId("s_serialno");
		if(serialNo.get("value")!=""){
			content.serialNo = serialNo.get("value");
		}
		
		var topic = registry.byId("s_topic");
		if(topic.get("value")!=""){
			content.topic = topic.get("value");
		}

		var count = Object.keys(content).length
		alert(count);
		if(count==0) return ;
		
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		rosten.kernel.refreshGrid(rosten.webPath + "/bbs/bbsGrid?companyId=" + companyId + "&userId=" + userid + "&type=person", content);
	}

});
</script>
</head>
<body>
	<div class="searchtab">
      <table width="100%" border="0">
        
        <tbody>
          <tr>
            <th width="5%">流水号</th>
            <td width="18%">
            	<input id="s_serialno" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='"class":"input",trim:true,
					value:""
               '/>
            </td>
            <th width="5%">主题</th>
            <td width="18%">
            	<input id="s_topic"  data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='"class":"input",
					value:""
               '/>
            </td>
            <th width="5%">状态</th>
            <td width="18%"><select name="select3" id="select3" style="width:134px">
              <option>新建</option>
              <option>计算机</option>
              <option>电信工程</option>
              <option>文学院</option>
            </select></td>
            <td>
            	<div class="btn">
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){search()}'>查询</button>
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){closesearch()}'>重置条件</button>
              	</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
	
</body>
</html>
