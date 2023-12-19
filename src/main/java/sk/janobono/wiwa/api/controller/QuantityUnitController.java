package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.business.service.QuantityUnitService;
import sk.janobono.wiwa.model.QuantityUnit;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/quantity-units")
public class QuantityUnitController {

    private final QuantityUnitService quantityUnitService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<QuantityUnit> getQuantityUnits() {
        return quantityUnitService.getQuantityUnits();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public QuantityUnit getQuantityUnit(@PathVariable("id") final String id) {
        return quantityUnitService.getQuantityUnit(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    @ResponseStatus(HttpStatus.CREATED)
    public QuantityUnit addQuantityUnit(@Valid @RequestBody final QuantityUnit quantityUnit) {
        return quantityUnitService.setQuantityUnit(quantityUnit);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public QuantityUnit setQuantityUnit(@Valid @RequestBody final QuantityUnit quantityUnit) {
        return quantityUnitService.setQuantityUnit(quantityUnit);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public void deleteQuantityUnit(@PathVariable("id") final String id) {
        quantityUnitService.deleteQuantityUnit(id);
    }
}
