package app.dto;

public record LoginRequestDTO(String username, String password) {

    @Override
    public String toString() {
        return "loginRequestDTO[username=" + username + "]";
    }
}
