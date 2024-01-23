package com.example.basicSecurity.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY="02eae79b95a6f8b4ca5048db91849146a0ccb278a316047331e4acb047cb20dc";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private  <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    //EXtra claims is any information i want to store with in my token
    private String generateToken(Map< String, Object> extraClaims, UserDetails userDetails){

        //MacAlgorithm hs256 = Jwts.SIG.HS256;
        return Jwts.builder()
               // .claims(extraClaims).subject(userDetails.getUsername()).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + 1000*60*24)).signWith(getSignInKey()).compact();

                .setClaims(extraClaims) //my claims
                .setSubject(userDetails.getUsername()) //user details
                .setIssuedAt(new Date(System.currentTimeMillis())) //Issued date
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*24)) //expiration date
                .signWith(getSignInKey())        
               // .signWith(SignatureAlgorithm.HS256, getSignInKey()) //Bu token için sign ı ekliyor
                .compact();
    }


    //İf we want without extra claims
    public String generateToken( UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }



    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()).build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username= extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignInKey() {
        byte[] keyBytes= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
