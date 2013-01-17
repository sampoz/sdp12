<html>
<head>
</head>
<body>
<%
	session.invalidate();
	
	response.sendRedirect("./login.jsf");
%>
</body>
</html>

