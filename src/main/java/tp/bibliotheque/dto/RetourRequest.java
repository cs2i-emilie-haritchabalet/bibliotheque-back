package tp.bibliotheque.dto;

import jakarta.validation.constraints.NotNull;

public record RetourRequest(@NotNull Long empruntId) {
}
