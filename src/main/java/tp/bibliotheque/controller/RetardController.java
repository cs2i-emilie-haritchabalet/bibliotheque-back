package tp.bibliotheque.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tp.bibliotheque.dto.EmpruntResponse;
import tp.bibliotheque.dto.RelanceResponse;
import tp.bibliotheque.service.EmpruntService;
import tp.bibliotheque.service.RelanceService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/retards")
@RequiredArgsConstructor
public class RetardController {

    private final EmpruntService empruntService;
    private final RelanceService relanceService;

    @GetMapping
    public List<EmpruntResponse> listerRetards() {
        return empruntService.listerRetards();
    }

    @PostMapping("/{empruntId}/relance")
    public Map<String, String> envoyerRelance(@PathVariable Long empruntId) {
        relanceService.envoyerRelance(empruntId);
        return Map.of("message", "Relance envoyée avec succès.");
    }
}
