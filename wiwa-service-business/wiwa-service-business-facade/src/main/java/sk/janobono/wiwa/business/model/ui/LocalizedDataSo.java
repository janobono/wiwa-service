package sk.janobono.wiwa.business.model.ui;

import java.util.List;

public record LocalizedDataSo<T>(List<LocalizedDataItemSo<T>> items) {
}
