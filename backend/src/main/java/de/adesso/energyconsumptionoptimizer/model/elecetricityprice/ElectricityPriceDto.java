package de.adesso.energyconsumptionoptimizer.model.elecetricityprice;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@JsonDeserialize(using = ElectricityPriceDeserializer.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElectricityPriceDto {

    private Instant startTimeStamp;
    private Instant endTimeStamp;
    private double marketPrice;
    private String unit;
    private double localPrice;
    private String city;
    private String zipCode;
}
