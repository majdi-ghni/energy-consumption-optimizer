package de.adesso.energyconsumptionoptimizer.security.token.accessToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    @Query(value = """
            select t from Token t inner join User u\s
            on t.user.id = u.id\s
            where u.id = :id and (t.expired = false or t.revoked = false)\s
            """)
    List<Token> findAllValidTokenByUser(@Param("id") UUID id);

    List<Token> findAllTokenByUserId(@Param("id") UUID id);

    Optional<Token> findByToken(String token);

    @Modifying
    @Query("delete from Token t where t in :tokens")
    void deleteAllTokens(@Param("tokens") List<Token> tokens);
}
