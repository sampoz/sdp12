<html>
<head>
</head>
<body>
<%
	session.invalidate();
	
	response.sendRedirect("./ui.jsf");
%>
</body>
</html>

