package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public QuantityUnit getQuantityUnit(final String id) {
        return toQuantityUnit(
                quantityUnitRepository.findById(id)
                        .orElseThrow(() -> WiwaException.QUANTITY_UNIT_NOT_FOUND.exception("Quantity unit with id {0} not found", id))
        );
    }

    public QuantityUnit setQuantityUnit(final QuantityUnit quantityUnit) {
        return toQuantityUnit(
                quantityUnitRepository.save(toQuantityUnitDo(quantityUnit))
        );
    }

    public void deleteQuantityUnit(final String id) {
        if (!quantityUnitRepository.existsById(id)) {
            throw WiwaException.QUANTITY_UNIT_NOT_FOUND.exception("Quantity unit with id {0} not found", id);
        }
        quantityUnitRepository.deleteById(id);
    }

    private QuantityUnit toQuantityUnit(final QuantityUnitDo quantityUnitDo) {
        return new QuantityUnit(
                quantityUnitDo.getId(),
                quantityUnitDo.getType(),
                quantityUnitDo.getUnit()
        );
    }

    private QuantityUnitDo toQuantityUnitDo(final QuantityUnit quantityUnit) {
        return QuantityUnitDo.builder()
                .id(quantityUnit.id())
                .type(quantityUnit.type())
                .unit(quantityUnit.unit())
                .build();
    }
}
