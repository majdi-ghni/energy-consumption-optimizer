package de.adesso.energyconsumptionoptimizer.repository.user;

import de.adesso.energyconsumptionoptimizer.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT DISTINCT u.address.zipCode FROM User u")
    List<String> findDistinctZipCodes();
}
