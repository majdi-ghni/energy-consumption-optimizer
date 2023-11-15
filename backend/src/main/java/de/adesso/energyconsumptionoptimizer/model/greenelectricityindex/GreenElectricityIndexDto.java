package de.adesso.energyconsumptionoptimizer.model.greenelectricityindex;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@JsonDeserialize(using = GreenElectricityIndexDeserializer.class)
public class GreenElectricityIndexDto {
    private Instant startTimeStamp;
    private Instant endTimeStamp;
    private double gsi; // holds the percentage of the green energy in electricity mix
    private String zipCode;
    private int ecoElectricityCo2InGram; // holds the weight of co2 of eco electricity in gram unit
    private int standardElectricityCo2InGram; // holds the weight of co2 of standard electricity in gram unit
}
