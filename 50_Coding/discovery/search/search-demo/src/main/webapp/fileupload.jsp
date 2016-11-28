<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href="js/uploadify-v2/uploadify.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="js/uploadify-v2/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/uploadify-v2/swfobject.js"></script>
<script type="text/javascript"
	src="js/uploadify-v2/jquery.uploadify.v2.1.4.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$('#file_upload').uploadify({
			'uploader' : 'js/uploadify-v2/uploadify.swf',
			'script' : 'recommend/upload', 
			'cancelImg' : 'js/uploadify-v2/cancel.png',
			'folder' : '/uploads',
			'scriptData'  : {'merchantId':'head_mechant','fileColumnId':'root'},
			'auto' : true
		});
	});
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>HEKATE file upload</title>
</head>
<body>

	
	<br>
	<br>
	<br>
	<br>

	<center>
		<h1>HEKATE file upload</h1>
	</center>

	<br>
	<br>

	<input id="file_upload" name="file_upload" type="file" />
</body>
</html>