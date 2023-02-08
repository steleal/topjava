<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="css/style.css">
    <title>Meals</title>
    <jsp:useBean id="dtFormatter" type="java.time.format.DateTimeFormatter" scope="request"/>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<table>
    <tr>
        <th>DateTime</th>
        <th>Description</th>
        <th>Calories</th>
        <th> </th>
        <th> </th>
    </tr>
    <c:forEach items="${mealsTo}" var="mealTo">
        <jsp:useBean id="mealTo" type="ru.javawebinar.topjava.dto.MealTo"/>
        <tr data-meal-excess = ${mealTo.excess}>
            <td>${mealTo.dateTime.format(dtFormatter)}</td>
            <td>${mealTo.description}</td>
            <td>${mealTo.calories}</td>
            <td><a href="meals?action=edit&id=${mealTo.id}">Update</a></td>
            <td><a href="meals?action=delete&id=${mealTo.id}">Delete</a></td>
        </tr>
    </c:forEach>
    <p><a href="meals?action=add">Add Meal</a></p>
</table>
</body>
</html>
