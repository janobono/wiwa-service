package sk.janobono.wiwa.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.model.ui.LocalizedDataItemSo;
import sk.janobono.wiwa.business.model.ui.LocalizedDataSo;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;

import java.util.ArrayList;

@RequiredArgsConstructor
@Slf4j
@Component
public class LocalizedDataUtil {

    private final ApplicationPropertyService applicationPropertyService;

    public LocalizedDataSo<String> saveLocalizedData(LocalizedDataSo<String> localizedData, String group, String key) {
        log.debug("saveLocalizedData({},{},{})", localizedData, group, key);
        LocalizedDataSo<String> result = new LocalizedDataSo<>(new ArrayList<>());
        for (LocalizedDataItemSo<String> localizedDataItem : localizedData.items()) {
            result.items().add(new LocalizedDataItemSo<>(localizedDataItem.language(),
                    applicationPropertyService.setApplicationProperty(group, key, localizedDataItem.language(), localizedDataItem.data()))
            );
        }
        log.debug("saveLocalizedData({},{},{})={}", localizedData, group, key, result);
        return result;
    }
}
