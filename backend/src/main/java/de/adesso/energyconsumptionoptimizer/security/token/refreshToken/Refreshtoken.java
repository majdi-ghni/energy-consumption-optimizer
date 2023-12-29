package de.adesso.energyconsumptionoptimizer.security.token.refreshToken;

import de.adesso.energyconsumptionoptimizer.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Refreshtoken {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(unique = true, length = 2048)
    public String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;
    private String type;
    private boolean revoked;
    private boolean expired;
}
