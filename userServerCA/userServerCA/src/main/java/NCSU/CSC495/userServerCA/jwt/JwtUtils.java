package NCSU.CSC495.userServerCA.jwt;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import NCSU.CSC495.userServerCA.User.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

/**
 * jwt methods e.g. generate, get, ...
 */
@Component
public class JwtUtils {

	private SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	private static final Long JWT_EXPIRATION = 60L;
	private static final int MS_IN_MIN = 60000;

	public String generateJwt(User user) {
		return Jwts.builder().claim("name", user.getName())
				.setSubject(user.getName()).setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(Instant.now()))
				.setExpiration(Date.from(Instant.now().plus(JWT_EXPIRATION, ChronoUnit.MINUTES)))
				.signWith(key, SignatureAlgorithm.HS512).compact();
	}

	public Claims getClaims(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		return claims;
	}

	public int getExpiration(String token) {
		int remainingTime = (int) (Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
				.getExpiration().getTime() - System.currentTimeMillis()) / MS_IN_MIN;
		return remainingTime;
	}

}
