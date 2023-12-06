package de.adesso.energyconsumptionoptimizer.model.electricityusage;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ElectricityUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "appliance_id")
    private UUID applianceId;

    @Column(name = "usage_period_id")
    private UUID usagePeriodId;

    private double price;
    private double gsi;
}
