package sk.janobono.wiwa.component;

import org.springframework.stereotype.Component;

@Component
public class CodeFormatter {

    public String format(final String prefix, final Long l) {
        return format(prefix, 19, l);
    }

    public String format(final String prefix, final int length, final long l) {
        return prefix + String.format("%0" + length + "d", l);
    }
}
