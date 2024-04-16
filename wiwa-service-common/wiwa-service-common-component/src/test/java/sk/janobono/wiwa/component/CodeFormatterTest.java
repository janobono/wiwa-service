package sk.janobono.wiwa.component;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {CodeFormatter.class}
)
class CodeFormatterTest {

    private static final String PREFIX = "CL";
    private static final int LENGTH = 19;
    private static final long NUMBER = 1L;
    private static final String RESULT = "CL0000000000000000001";

    @Autowired
    public CodeFormatter codeFormatter;

    @Test
    void formatPrefixAndLengthAndNumber() {
        assertThat(codeFormatter.format(PREFIX, LENGTH, NUMBER)).isEqualTo(RESULT);
    }

    @Test
    void formatPrefixAndNumber() {
        assertThat(codeFormatter.format(PREFIX, NUMBER)).isEqualTo(RESULT);
    }
}
