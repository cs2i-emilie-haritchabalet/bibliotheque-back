package tp.bibliotheque.dto;

public record LoginResponse(
        Long id,
        String nomComplet,
        String email,
        String role,
        String type
) {
}
