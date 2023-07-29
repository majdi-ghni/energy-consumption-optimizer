package de.adesso.energyconsumptionoptimizer.model.elecetricityprice;

import de.adesso.energyconsumptionoptimizer.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ElectricityPrice {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "start_time")
    private Instant startTimeStamp;

    @Column(name = "end_time")
    private Instant endTimeStamp;

    @Column(name = "market_price")
    private BigDecimal marketPrice;

    @Column(name = "local_price")
    private BigDecimal localPrice;

    private String city;
    private String zipCode;
    private String unit; // in Eur/MWh

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * prevent an entity from being updated after it is initially persisted.
     */
    @PreUpdate
    private void preUpdate() {
        throw new UnsupportedOperationException();
    }
}
