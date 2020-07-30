package br.edu.ifrn.laj.pdcorp.apisea.security;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import br.edu.ifrn.laj.pdcorp.apisea.enums.ExceptionMessages;
import br.edu.ifrn.laj.pdcorp.apisea.exceptions.ApiUserException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * This class is responsible about any operations on JWT requests.
 * @author Dannylo Johnathan
 * @since 22/05/20
 * 
 */
public class TokenAuthenticationUtil {

	private static final long EXPIRATION_TIME = 860_000_000;
	private static final String SECRET = "SEASecretWordCrypt";
	private static final String TOKEN_PREFIX = "Bearer";
	private static final String HEADER_STRING = "Authorization";

	/**
	 * Add the JWT token generated after login in response HTTP. 
	 * @param response is the HTTP response of server.
	 * @param username must be the right username of one authenticated user.
	 *  
	 **/
	public static void addAuthentication(HttpServletResponse response, String username) {
		String jwt = Jwts.builder().setSubject(username)
				.setExpiration(new Date(Instant.now().toEpochMilli() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();

		StringBuilder token = new StringBuilder(TOKEN_PREFIX);
		token.append(" ").append(jwt);

		System.out.println(token.toString());

		response.addHeader(HEADER_STRING, token.toString());
	}

	public static Authentication getAuthentication(HttpServletRequest request){
		String token = request.getHeader(HEADER_STRING);
		if (!StringUtils.isEmpty(token)) {
			String user = Jwts.parser()
					.setSigningKey(SECRET)
					.parseClaimsJws(token.replace(TOKEN_PREFIX, "").trim())
					.getBody()
					.getSubject();
			
			if (!StringUtils.isEmpty(user)) {
				return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());	
			}
			return null;
		} 
		return null;
	}
	

}