package NCSU.CSC495.userServerCA.User;

import lombok.*;
import jakarta.persistence.*;

/**
 * user model
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String pw;
	private boolean notifications;
}

