package sk.janobono.wiwa.common.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.common.config.CommonConfigProperties;
import sk.janobono.wiwa.common.exception.WiwaException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;

@Slf4j
@Component
public class Captcha {

    private final BCryptPasswordEncoder tokenEncoder;
    private final RandomString randomString;
    private final int captchaLength;

    public Captcha(CommonConfigProperties commonConfigProperties, RandomString randomString) {
        this.captchaLength = commonConfigProperties.captchaLength();
        this.randomString = randomString;
        this.tokenEncoder = new BCryptPasswordEncoder(6);
    }

    public String generateText() {
        log.debug("generateText()");
        return randomString.alphaNumeric(captchaLength / 2, captchaLength / 2 + captchaLength % 2, 0, captchaLength, captchaLength);
    }

    public byte[] generateImage(String text) {
        log.debug("generateImage({})", text);
        int w = 180, h = 40;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);
        g.setFont(new Font("Serif", Font.PLAIN, 26));
        g.setColor(Color.blue);
        int start = 10;
        byte[] bytes = text.getBytes();

        Random random = new Random();
        for (int i = 0; i < bytes.length; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.drawString(new String(new byte[]{bytes[i]}), start + (i * 20), (int) (Math.random() * 20 + 20));
        }
        g.setColor(Color.white);
        for (int i = 0; i < 8; i++) {
            g.drawOval((int) (Math.random() * 160), (int) (Math.random() * 10), 30, 30);
        }
        g.dispose();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", bout);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bout.toByteArray();
    }

    public String generateToken(String text) {
        log.debug("generateToken({})", text);
        return tokenEncoder.encode(text);
    }

    public boolean isTokenValid(String text, String token) {
        log.debug("isTokenValid({},{})", text, token);
        return tokenEncoder.matches(text, token);
    }

    public void checkTokenValid(String text, String token) {
        log.debug("isTokenValid({},{})", text, token);
        if (!isTokenValid(text, token)) {
            throw WiwaException.INVALID_CAPTCHA.exception("Invalid captcha.");
        }
    }
}
