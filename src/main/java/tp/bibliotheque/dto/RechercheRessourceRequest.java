package tp.bibliotheque.dto;

public record RechercheRessourceRequest(
        String titre,
        String auteur,
        Integer anneePublication,
        String theme
) {
}
