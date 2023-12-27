package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.quantityunit.QuantityUnitDataSo;
import sk.janobono.wiwa.dal.domain.QuantityUnitDo;
import sk.janobono.wiwa.dal.repository.QuantityUnitRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.QuantityUnit;

import java.util.List;

@RequiredArgsConstructor
@Service
public class QuantityUnitService {

    private final QuantityUnitRepository quantityUnitRepository;

    public List<QuantityUnit> getQuantityUnits() {
        return quantityUnitRepository.findAll().stream()
                .map(this::toQuantityUnit)
                .toList();
    }

    public QuantityUnit getQuantityUnit(final Long id) {
        return toQuantityUnit(
                quantityUnitRepository.findById(id)
                        .orElseThrow(() -> WiwaException.QUANTITY_UNIT_NOT_FOUND.exception("Quantity unit with id {0} not found", id))
        );
    }

    public QuantityUnit addQuantityUnit(final QuantityUnitDataSo data) {
        return toQuantityUnit(quantityUnitRepository.save(
                QuantityUnitDo.builder()
                        .type(data.type())
                        .name(data.name())
                        .unit(data.unit())
                        .build())
        );
    }

    public QuantityUnit setQuantityUnit(final Long id, final QuantityUnitDataSo data) {
        return toQuantityUnit(quantityUnitRepository.save(
                QuantityUnitDo.builder()
                        .id(id)
                        .type(data.type())
                        .name(data.name())
                        .unit(data.unit())
                        .build())
        );
    }

    public void deleteQuantityUnit(final Long id) {
        if (!quantityUnitRepository.existsById(id)) {
            throw WiwaException.QUANTITY_UNIT_NOT_FOUND.exception("Quantity unit with id {0} not found", id);
        }
        quantityUnitRepository.deleteById(id);
    }

    private QuantityUnit toQuantityUnit(final QuantityUnitDo quantityUnitDo) {
        return new QuantityUnit(
                quantityUnitDo.getId(),
                quantityUnitDo.getType(),
                quantityUnitDo.getName(),
                quantityUnitDo.getUnit()
        );
    }
}
