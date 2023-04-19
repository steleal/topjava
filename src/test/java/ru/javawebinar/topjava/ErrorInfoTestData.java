package ru.javawebinar.topjava;

import ru.javawebinar.topjava.util.exception.ErrorInfo;

public class ErrorInfoTestData {
    public static final MatcherFactory.Matcher<ErrorInfo> ERROR_WITHOUT_DETAIL_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(ErrorInfo.class, "detail");
    public static final MatcherFactory.Matcher<ErrorInfo> ERROR_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(ErrorInfo.class);
}
