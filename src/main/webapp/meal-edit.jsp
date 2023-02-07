<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="css/style.css">
    <title>Meals</title>
    <jsp:useBean id="dtFormatter" type="java.time.format.DateTimeFormatter" scope="request"/>
    <jsp:useBean id="mealTo" type="ru.javawebinar.topjava.model.MealTo" scope="request"/>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit Meal</h2>
<form method="post" action="meals" enctype="application/x-www-form-urlencoded">
    <input type="hidden" name="id" value="${mealTo.id}">
    <div>
        <label for="dateTime">DateTime</label>
        <input type="datetime-local" id="dateTime" name="dateTime" value="${mealTo.dateTime.format(dtFormatter)}">
    </div>
    <div>
        <label for="description">Description</label>
        <input type="text" id="description" name="description" value="${mealTo.description}" placeholder="Description">
    </div>
    <div>
        <label for="calories">Calories</label>
        <input type="number" id="calories" name="calories" value="${mealTo.calories}" placeholder="1000" min="0" max="10000">
    </div>
    <button type="submit">Save</button>
    <button type="button" onclick="window.history.back()">Cancel</button>
</form>
</body>
</html>
