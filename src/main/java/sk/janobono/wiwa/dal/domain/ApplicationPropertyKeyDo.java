package sk.janobono.wiwa.dal.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Embeddable
public class ApplicationPropertyKeyDo implements Serializable {
    private String group;
    private String key;
}
