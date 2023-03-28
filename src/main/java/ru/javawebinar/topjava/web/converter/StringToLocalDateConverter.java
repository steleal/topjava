package ru.javawebinar.topjava.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDate;

@Component
public class StringToLocalDateConverter implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(@Nullable String strDate) {
        return DateTimeUtil.parseLocalDate(strDate);
    }
}
