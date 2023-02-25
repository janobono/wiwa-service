package sk.janobono.wiwa.common.component;

import org.springframework.stereotype.Component;

@Component
public class CodeFormatter {

    public String format(String prefix, Long l) {
        return format(prefix, 19, l);
    }

    public String format(String prefix, int length, long l) {
        return prefix + String.format("%0" + length + "d", l);
    }
}
