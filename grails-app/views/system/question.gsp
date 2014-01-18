<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>你问问答</title>
    
    
    <g:if test="${question && question.id!=null}">
    	 <meta name="layout" content="rosten" />
    </g:if>
	<script type="text/javascript">

		require([
				"dojo/_base/kernel",
		 		"dijit/registry",
		     	"rosten/widget/ActionBar",
				"dijit/form/ValidationTextBox",
				"dijit/form/SimpleTextarea",
				"dijit/form/DateTextBox",
				"dijit/form/Button",
				"rosten/app/SystemApplication",
				"rosten/app/QuestionManage"
		     	],function(kernel,registry){

			<g:if test="${question && question.id!=null}">
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}"});
					rosten.cssinit();
				});
			 </g:if>

		});	
    </script>
</head>
<body>
	<g:if test="${question && question.id!=null}">
		<div class="rosten_action">
			<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'systemAction',action:'questionForm',params:[userid:user?.id])}"'></div>
		</div>
	</g:if>
	<div style="text-Align:center">
	    <div class="rosten_form" style="width:550px">
	    	<input style="display:none" data-dojo-type="dijit/form/ValidationTextBox" id="unid" name="unid" value = "${question?.id }"></input>
	        <fieldset class="fieldset-form">
	            <legend class="tableHeader">你问我答
	            </legend>
	            <table width="500" border="0">
				<tr>
				    <td width="110">问题标题：</td>
				    <td>
				    	<input id="questionTitle" class="input" type="text" style="width: 390px;" promptMessage="请正确输入标题..."
				    		value = "${question?.questionTitle }"
                            data-dojo-type="dijit/form/ValidationTextBox" trim="true" required="true" />
				    </td>
				  </tr>
				  <tr>
				    <td width="110">问题描述：</td>
				    <td ><textarea id="question" name="question" rows="4" dojoType="dijit/form/SimpleTextarea" 
				    	value = "${question?.question }"
				    	trim="true" required="true" style="width:390px;height:78px" ></textarea></td>
				  </tr>
 					
 				  <tr>
				    <td width="110">提问时间：</td>
				    <td style="text-align:left">
				    	<input id="questionTime" name="questionTime" value = "${question?.getFormattedCreateDate() }"data-dojo-type="dijit/form/DateTextBox" required="true" trim="true" />
				    </td>
				  </tr>
				  <g:if test="${question && question.id!=null}">
				  
					  <tr id="isAnswerRow" >
					    <td width="110">是否已解决：</td>
					    <td style="text-align:left">
					    	<select id="isAnswer" name="isAnswer" data-dojo-type="dijit/form/FilteringSelect"
								style="width: 194px;font-size:13px;" value="${question?.getIsAnswerValue() }"
								autoComplete="false" >	
								
								<option value="是">是</option>
								<option value="否" selected>否</option>					
							</select>	
					    </td>
					  </tr>
					  <tr id="questionAnswerRow">
					    <td width="110">问题解决：</td>
					    <td ><textarea id="questionAnswer" name="questionAnswer" data-dojo-type="dijit/form/SimpleTextarea" value="${question?.questionAnswer }"
					    	rows="4" style="width:390px;height:78px" ></textarea></td>
					  </tr>
				</g:if>
				<g:else>
				  <tr id="buttonRow">
				    <td>&nbsp;</td>
				    <td style="text-align:left"><button onClick="addQuestion()" data-dojo-type="dijit/form/Button">提交</button>
				    	<button onClick="rosten.kernel.hideRostenShowDialog()" data-dojo-type="dijit/form/Button">取消</button>
				    </td>
				  </tr>
				</g:else>
				</table>
	        </fieldset>
		</div>
	</div>
</body>
</html>