package sk.janobono.wiwa.common.component;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {RandomString.class}
)
class RandomStringTest {

    @Autowired
    public RandomString randomString;

    @Test
    void generateWrongLengthSettings() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> {
                    randomString.alphaNumeric(0, 0, 0, 5, 2);
                }
        );
    }

    @Test
    void generateWrongMinimalsSettings() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> {
                    randomString
                            .alphaNumeric(2, 5, 3, 8, 8);
                }
        );
    }

    @Test
    void generateWrongMinimalsSettings01() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> {
                    randomString
                            .alphaNumeric(2, 5, 3, 5, 8);
                }
        );
    }

    @Test
    void generateNumeric() {
        String generated = randomString.numeric(20);
        assertThat(generated.length()).isEqualTo(20);
        assertThat(generated).containsPattern("(%d)*");
    }

    @Test
    void generateAlfa() {
        String generated = randomString.alphabet(20);
        assertThat(generated.length()).isEqualTo(20);
        assertThat(generated).containsPattern("[a-z, A-Z]*");
    }

    @Test
    void generateAlfaNumeric() {
        String generated = randomString.alphaNumeric(20);
        assertThat(generated.length()).isEqualTo(20);
        assertThat(generated).containsPattern("[a-z, A-Z, 0-9]*");
    }

    @Test
    void generateAlfaNumericWithSpecials() {
        String generated = randomString.alphaNumericWithSpecial(20);
        assertThat(generated.length()).isEqualTo(20);
    }

    @Test
    void generateConcrete() {
        String generated = randomString.alphaNumeric(5, 10, 5, 20, 20);
        assertThat(generated.length()).isEqualTo(20);

        int numbers = 0;
        int characters = 0;
        int capitals = 0;
        char[] chars = generated.toCharArray();
        for (char c : chars) {
            if (c >= 'A' && c <= 'Z') {
                capitals++;
            } else if (c >= 'a' && c <= 'z') {
                characters++;
            } else if (c >= '0' && c <= '9') {
                numbers++;
            }
        }
        assertThat(numbers).isEqualTo(5);
        assertThat(characters).isEqualTo(10);
        assertThat(capitals).isEqualTo(5);
    }

    @Test
    void generateBetween() {
        String generated = randomString.alphaNumeric(5, 10, 5, 50, 200);
        assertThat(generated.length()).isBetween(50, 200);

        int numbers = 0;
        int characters = 0;
        int capitals = 0;
        char[] chars = generated.toCharArray();
        for (char c : chars) {
            if (c >= 'A' && c <= 'Z') {
                capitals++;
            } else if (c >= 'a' && c <= 'z') {
                characters++;
            } else if (c >= '0' && c <= '9') {
                numbers++;
            }
        }
        assertThat(numbers >= 5).isTrue();
        assertThat(characters >= 10).isTrue();
        assertThat(capitals >= 5).isTrue();
    }
}
