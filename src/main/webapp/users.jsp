<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<head>
    <title>Users</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Users</h2>
<form method="post" action="users">
    <div>
        <select name="userId">
            <option disabled>Select user</option>
            <option value="1" ${userId == '1' ? 'selected' : ''}>User</option>
            <option value="2" ${userId == '2' ? 'selected' : ''}>Admin</option>
        </select>
    </div>
    <br>
    <div>
        <input type="submit" value="Отправить">
    </div>
</form>
</body>
</html>
