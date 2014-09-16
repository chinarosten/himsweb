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
            <th width="5%">类型</th>
            <td width="18%">
            	<select id="s_purpose" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:""
	            '>
					<option value="支出">支出</option>
					<option value="收入">收入</option>
	    		</select>
            </td>
            <th width="5%">项目名称</th>
            <td width="18%">
            	<select id="s_project" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:""
	            '>
					<g:each in="${projectList}" var="item">
                    	<option value="${item.id }">${item.name }</option>
                    </g:each>
	    		</select>
            </td>
            <th width="5%">用途</th>
            <td width="18%">
            	<select id="s_category" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:""
	            '>
					<g:each in="${cagetoryList}" var="item">
                    	<option value="${item.id }">${item.name }</option>
                    </g:each>
	    		</select>
            
            </td>
            <td>
            	<div class="btn">
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){account_search()}'>查询</button>
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){account_resetSearch()}'>重置条件</button>
              	</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
	
</body>
</html>
