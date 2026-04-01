package tp.bibliotheque.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tp.bibliotheque.dto.EmpruntCreateRequest;
import tp.bibliotheque.dto.EmpruntResponse;
import tp.bibliotheque.dto.RetourRequest;
import tp.bibliotheque.service.EmpruntService;

import java.util.List;

@RestController
@RequestMapping("/api/emprunts")
@RequiredArgsConstructor
public class EmpruntController {

    private final EmpruntService empruntService;

    @PostMapping
    public EmpruntResponse emprunter(@Valid @RequestBody EmpruntCreateRequest request) {
        return empruntService.emprunter(request);
    }

    @PostMapping("/retour")
    public EmpruntResponse retourner(@Valid @RequestBody RetourRequest request) {
        return empruntService.retourner(request.empruntId());
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public List<EmpruntResponse> listerParUtilisateur(@PathVariable Long utilisateurId) {
        return empruntService.listerParUtilisateur(utilisateurId);
    }
}
