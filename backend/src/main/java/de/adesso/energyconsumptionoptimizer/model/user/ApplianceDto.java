package de.adesso.energyconsumptionoptimizer.model.user;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ApplianceDto {
    private UUID id;

    private UserDto userDto;

    private String name; // name or type of the appliance

    private int estimatedUsageDuration; // estimated duration of use for each device in minutes

    private Double powerRating; // it  represents the amount of electrical power the appliance consumes in kilowatts

    private Instant createdAt;

    private Instant updatedAt;

    private ApplianceUsageType applianceUsageType;
}
