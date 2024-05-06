package sk.janobono.wiwa.business.model.application;

import sk.janobono.wiwa.model.BoardPosition;
import sk.janobono.wiwa.model.CornerPosition;
import sk.janobono.wiwa.model.EdgePosition;
import sk.janobono.wiwa.model.CSVColumn;

import java.util.Map;

public record CSVPropertiesData(
        String separator,
        Map<CSVColumn, String> columns,
        Map<BoardPosition, String> boards,
        Map<EdgePosition, String> edges,
        Map<CornerPosition, String> corners,
        String numberFormat,
        String nameBasicFormat,
        String nameFrameFormat,
        String nameDuplicatedBasicFormat,
        String nameDuplicatedFrameFormat
) {
}
