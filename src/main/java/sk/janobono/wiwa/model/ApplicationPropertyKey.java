package sk.janobono.wiwa.model;

public interface ApplicationPropertyKey {
    String getGroup();

    String getKey(Object... arguments);
}
