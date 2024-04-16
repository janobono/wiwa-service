package sk.janobono.wiwa.component;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Component
public class TimeUtil {

    public ZonedDateTime toZonedDateTime(final LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(v -> v.atZone(ZoneId.systemDefault())).orElse(null);
    }

    public LocalDateTime toLocalDateTime(final ZonedDateTime zonedDateTime) {
        return Optional.ofNullable(zonedDateTime).map(ZonedDateTime::toLocalDateTime).orElse(null);
    }
}
