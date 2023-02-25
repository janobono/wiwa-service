package sk.janobono.wiwa.business.service;

import sk.janobono.wiwa.common.model.ApplicationPropertyKey;

public interface ApplicationPropertyService {

    String getProperty(ApplicationPropertyKey key, Object... arguments);

    String setApplicationProperty(String group, String key, String language, String value);
}
