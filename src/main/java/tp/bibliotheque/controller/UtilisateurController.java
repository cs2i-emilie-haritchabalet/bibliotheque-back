package tp.bibliotheque.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tp.bibliotheque.dto.CautionRequest;
import tp.bibliotheque.dto.UtilisateurCreateRequest;
import tp.bibliotheque.dto.UtilisateurResponse;
import tp.bibliotheque.service.UtilisateurService;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurResponse creer(@Valid @RequestBody UtilisateurCreateRequest request) {
        return utilisateurService.creer(request);
    }

    @GetMapping
    public List<UtilisateurResponse> lister() {
        return utilisateurService.lister();
    }

    @PatchMapping("/{id}/credit-caution")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void crediterCaution(@PathVariable Long id, @Valid @RequestBody CautionRequest request) {
        utilisateurService.crediterCaution(id, request.montant());
    }

    @PatchMapping("/{id}/debit-caution")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void debiterCaution(@PathVariable Long id, @Valid @RequestBody CautionRequest request) {
        utilisateurService.debiterCaution(id, request.montant());
    }
}