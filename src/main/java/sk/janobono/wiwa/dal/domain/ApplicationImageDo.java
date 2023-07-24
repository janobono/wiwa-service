package sk.janobono.wiwa.dal.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(of = "fileName")
@Getter
@Setter
@ToString(exclude = {"thumbnail", "data"})
@Entity
@Table(name = "wiwa_application_image")
public class ApplicationImageDo {

    @Id
    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "thumbnail")
    private byte[] thumbnail;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "data")
    private byte[] data;
}
