package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.config.AppConfig;
import ru.javawebinar.topjava.dao.CrudDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.config.AppConfig.CALORIES_PER_DAY;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private CrudDao<Meal> mealDao;
    private DateTimeFormatter dateTimeFormatter;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        AppConfig appConfig = AppConfig.getInstance();
        mealDao = appConfig.getMealDao();
        dateTimeFormatter = appConfig.getDateTimeFormatter();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("forward to meals");
        List<Meal> meals = mealDao.getAll();
        meals.sort(Comparator.comparing(Meal::getDateTime));
        List<MealTo> mealsTo = MealsUtil.toMealsTo(meals, CALORIES_PER_DAY);
        request.setAttribute("mealsTo", mealsTo);
        request.setAttribute("dtFormatter", dateTimeFormatter);
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }
}
