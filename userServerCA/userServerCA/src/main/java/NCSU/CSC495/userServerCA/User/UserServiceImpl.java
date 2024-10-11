package NCSU.CSC495.userServerCA.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * implementation for user db logic
 */
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public User addUser(User user) {
		return userRepository.saveAndFlush(user);
	}
	
	@Override
	public User getUser(User user) {
		return userRepository.findByName(user.getName());
	}

}
