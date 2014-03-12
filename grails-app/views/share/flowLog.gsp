<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>flowLog</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>
<style type="text/css">
</style>

<body>
	<div class="flowLog">
		<table>
			<tbody>
				<g:each in="${log}">
					<tr>
						<th>${it.getFormattedCreatedDate()}</th>
   						<td>${it.user.getFormattedName() + " —> " + it.content}</td>
   					</tr>
				</g:each>
			</tbody>
		</table>
	</div>
</body>
</html>
