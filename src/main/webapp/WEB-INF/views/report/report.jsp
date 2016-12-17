<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Report panel</title>
</head>
<body>
<p align="center"><a href="/main.jsp">Main menu</a>
<p align="right">You are logged in as <b>${userLoggedIn}</b>
    <a href="/login?logout">logout</a></p>

<hr>
<a href="/generateDailyReport">Generate daily report</a><br>
<a href="/generateWeeklyReport">Generate weekly report</a><br>
<a href="/generateMonthlyReport">Generate monthly report</a><br>
<hr>

</body>
</html>
