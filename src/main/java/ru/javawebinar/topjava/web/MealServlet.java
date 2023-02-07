package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.CrudDao;
import ru.javawebinar.topjava.dao.MemoryCrudDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int CALORIES_PER_DAY = 2000;
    private static final Logger log = getLogger(MealServlet.class);

    private CrudDao<Meal> mealDao;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        mealDao = new MemoryCrudDao();
        MealsUtil.getStartedMealList().forEach(mealDao::add);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("doGet");

        String action = request.getParameter("action");
        if (action == null) {
            forwardToMeals(request, response);
            return;
        }

        int id;
        switch (action) {
            case "delete":
                id = Integer.parseInt(request.getParameter("id"));
                mealDao.delete(id);
                response.sendRedirect("meals");
                break;
            case "add":
                forwardToMealEdit(request, response, MealTo.EMPTY);
                break;
            case "edit":
                id = Integer.parseInt(request.getParameter("id"));
                MealTo mealTo = MealsUtil.createTo(mealDao.getById(id), false);
                forwardToMealEdit(request, response, mealTo);
                break;
            default:
                forwardToMeals(request, response);
                break;
        }
    }

    private void forwardToMealEdit(HttpServletRequest request, HttpServletResponse response, MealTo mealTo) throws ServletException, IOException {
        log.debug("forward to meal-edit with mealTo: {}", mealTo);
        request.setAttribute("mealTo", mealTo);
        request.getRequestDispatcher("/meal-edit.jsp").forward(request, response);
    }

    private void forwardToMeals(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("list All");
        request.setAttribute("mealsTo", getMealsTo());
        request.setAttribute("dtFormatter", DATE_TIME_FORMATTER);
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }

    private List<MealTo> getMealsTo() {
        log.debug("getMealsTo");
        List<Meal> meals = mealDao.getAll();
        return MealsUtil.toMealsTo(meals, CALORIES_PER_DAY);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("doPost");
        request.setCharacterEncoding("UTF-8");

        String strId = request.getParameter("id");
        String strDateTime = request.getParameter("dateTime");
        String description = request.getParameter("description");
        String strCalories = request.getParameter("calories");
        log.debug("doPost params: id {}, dateTime {}, description {}, calories {}",
                strId, strDateTime, description, strCalories);

        int id = strId == null || strId.isEmpty() ? 0 : Integer.parseInt(strId);
        LocalDateTime dateTime = LocalDateTime.parse(strDateTime);
        int calories = Integer.parseInt(strCalories);

        final boolean isCreate = id == 0;
        Meal meal = new Meal(id, dateTime, description, calories);
        if (isCreate) {
            mealDao.add(meal);
        } else {
            mealDao.update(meal);
        }
        response.sendRedirect("meals");
    }
}
