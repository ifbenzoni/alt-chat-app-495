package NCSU.CSC495.chatServerCA.Chat;

import java.util.List;

/**
 * chat interface for processing db info
 */
public interface ChatService {
	
	public List<Chat> getAllChats();
	public Chat addChat(Chat chat);

}
