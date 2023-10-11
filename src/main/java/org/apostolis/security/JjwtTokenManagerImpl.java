package org.apostolis.security;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;


import io.javalin.http.ForbiddenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apostolis.model.Role;

public class JjwtTokenManagerImpl implements TokenManager{
    private static final long EXPIRE_AFTER_MINS = 30;
    private final Key key;

    public JjwtTokenManagerImpl(){
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
    @Override
    public String issueToken(String username, Role role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("Role",role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(EXPIRE_AFTER_MINS)))
                .signWith(key)
                .compact();
    }
    @Override
    public boolean authorize(String token, String username) {
        try {
            String subject = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
            Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
            return subject.equalsIgnoreCase(username) && expiration.before(new Date());
        } catch (Exception ex){
            throw new ForbiddenResponse();
        }
    }
}
