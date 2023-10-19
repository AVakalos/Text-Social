package org.apostolis.security;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;


import io.javalin.http.ForbiddenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apostolis.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JjwtTokenManagerImpl implements TokenManager{
    private static final long EXPIRE_AFTER_MINS = 60;       // Debug

    // Debugging
    private final String SECRET = "sfdghtuhgruitjkkourijkldjlifgjdfuiryuytukhg";

    //private final Key key;

    //private static final Logger logger = LoggerFactory.getLogger(JjwtTokenManagerImpl.class);

    public JjwtTokenManagerImpl(){
        //this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    @Override
    public String issueToken(String username, Role role) {
        //logger.info("Key at issue: "+getSignInKey());
        return Jwts.builder()
                .setSubject(username)
                .claim("Role",role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(EXPIRE_AFTER_MINS)))
                .signWith(getSignInKey())
                .compact();
    }
    @Override
    public boolean validateToken(String token) {
        try {
            Date expiration = Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody().getExpiration();
            return expiration.after(new Date());
        } catch (Exception ex){
            throw new ForbiddenResponse("Token has expired or is invalid");
        }
    }

    public Role extractRole(String token){
        return Role.valueOf(Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody().get("Role").toString());
    }
}
