package de.adesso.energyconsumptionoptimizer.security.token.refreshToken;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<Refreshtoken, UUID> {

    @Query(value = """
            select t from Refreshtoken t inner join User u\s
            on t.user.id = u.id\s
            where u.id = :id and (t.expired = false or t.revoked = false)\s
            """)
    List<Refreshtoken> findAllValidTokenByUser(@Param("id") UUID id);

    List<Refreshtoken> findAllTokenByUserId(@Param("id") UUID id);

    Optional<Refreshtoken> findByToken(String token);

    @Modifying
    @Query("delete from Refreshtoken t where t in :refreshtokens")
    void deleteAllTokens(@Param("refreshtokens") List<Refreshtoken> refreshtokens);
}
