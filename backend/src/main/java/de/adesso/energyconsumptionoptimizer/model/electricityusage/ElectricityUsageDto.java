package de.adesso.energyconsumptionoptimizer.model.electricityusage;

import lombok.Data;

import java.util.UUID;

@Data // Lombok annotation for getters and setters
public class ElectricityUsageDto {
    private UUID id;
    private UUID userId;
    private String deviceName;
    private UUID applianceId;
    private UUID usagePeriodId;
    private double price;
    private double gsi;
}
