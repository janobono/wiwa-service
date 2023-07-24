package sk.janobono.wiwa.dal.domain;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "key")
@Getter
@Setter
@ToString
@Entity
@Table(name = "wiwa_application_property")
public class ApplicationPropertyDo {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "group", column = @Column(name = "property_group")),
            @AttributeOverride(name = "key", column = @Column(name = "property_key"))
    })
    private ApplicationPropertyKeyDo key;

    @Column(name = "property_value")
    private String value;
}
