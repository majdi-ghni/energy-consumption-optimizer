package de.adesso.energyconsumptionoptimizer.model.user;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Address implements Serializable {
    private String street;

    private String city;

    private String state;

    private String zipCode;
}
