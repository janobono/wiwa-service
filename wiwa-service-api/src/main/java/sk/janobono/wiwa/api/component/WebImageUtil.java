package sk.janobono.wiwa.api.component;

import org.springframework.stereotype.Component;
import sk.janobono.wiwa.api.model.ApplicationImageWeb;
import sk.janobono.wiwa.business.model.ApplicationImageSo;

import java.text.MessageFormat;
import java.util.Base64;

@Component
public class WebImageUtil {

    public String toDataString(final String fileType, final byte[] data) {
        return MessageFormat.format("data:{0};base64,{1}", fileType, Base64.getEncoder().encodeToString(data));
    }

    public ApplicationImageWeb toWeb(final ApplicationImageSo applicationImageSo) {
        return new ApplicationImageWeb(
                applicationImageSo.fileName(),
                toDataString(applicationImageSo.fileType(), applicationImageSo.thumbnail())
        );
    }
}
