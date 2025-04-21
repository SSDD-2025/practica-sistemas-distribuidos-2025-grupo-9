package es.urjc.club_tenis.repositories;

import es.urjc.club_tenis.model.Court;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {

    Page<Court> findAll(Pageable pageable);
}
