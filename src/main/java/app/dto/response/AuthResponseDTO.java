package app.dto.response;

import app.dto.UserDTO;

public record AuthResponseDTO(String token, UserDTO user) {}
