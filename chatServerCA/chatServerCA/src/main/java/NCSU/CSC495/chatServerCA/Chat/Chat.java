package NCSU.CSC495.chatServerCA.Chat;

import lombok.*;

import java.util.List;

import jakarta.persistence.*;

/**
 * chat model
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String owner;
	private List<String> participants;
	private List<String> history;
}
