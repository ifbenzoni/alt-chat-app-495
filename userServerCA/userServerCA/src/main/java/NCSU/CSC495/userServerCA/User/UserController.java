package NCSU.CSC495.userServerCA.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestTemplate;

import NCSU.CSC495.userServerCA.jwt.JwtUtils;
import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.google.gson.Gson;

/**
 * user endpoints
 */
@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * create user
	 */
	@PostMapping("/create")
    ResponseEntity<String> createAccount (@RequestBody User user) {
		User ret = userService.addUser(user);
		return new ResponseEntity<>(new Gson().toJson(ret.getName() + " added"), HttpStatus.OK);
    }
	
	/**
	 * login (authentication)
	 */
	@PostMapping("/login")
    ResponseEntity<String> login (@RequestBody User user) {
		User ret = userService.getUser(user);
		if (ret == null || !user.getPw().equals(ret.getPw())) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		String jwt = jwtUtils.generateJwt(ret);
		return new ResponseEntity<>(new Gson().toJson(jwt), HttpStatus.OK);
    }
	
	/**
	 * get name from jwt (also validates jwt)
	 */
	@PostMapping("/jwtName")
	public ResponseEntity<String> jwtName(@RequestBody String token) {
		try {
			Claims claims = jwtUtils.getClaims(token);
			String name = claims.get("name", String.class);
			return new ResponseEntity<>(name, HttpStatus.OK);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
	}
	
	/**
	 * get user details based on jwt name, removes pw
	 */
	@GetMapping("/userDetails")
	public ResponseEntity<?> userDetails(@RequestHeader("token") String token) {
        String name;
		try {
            String jwtCheckEndpoint = "http://localhost:8080/user/jwtName";
            ResponseEntity<String> response = restTemplate.postForEntity(jwtCheckEndpoint, token, String.class);
            System.out.println(response.getBody());
            name = response.getBody();
        } catch (Exception e) {
            return new ResponseEntity<>("invalid jwt (or connection issues)", HttpStatus.INTERNAL_SERVER_ERROR);
        }
		User find = new User();
		find.setName(name);
		User ret = userService.getUser(find);
		if (ret == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		ret.setPw("");
		return new ResponseEntity<>(ret, HttpStatus.OK);
	}
	
	/**
	 * changes notification setting
	 */
	@PostMapping("/settings")
	public ResponseEntity<String> jwtName(@RequestBody Boolean setNotifications, @RequestHeader("token") String token) {
        String name = "";
		try {
            String jwtCheckEndpoint = "http://localhost:8080/user/jwtName";
            ResponseEntity<String> response = restTemplate.postForEntity(jwtCheckEndpoint, token, String.class);
            name = response.getBody();
        } catch (Exception e) {
            return new ResponseEntity<>("invalid jwt (or connection issues)", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User find = new User();
		find.setName(name);
		User ret = userService.getUser(find);
		if (ret == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		ret.setNotifications(setNotifications);
		userService.addUser(ret);
		return new ResponseEntity<>(new Gson().toJson("success"), HttpStatus.OK);
	}

}