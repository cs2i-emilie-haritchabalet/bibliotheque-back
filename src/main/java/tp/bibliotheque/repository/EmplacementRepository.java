package tp.bibliotheque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tp.bibliotheque.entity.Emplacement;

import java.util.Optional;

public interface EmplacementRepository extends JpaRepository<Emplacement, Long> {
    Optional<Emplacement> findByCode(String code);
}
