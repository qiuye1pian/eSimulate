package org.core.model.environment.sunlight;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "sunlight_irradiance_scheme")
public class SunlightIrradianceScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_name", nullable = false)
    private String schemeName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "sunlightIrradianceScheme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SunlightIrradianceValue> sunlightIrradianceValues;
}
