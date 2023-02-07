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
import java.util.Comparator;
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
        request.setCharacterEncoding("UTF-8");

        String strId = request.getParameter("id");
        Integer id = strId == null ? null : Integer.parseInt(strId);
        String action = request.getParameter("action");
        log.debug("doGet params: id = {}, action= {}", strId, action);

        request.setAttribute("dtFormatter", DATE_TIME_FORMATTER);
        if (action == null) {
            log.debug("list All");
            request.setAttribute("mealsTo", getMealsTo());
            request.setAttribute("dtFormatter", DATE_TIME_FORMATTER);
            request.getRequestDispatcher("/meals.jsp").forward(request, response);
            return;
        }

        MealTo mealTo;
        switch (action) {
            case "delete":
                mealDao.delete(id);
                response.sendRedirect("meals");
                return;
            case "add":
                mealTo = MealTo.EMPTY;
                break;
//            case "view":
            case "edit":
                mealTo = MealsUtil.createTo(mealDao.getById(id), false);
                break;
            default:
                String errorMessage = "Action " + action + "is illegal";
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
        }

        log.debug("forward to meal-edit with mealTo: {}", mealTo);
        request.setAttribute("mealTo", mealTo);
        request.getRequestDispatcher("/meal-edit.jsp").forward(request, response);
    }

    private List<MealTo> getMealsTo() {
        log.debug("getMealsTo");
        List<Meal> meals = mealDao.getAll();
        meals.sort(Comparator.comparing(Meal::getDateTime));
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
