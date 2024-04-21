package sk.janobono.wiwa.business.impl.component;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.model.order.OrderItemChangeData;
import sk.janobono.wiwa.business.model.order.OrderItemData;
import sk.janobono.wiwa.business.model.order.OrderSummaryData;
import sk.janobono.wiwa.business.model.order.OrderUserData;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

@Component
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class OrderItemUtil {

    private BigDecimal weightValue = BigDecimal.ZERO;
    private Unit weightUnit = Unit.KILOGRAM;
    private BigDecimal netWeightValue = BigDecimal.ZERO;
    private Unit netWeightUnit = Unit.KILOGRAM;
    private BigDecimal totalValue = BigDecimal.ZERO;
    private Unit totalUnit = Unit.EUR;

    public void setOrderItems(final List<OrderItemData> orderItems) {
// TODO
    }

    public List<OrderItemData> getOrderItems() {
        // TODO
        return null;
    }

    public OrderItemData addItem(OrderUserData creator, OrderItemChangeData orderItemChange) {
        return null;
    }

    public OrderItemData setItem(Long itemId, OrderUserData modifier, OrderItemChangeData orderItemChange) {
        return null;
    }

    public OrderSummaryData getOrderSummary() {
        return null;
    }

    public OrderItemData moveUpItem(Long itemId, OrderUserData modifier) {
        return null;
    }

    public OrderItemData moveDownItem(Long itemId, OrderUserData modifier) {
        return null;
    }

    public void deleteItem(Long itemId) {
    }

    public BigDecimal getWeightValue() {
        return weightValue;
    }

    public Unit getWeightUnit() {
        return weightUnit;
    }

    public BigDecimal getNetWeightValue() {
        return netWeightValue;
    }

    public Unit getNetWeightUnit() {
        return netWeightUnit;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public Unit getTotalUnit() {
        return totalUnit;
    }
}
