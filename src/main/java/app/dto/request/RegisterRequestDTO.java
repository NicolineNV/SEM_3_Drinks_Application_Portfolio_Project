package app.dto.request;

public record RegisterRequestDTO(String username, String email, String password) {

    @Override
    public String toString() {
        return "RegisterRequestDTO[username=" + username + ", email=" + email + "]";
    }
}
