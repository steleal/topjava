### Test MealRestController

####  Create a meal
```
curl -X POST -H 'Content-Type:application/json;charset=UTF-8' \
-d '{"dateTime":"2023-03-28T11:00","description":"Breakfest","calories":555}' \
http://localhost:8080/topjava/rest/meals
```

####  Get a meal by id
`curl http://localhost:8080/topjava/rest/meals/100003`

####  Update a meal by id
```
curl -X PUT -H 'Content-Type:application/json;charset=UTF-8' \
-d '{"id":100003,"dateTime":"2020-01-30T10:00","description":"Updated breakfest","calories":555}' \
http://localhost:8080/topjava/rest/meals/100003
```

####  Delete a meal by id
`curl -X DELETE http://localhost:8080/topjava/rest/meals/100003`

####  Get all meals
`curl http://localhost:8080/topjava/rest/meals`

####  Get meals between
`curl 'http://localhost:8080/topjava/rest/meals/filter?startDate=2020-01-31&endDate=2020-01-31'`
