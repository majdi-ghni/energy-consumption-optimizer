package de.adesso.energyconsumptionoptimizer.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${SECRET_KEY}")
    String SECRET_KEY;
    @Value("${expirationMs}")
    Long expirationMs;
    @Value("${issuer}")
    String issuer;
    @Value("${refreshExpirationMs}")
    Long refreshExpirationMs;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities());
        claims.put("tokenType", "accessToken");
        claims.put("iss", issuer);

        return Jwts
                .builder()
                .setIssuer(issuer)
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", issuer);
        return Jwts
                .builder()
                .setIssuer(issuer)
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates token only from user details
     *
     * @param userDetails holds user details
     * @return generated token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // when this claim was created
                // TODO: choose a convenience period for token expiration
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // how long this token should be valid. here => 24 hours
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact(); // compact will generate and return the token
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes); // hmacShaKeyFor is the algorithm used in the request header
    }

    /**
     * Validate if token is valid and belong to the user details
     *
     * @param token
     * @param userDetails
     * @return true if token is valid and belong to the user details and false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String extractUsername = extractUsername(token);
        return (extractUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));

    }

    /**
     * Tells if token expired or not
     *
     * @param token
     * @return true if token is not expired and false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
