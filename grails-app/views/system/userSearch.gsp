<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<style type="text/css">
	
</style>
<script type="text/javascript">	

</script>
</head>
<body>
	<div class="searchtab">
      <table width="100%" border="0">
        
        <tbody>
          <tr>
            <th width="5%">登录名</th>
            <td width="18%">
            	<input id="s_username" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true
               '/>
            </td>
            <th width="5%">中文名</th>
            <td width="18%">
            	<input id="s_chinaName"  data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true
               '/>
            </td>
            <th width="5%">手机号</th>
            <td width="18%">
            	<input id="s_telephone"  data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true
               '/>
            
            </td>
            <td>
            	<div class="btn">
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){user_search()}'>查询</button>
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){user_resetSearch()}'>重置条件</button>
              	</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
	
</body>
</html>
