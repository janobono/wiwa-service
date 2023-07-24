package sk.janobono.wiwa.component;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Path;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {LocalStorage.class}
)
class LocalStorageTest {

    @Autowired
    public LocalStorage localStorage;

    @Test
    void fullTest() throws Exception {
        final Path src = Path.of(Objects.requireNonNull(LocalStorageTest.class.getResource("/Debian_logo_01.png")).toURI());
        final Path target = localStorage.createTempFile("Test", ".png");

        localStorage.copy(src, target);
        assertThat(localStorage.getFileData(src)).isEqualTo(localStorage.getFileData(target));

        localStorage.write(target, localStorage.getFileData(src));
        assertThat(localStorage.getFileData(src)).isEqualTo(localStorage.getFileData(target));

        final Path dir = localStorage.createDirectory("test", "01");
        localStorage.delete(dir);
    }
}
