package de.adesso.energyconsumptionoptimizer.model.electricitypriceandgreenindex;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class ElectricityPriceAndGreenIndexDto {
    private Instant startTimeStamp;
    private Instant endTimeStamp;
    private double price;
    private String priceUnit;
    private double gsi;
    private int standardElectricityCo2InGram; // holds the weight of co2 of standard electricity in gram unit
    private String city;
    private String zipCode;
}
