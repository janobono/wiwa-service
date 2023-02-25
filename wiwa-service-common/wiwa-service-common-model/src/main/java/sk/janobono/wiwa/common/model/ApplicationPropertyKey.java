package sk.janobono.wiwa.common.model;

public interface ApplicationPropertyKey {
    String getGroup();

    String getKey(Object... arguments);

    boolean isLocalized();
}
