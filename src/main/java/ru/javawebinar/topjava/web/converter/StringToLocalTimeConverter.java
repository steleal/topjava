package ru.javawebinar.topjava.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalTime;

@Component
public class StringToLocalTimeConverter implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(@Nullable String strTime) {
        return DateTimeUtil.parseLocalTime(strTime);
    }
}
