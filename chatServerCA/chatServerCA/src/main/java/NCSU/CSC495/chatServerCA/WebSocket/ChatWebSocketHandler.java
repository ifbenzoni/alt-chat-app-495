package NCSU.CSC495.chatServerCA.WebSocket;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import NCSU.CSC495.chatServerCA.Chat.Chat;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.core.ParameterizedTypeReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * web socket processing
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final RestTemplate restTemplate = new RestTemplate();  // Initialize RestTemplate

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Extract the JWT token from session URI
        String token = getTokenFromSession(session);
        //validate jwt
        if (token == null || !validateJwtToken(token)) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            System.out.println("Invalid JWT token, connection closed");
            return;
        }
        //get name contained in jwt
        String name = "";
        try {
            String jwtCheckEndpoint = "http://localhost:8080/user/jwtName";
            ResponseEntity<String> response = restTemplate.postForEntity(jwtCheckEndpoint, token, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                name = response.getBody();
                System.out.println("retrieved jwt name: " + name);
            } else {
                System.out.println("failed to get name, status code: " + response.getStatusCode());
                return;
            }
        } catch (Exception e) {
            System.out.println("Error getting name: " + e.getMessage());
            return;
        }
        //get list of chats that goes with jwt name
        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        List<Chat> chatList = new ArrayList<Chat>();
        try {
            String chatDetailsEndpoint = "http://localhost:8081/chat/chatDetails/" + name;
            ParameterizedTypeReference<List<Chat>> responseType = new ParameterizedTypeReference<List<Chat>>() {};
            ResponseEntity<List<Chat>> response = restTemplate.exchange(
                    chatDetailsEndpoint, 
                    HttpMethod.GET, 
                    entity, 
                    responseType
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                chatList = response.getBody();
                System.out.println("retrieved chat details: " + chatList);
            } else {
                System.out.println("failed to get chat details, status code: " + response.getStatusCode());
                return;
            }
        } catch (Exception e) {
            System.out.println("Error getting chat details: " + e.getMessage());
            return;
        }
        
        //validate that name goes w/ chat id
        String chatId = getChatIdFromSession(session);
        boolean allow = false;
        for(Chat chat: chatList) {
        	if (chat.getId() == Integer.parseInt(chatId)) {
        		allow = true;
        	}
        }
        if (!allow) {
        	return;
        }

        // If token is valid, allow the session
        sessions.add(session);
        System.out.println("New session connected for chat ID: " + chatId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String chatId = getChatIdFromSession(session);  // Extract chat ID
        System.out.println("Received message: " + payload + " for chat ID: " + chatId);
        
        // Handle message based on the chatId
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen()) {
                webSocketSession.sendMessage(new TextMessage(payload));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Session closed");
    }

    // get jwt from session url
    private String getTokenFromSession(WebSocketSession session) {
        Map<String, String> params = getParamsFromUri(session.getUri().toString());
        return params.get("token");
    }

    // get chat id from session url
    private String getChatIdFromSession(WebSocketSession session) {
        Map<String, String> params = getParamsFromUri(session.getUri().toString());
        return params.get("chatId");
    }

    // get params from uri
    private Map<String, String> getParamsFromUri(String uri) {
        return Arrays.stream(uri.split("\\?")[1].split("&"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(a -> a[0], a -> a[1]));
    }

    // check valid jwt
    private boolean validateJwtToken(String token) {
        try {
            // Call external endpoint to validate the JWT
            String jwtCheckEndpoint = "http://localhost:8080/user/jwtName";
            ResponseEntity<String> response = restTemplate.postForEntity(jwtCheckEndpoint, token, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String name = response.getBody();
                System.out.println("JWT validated for user: " + name);
                return true;
            } else {
                System.out.println("JWT validation failed, status code: " + response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error during JWT validation: " + e.getMessage());
            return false;
        }
    }
}
