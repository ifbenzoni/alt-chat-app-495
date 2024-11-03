package NCSU.CSC495.chatServerCA.Chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * chat endpoints
 */
@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
	
	@Autowired
	private ChatService chatService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * create chat in db
	 */
	@PostMapping("/create")
    ResponseEntity<String> createChat (@RequestBody Chat chat, @RequestHeader("token") String token) {
		try {
            String jwtCheckEndpoint = "http://user-server:8080/user/jwtName";
            ResponseEntity<String> response = restTemplate.postForEntity(jwtCheckEndpoint, token, String.class);
            System.out.println(response.getBody());
        } catch (Exception e) {
            return new ResponseEntity<>("invalid jwt (or connection issues)", HttpStatus.INTERNAL_SERVER_ERROR);
        }
		Chat ret = chatService.addChat(chat);
		return new ResponseEntity<>(new Gson().toJson(ret.getTitle() + " added"), HttpStatus.OK);
    }
	
	/**
	 * get chats related to a name
	 */
	@GetMapping("/chatDetails/{name}")
	public ResponseEntity<List<Chat>> chatDetails (@PathVariable("name") String name, @RequestHeader("token") String token) {
        try {
            String jwtCheckEndpoint = "http://user-server:8080/user/jwtName";
            ResponseEntity<String> response = restTemplate.postForEntity(jwtCheckEndpoint, token, String.class);
            System.out.println(response.getBody());
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<Chat> allChats = chatService.getAllChats();
		if (allChats == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		List<Chat> toRemove = new ArrayList<Chat>();
		for (Chat chat: allChats) {
			if (!chat.getParticipants().contains(name) && !chat.getOwner().equals(name)) {
				toRemove.add(chat);
			}
		}
		for (Chat chat: toRemove) {
			allChats.remove(chat);
		}
		return new ResponseEntity<>(allChats, HttpStatus.OK);
	}
	
	/**
	 * search chat history for entries containing input string
	 */
	@GetMapping("/search/{id}/{text}")
	public ResponseEntity<List<String>> search (@PathVariable("id") int id, @PathVariable("text") String text, @RequestHeader("token") String token) {
        try {
            String jwtCheckEndpoint = "http://user-server:8080/user/jwtName";
            ResponseEntity<String> response = restTemplate.postForEntity(jwtCheckEndpoint, token, String.class);
            System.out.println(response.getBody());
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<Chat> allChats = chatService.getAllChats();
		if (allChats == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
        //System.out.println("1: " + allChats.size());
		List<Chat> toRemove = new ArrayList<Chat>();
		for (Chat chat: allChats) {
			if (!(chat.getId() == id)) {
				toRemove.add(chat);
			}
		}
		for (Chat chat: toRemove) {
			allChats.remove(chat);
		}
		//System.out.println("2: " + allChats.size());
		List<String> filteredPosts = new ArrayList<String>();
		for (String post: allChats.get(0).getHistory()) {
			if (post.contains(text)) {
				filteredPosts.add(post);
			}
		}
		return new ResponseEntity<>(filteredPosts, HttpStatus.OK);
	}
	
	/**
	 * add entry to chat history
	 */
	@PostMapping("/add/{id}")
	public ResponseEntity<Chat> postToChat (@PathVariable("id") Long id, @RequestBody String text, @RequestHeader("token") String token) {
        String name = "";
		try {
            String jwtCheckEndpoint = "http://user-server:8080/user/jwtName";
            ResponseEntity<String> response = restTemplate.postForEntity(jwtCheckEndpoint, token, String.class);
            name = response.getBody();
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<Chat> allChats = chatService.getAllChats();
		if (allChats == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		List<Chat> toRemove = new ArrayList<Chat>();
		for (Chat chat: allChats) {
			if (!(chat.getId() == id)) {
				toRemove.add(chat);
			}
		}
		for (Chat chat: toRemove) {
			allChats.remove(chat);
		}
		Chat chat = allChats.get(0);
		chat.getHistory().add(name + ": " + text);
		chatService.addChat(chat);
		return new ResponseEntity<>(chat, HttpStatus.OK);
	}
}