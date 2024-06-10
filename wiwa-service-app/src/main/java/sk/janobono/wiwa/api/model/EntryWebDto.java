package sk.janobono.wiwa.api.model;

import jakarta.validation.constraints.NotNull;

public record EntryWebDto<K, V>(@NotNull K key, @NotNull V value) {
}
