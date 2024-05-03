package sk.janobono.wiwa.business.impl.component;

import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.model.order.summary.*;
import sk.janobono.wiwa.dal.domain.OrderItemSummaryDo;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Component
public class OrderSummaryCodeMapper {

    private static final String CODE_SEPARATOR = "::";
    private static final String PART = "PART";
    private static final String TOTAL = "TOTAL";
    private static final String BOARD = "BOARD";
    private static final String EDGE = "EDGE";
    private static final String CONSUMPTION = "CONSUMPTION";
    private static final String GLUE = "GLUE";
    private static final String CUT = "CUT";

    public List<OrderItemSummaryDo> toOrderItemSummaries(final Long itemId, final OrderItemSummaryData orderItemSummary) {
        final List<OrderItemSummaryDo> orderItemSummaries = new LinkedList<>();

        orderItemSummaries.addAll(mapBoardSummaries(itemId, PART, orderItemSummary.partSummary().boardSummary()));
        orderItemSummaries.addAll(mapEdgeSummaries(itemId, PART, orderItemSummary.partSummary().edgeSummary()));
        orderItemSummaries.add(OrderItemSummaryDo.builder()
                .orderItemId(itemId)
                .code(createCode(PART, GLUE))
                .amount(orderItemSummary.partSummary().gluedArea())
                .build());
        orderItemSummaries.addAll(mapCutSummaries(itemId, PART, orderItemSummary.partSummary().cutSummary()));

        orderItemSummaries.addAll(mapBoardSummaries(itemId, TOTAL, orderItemSummary.totalSummary().boardSummary()));
        orderItemSummaries.addAll(mapEdgeSummaries(itemId, TOTAL, orderItemSummary.totalSummary().edgeSummary()));
        orderItemSummaries.add(OrderItemSummaryDo.builder()
                .orderItemId(itemId)
                .code(createCode(TOTAL, GLUE))
                .amount(orderItemSummary.totalSummary().gluedArea())
                .build());
        orderItemSummaries.addAll(mapCutSummaries(itemId, TOTAL, orderItemSummary.totalSummary().cutSummary()));

        return orderItemSummaries;
    }

    public OrderItemSummaryData toOrderItemSummary(final List<OrderItemSummaryDo> orderItemSummaries) {
        return OrderItemSummaryData.builder()
                .partSummary(toSummary(PART, orderItemSummaries))
                .totalSummary(toSummary(TOTAL, orderItemSummaries))
                .build();
    }

    private List<OrderItemSummaryDo> mapBoardSummaries(final Long itemId, final String prefix, final List<OrderItemBoardSummaryData> items) {
        final List<OrderItemSummaryDo> orderItemSummaries = new LinkedList<>();
        for (final OrderItemBoardSummaryData orderBoardSummary : items) {
            orderItemSummaries.add(OrderItemSummaryDo.builder()
                    .orderItemId(itemId)
                    .code(createCode(prefix, BOARD, orderBoardSummary.id().toString()))
                    .amount(orderBoardSummary.area())
                    .build());
        }
        return orderItemSummaries;
    }

    private List<OrderItemSummaryDo> mapEdgeSummaries(final Long itemId, final String prefix, final List<OrderItemEdgeSummaryData> items) {
        final List<OrderItemSummaryDo> orderItemSummaries = new LinkedList<>();
        for (final OrderItemEdgeSummaryData orderEdgeSummary : items) {
            orderItemSummaries.add(OrderItemSummaryDo.builder()
                    .orderItemId(itemId)
                    .code(createCode(prefix, EDGE, CONSUMPTION, orderEdgeSummary.id().toString()))
                    .amount(orderEdgeSummary.length())
                    .build());

            orderItemSummaries.add(OrderItemSummaryDo.builder()
                    .orderItemId(itemId)
                    .code(createCode(prefix, EDGE, GLUE, orderEdgeSummary.id().toString()))
                    .amount(orderEdgeSummary.glueLength())
                    .build());
        }
        return orderItemSummaries;
    }

    private List<OrderItemSummaryDo> mapCutSummaries(final Long itemId, final String prefix, final List<OrderItemCutSummaryData> items) {
        final List<OrderItemSummaryDo> orderItemSummaries = new LinkedList<>();
        for (final OrderItemCutSummaryData orderCutSummary : items) {
            orderItemSummaries.add(OrderItemSummaryDo.builder()
                    .orderItemId(itemId)
                    .code(createCode(prefix, CUT, orderCutSummary.thickness().toPlainString()))
                    .amount(orderCutSummary.amount())
                    .build());
        }
        return orderItemSummaries;
    }

    private OrderItemPartSummaryData toSummary(final String prefix, final List<OrderItemSummaryDo> items) {
        return OrderItemPartSummaryData.builder()
                .boardSummary(toBoardSummary(prefix, items))
                .edgeSummary(toEdgeSummary(prefix, items))
                .gluedArea(toGluedArea(prefix, items))
                .cutSummary(toCutSummary(prefix, items))
                .build();
    }

    private List<OrderItemBoardSummaryData> toBoardSummary(final String prefix, final List<OrderItemSummaryDo> items) {
        return items.stream()
                .filter(item -> item.getCode().startsWith(createCode(prefix, BOARD)))
                .map(item -> new OrderItemBoardSummaryData(Long.valueOf(parseCode(item.getCode())[2]), item.getAmount()))
                .toList();
    }

    private List<OrderItemEdgeSummaryData> toEdgeSummary(final String prefix, final List<OrderItemSummaryDo> items) {
        final List<OrderItemSummaryDo> edgeSummaries = items.stream()
                .filter(item -> item.getCode().startsWith(createCode(prefix, EDGE, CONSUMPTION))
                        || item.getCode().startsWith(createCode(prefix, EDGE, GLUE)))
                .toList();

        return edgeSummaries.stream()
                .filter(item -> item.getCode().startsWith(createCode(prefix, EDGE, CONSUMPTION)))
                .map(item -> Long.valueOf(parseCode(item.getCode())[3]))
                .map(id -> {
                    final BigDecimal length = edgeSummaries.stream()
                            .filter(item -> item.getCode().equals(createCode(prefix, EDGE, CONSUMPTION, id.toString())))
                            .map(OrderItemSummaryDo::getAmount)
                            .findFirst()
                            .orElse(BigDecimal.ZERO);

                    final BigDecimal glueLength = edgeSummaries.stream()
                            .filter(item -> item.getCode().equals(createCode(prefix, EDGE, GLUE, id.toString())))
                            .map(OrderItemSummaryDo::getAmount)
                            .findFirst()
                            .orElse(BigDecimal.ZERO);

                    return new OrderItemEdgeSummaryData(id, length, glueLength);
                })
                .toList();
    }

    private BigDecimal toGluedArea(final String prefix, final List<OrderItemSummaryDo> items) {
        return items.stream()
                .filter(item -> item.getCode().equals(createCode(prefix, GLUE)))
                .findFirst()
                .map(OrderItemSummaryDo::getAmount)
                .orElse(BigDecimal.ZERO);
    }

    private List<OrderItemCutSummaryData> toCutSummary(final String prefix, final List<OrderItemSummaryDo> items) {
        return items.stream()
                .filter(item -> item.getCode().startsWith(createCode(prefix, CUT)))
                .map(item -> new OrderItemCutSummaryData(new BigDecimal(parseCode(item.getCode())[2]), item.getAmount()))
                .toList();
    }

    private String createCode(final String... elements) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elements.length; i++) {
            sb.append(elements[i]);
            if (i < elements.length - 1) {
                sb.append(CODE_SEPARATOR);
            }
        }
        return sb.toString();
    }

    private String[] parseCode(final String code) {
        return code.split(CODE_SEPARATOR);
    }
}
