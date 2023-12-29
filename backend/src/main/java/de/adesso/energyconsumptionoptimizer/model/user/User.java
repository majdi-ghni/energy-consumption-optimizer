package de.adesso.energyconsumptionoptimizer.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.adesso.energyconsumptionoptimizer.model.elecetricityprice.ElectricityPrice;
import de.adesso.energyconsumptionoptimizer.model.electricitypriceandgreenindex.ElectricityPriceAndGreenIndex;
import de.adesso.energyconsumptionoptimizer.model.greenelectricityindex.GreenElectricityIndex;
import de.adesso.energyconsumptionoptimizer.security.token.accessToken.Token;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "\"User\"")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String username;

    @Embedded
    private Address address;

    private String password;

    private String email;

    //@Enumerated(EnumType.STRING) TODO: check if needed or remove it
    private Role role;

    @Column(name = "actual_tariff")
    private double actualTariff; // actual electricity tariff of the user to compare it with the dynamic tariffs

    @Column(name = "saved_co2_footprint", columnDefinition = "INT default '0'")
    private int savedCo2Footprint; // amount of reduced CO2 footprint in kilograms by adjusting their electricity usage

    @Column(name = "total_co2_footprint", columnDefinition = "INT default '0'")
    private double totalCo2Footprint; // total amount of CO2 footprint in kilograms of user

    // TODO: Set initial value. delete and re create DB
    @Column(name = "total_electricity_Cost", columnDefinition = "Decimal(7, 2)")
    private BigDecimal totalElectricityCost = BigDecimal.ZERO; // total virtual electricity costs made by the user since using the app

    @Column(name = "last_month_bill", columnDefinition = "Decimal(7, 2)")
    private BigDecimal lastMonthBill = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Appliance> appliances;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
