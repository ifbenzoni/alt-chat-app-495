package NCSU.CSC495.userServerCA.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * user db connection
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	User findByName(String name);
	
}