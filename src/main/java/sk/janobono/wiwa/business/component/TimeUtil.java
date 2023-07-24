package sk.janobono.wiwa.business.component;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class TimeUtil {

    public ZonedDateTime toZonedDateTime(final LocalDate localDate) {
        return localDate.atStartOfDay(ZoneId.systemDefault());
    }

    public LocalDate toLocalDate(final ZonedDateTime zonedDateTime) {
        return zonedDateTime.toLocalDate();
    }
}
