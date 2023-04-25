package sk.janobono.wiwa.common.component;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.Normalizer;

@Component
public class ScDf {

    public String toDf(final String text) {
        if (!StringUtils.hasLength(text)) {
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
        if (!StringUtils.hasLength(text)) {
            return null;
        }
        return toDf(text).toLowerCase().trim();
    }
}
