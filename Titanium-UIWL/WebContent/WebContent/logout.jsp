<html>
<head>
</head>
<body>
<%
	session.invalidate();
	
	response.sendRedirect("./second.jsf");
%>
</body>
</html>

