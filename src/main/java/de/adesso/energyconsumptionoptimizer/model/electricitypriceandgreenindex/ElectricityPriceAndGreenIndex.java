package de.adesso.energyconsumptionoptimizer.model.electricitypriceandgreenindex;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ElectricityPriceAndGreenIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private Instant startTimeStamp;
    private Instant endTimeStamp;
    private double price;
    private String priceUnit;
    private double gsi;
    private int standardElectricityCo2InGram; // holds the weight of co2 of standard electricity in gram unit
    private String city;
    private String zipCode;

}
