package app.dto;

import app.entities.user.Role;
import app.entities.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private Role role;

    public static UserDTO fromEntity (User user){
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

}
