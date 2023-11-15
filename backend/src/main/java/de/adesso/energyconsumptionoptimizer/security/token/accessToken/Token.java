package de.adesso.energyconsumptionoptimizer.security.token.accessToken;

import de.adesso.energyconsumptionoptimizer.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Token {

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
