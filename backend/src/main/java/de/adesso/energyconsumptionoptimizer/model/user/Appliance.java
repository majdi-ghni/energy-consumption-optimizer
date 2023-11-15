package de.adesso.energyconsumptionoptimizer.model.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Appliance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name; // name or type of the appliance

    // Todo: delete brand from appliances
    private String brand; // the brand of the appliance e.g. Samsung

    private String model; // the model of the appliance e.g. washing machines

    private Duration estimatedUsageDuration; // estimated duration of use for each device

    @Column(name = "power_rating")
    private Double powerRating; // it  represents the amount of electrical power the appliance consumes in kilowatts

    // TODO: is this really needed? DELETE
    @Column(name = "average_daily_usage")
    private Double averageDailyUsageHours; //represents the average number of hours the appliance is used per day. It can be used to calculate the daily energy consumption

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column
    private ApplianceUsageType applianceUsageType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
