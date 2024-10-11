package NCSU.CSC495.chatServerCA.Chat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * implementation of chat interface to process db info
 */
@Service
public class ChatServiceImpl implements ChatService {
	
	@Autowired
	private ChatRepository chatRepository;
	
	@Override
	public List<Chat> getAllChats() {
		return chatRepository.findAll();
	}

	@Override
	public Chat addChat(Chat chat) {
		return chatRepository.saveAndFlush(chat);
	}

}