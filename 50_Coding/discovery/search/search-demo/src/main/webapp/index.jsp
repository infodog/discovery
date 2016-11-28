<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<link type="text/css"
	href="jquery-ui-1-3/css/smoothness/jquery-ui-1.8.18.custom.css"
	rel="stylesheet" />

<script type="text/javascript"
	src="jquery-ui-1-3/js/jquery-1.7.1.min.js">
	
</script>
<script type="text/javascript"
	src="jquery-ui-1-3/js/jquery-ui-1.8.18.custom.min.js">
	
</script>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>HEKATE</title>
</head>
<body>

	<script type="text/javascript">
		$(document).ready(
				function() {
					
					$("#keyword").autocomplete({
								source: function(request, response) {
									  $.post( "recommend/autoSuggest", { term: request.term }, function( data ) {
									   response(data)
									  }
									  , 'json'); 
									 },
								minLength : 1,
								select: function( event, ui ) {
									$("#search").submit();
									return false;
								}
								
					}).data( "autocomplete" )._renderItem = function( ul, item ) {
						return $( "<li></li>" )
						.data( "item.autocomplete", item )
						.append( "<a>" + item.value + "<span style='float:right;color:red'>约" + item.num + "条</span>" + "</a>" )
						.appendTo( ul );
				};
		});
	</script>

	<br>
	<br>
	<br>
	<br>

	<center>
		<h1>HEKATE</h1>
	</center>

	<br>
	<br>


	<center>
		<form id="search" action="recommend/search" method="post">
			<input type="text" id="keyword" name="keyword" size="77" /> <br>
			<br> <input type="submit" value="search" />
		</form>
	</center>
</body>
</html>