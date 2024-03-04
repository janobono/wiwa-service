package sk.janobono.wiwa.api.model;

import jakarta.validation.constraints.NotNull;

public record SingleValueBodyWebDto<T>(@NotNull T value) {
}
