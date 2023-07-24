package sk.janobono.wiwa.component;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Path;
import java.util.Objects;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {LocalStorage.class, ImageUtil.class}
)
class ImageUtilTest {

    @Autowired
    public LocalStorage localStorage;

    @Autowired
    public ImageUtil imageUtil;

    @Test
    void fullTest() throws Exception {
        final Path pngFilePath = Path.of(Objects.requireNonNull(ImageUtilTest.class.getResource("/Debian_logo_01.png")).toURI());
        byte[] data = localStorage.getFileData(pngFilePath);
        Path tmpPath = localStorage.createTempFile("Test", ".png");
        localStorage.write(tmpPath, imageUtil.scaleImage("image/png", data, 100, 100));

        final Path jpgFilePath = Path.of(Objects.requireNonNull(ImageUtilTest.class.getResource("/wallpaper.jpg")).toURI());
        data = localStorage.getFileData(jpgFilePath);
        tmpPath = localStorage.createTempFile("Test", ".jpg");
        localStorage.write(tmpPath, imageUtil.scaleImage("image/jpg", data, 100, 100));
    }
}
