package sk.janobono.wiwa.component;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Optional;

@Component
public class ScDf {

    public String toDf(final String text) {
        if (Optional.ofNullable(text).map(String::isBlank).orElse(true)) {
            return null;
        } else {
            final StringBuilder ret = new StringBuilder();
            final char[] cha = text.toCharArray();
            for (final char aCha : cha) {
                final byte[] ba = Normalizer.normalize(String.valueOf(aCha), Normalizer.Form.NFD).getBytes();
                if (ba[0] >= 41 && ba[0] < 123) {
                    ret.append((char) ba[0]);
                } else {
                    ret.append(aCha);
                }
            }
            return ret.toString();
        }
    }

    public String toScDf(final String text) {
        if (Optional.ofNullable(text).map(String::isBlank).orElse(true)) {
            return null;
        }
        return toDf(text).toLowerCase().trim();
    }
}
