package tp.bibliotheque.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tp.bibliotheque.dto.RechercheRessourceRequest;
import tp.bibliotheque.dto.RessourceCreateRequest;
import tp.bibliotheque.dto.RessourceResponse;
import tp.bibliotheque.service.RessourceService;

import java.util.List;

@RestController
@RequestMapping("/api/ressources")
@RequiredArgsConstructor
public class RessourceController {

    private final RessourceService ressourceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RessourceResponse creer(@Valid @RequestBody RessourceCreateRequest request) {
        return ressourceService.creer(request);
    }

    @GetMapping("/search")
    public List<RessourceResponse> rechercheSimple(@RequestParam String titre) {
        return ressourceService.rechercheSimple(titre);
    }

    @PostMapping("/advanced-search")
    public List<RessourceResponse> rechercheAvancee(@RequestBody RechercheRessourceRequest request) {
        return ressourceService.rechercheAvancee(request);
    }

    @GetMapping("/{id}")
    public RessourceResponse detail(@PathVariable Long id) {
        return ressourceService.detail(id);
    }
}
