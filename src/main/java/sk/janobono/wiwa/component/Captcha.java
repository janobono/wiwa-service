package sk.janobono.wiwa.component;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.exception.WiwaException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;

@Component
public class Captcha {

    private final BCryptPasswordEncoder tokenEncoder;
    private final RandomString randomString;
    private final int captchaLength;

    public Captcha(final CommonConfigProperties commonConfigProperties, final RandomString randomString) {
        this.captchaLength = commonConfigProperties.captchaLength();
        this.randomString = randomString;
        this.tokenEncoder = new BCryptPasswordEncoder(6);
    }

    public String generateText() {
        return randomString.alphaNumeric(captchaLength / 2, captchaLength / 2 + captchaLength % 2, 0, captchaLength, captchaLength);
    }

    public byte[] generateImage(final String text) {
        final int w = 180;
        final int h = 40;
        final BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);
        g.setFont(new Font("Serif", Font.PLAIN, 26));
        g.setColor(Color.blue);
        final int start = 10;
        final byte[] bytes = text.getBytes();

        final Random random = new Random();
        for (int i = 0; i < bytes.length; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.drawString(new String(new byte[]{bytes[i]}), start + (i * 20), (int) (Math.random() * 20 + 20));
        }
        g.setColor(Color.white);
        for (int i = 0; i < 8; i++) {
            g.drawOval((int) (Math.random() * 160), (int) (Math.random() * 10), 30, 30);
        }
        g.dispose();
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", bout);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return bout.toByteArray();
    }

    public String generateToken(final String text) {
        return tokenEncoder.encode(text);
    }

    public boolean isTokenValid(final String text, final String token) {
        return tokenEncoder.matches(text, token);
    }

    public void checkTokenValid(final String text, final String token) {
        if (!isTokenValid(text, token)) {
            throw WiwaException.INVALID_CAPTCHA.exception("Invalid captcha.");
        }
    }
}
