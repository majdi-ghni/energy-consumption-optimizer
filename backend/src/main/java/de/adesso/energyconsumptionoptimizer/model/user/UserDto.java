package de.adesso.energyconsumptionoptimizer.model.user;

import de.adesso.energyconsumptionoptimizer.model.elecetricityprice.ElectricityPrice;
import de.adesso.energyconsumptionoptimizer.model.greenelectricityindex.GreenElectricityIndex;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class UserDto {

    private UUID id;

    @NotNull
    private String username;

    // TODO: just one field to hold name. delete first & last names
    private String firstName;

    private String lastName;

    @Embedded
    private Address address;

    @NotNull
    private String password;

    @NotNull
    @Email(message = "Invalid Email")
    private String email;

    @NotNull
    private Role role;

    private double actualTariff; // actual electricity tariff of the user to compare it with the dynamic tariffs

    private int savedCo2Footprint; // amount of reduced CO2 footprint in kilograms by adjusting their electricity usage

    private double totalCo2Footprint; // total amount of CO2 footprint in kilograms of user

    private BigDecimal totalPayments; // total payments made by the user since using the app

    private BigDecimal lastMonthBill;

    private Instant createdAt;

    private Instant updatedAt;

    private List<Appliance> appliances;

    // TODO: make relation n -> n for electricityPrices & greenElectricityIndexList
    private List<ElectricityPrice> electricityPrices;

    private List<GreenElectricityIndex> greenElectricityIndexList;
}
