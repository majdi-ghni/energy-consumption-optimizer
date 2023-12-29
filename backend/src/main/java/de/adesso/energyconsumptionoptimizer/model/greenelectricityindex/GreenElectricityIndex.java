package de.adesso.energyconsumptionoptimizer.model.greenelectricityindex;

import de.adesso.energyconsumptionoptimizer.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GreenElectricityIndex {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Instant startTimeStamp;
    private Instant endTimeStamp;
    private String zipCode;
    private double gsi; // holds the percentage of the green energy in the electricity mix
    private int ecoElectricityCo2InGram; // holds the weight of co2 of eco electricity in gram unit
    private int standardElectricityCo2InGram; // holds the weight of co2 of standard electricity in gram unit

    /**
     * Entity not updatable
     */
    @PreUpdate
    private void preUpdate() {
        throw new UnsupportedOperationException();
    }

    public void setGsi(double gsi) {
        this.gsi = BigDecimal.valueOf(gsi).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

}
