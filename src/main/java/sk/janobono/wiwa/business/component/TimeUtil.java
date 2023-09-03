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

    public boolean isAfterOrEquals(final LocalDate date1, final LocalDate date2) {
        return date1.isAfter(date2) || date1.isEqual(date2);
    }

    public boolean isBeforeOrEquals(final LocalDate date1, final LocalDate date2) {
        return date1.isBefore(date2) || date1.isEqual(date2);
    }
}
