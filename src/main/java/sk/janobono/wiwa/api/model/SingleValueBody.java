package sk.janobono.wiwa.api.model;

import jakarta.validation.constraints.NotNull;

public record SingleValueBody<T>(@NotNull T value) {
}
