package NCSU.CSC495.chatServerCA.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * connection to db
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}