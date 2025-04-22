package es.urjc.club_tenis.repositories;

import es.urjc.club_tenis.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long>  {

    Page<Tournament> findAll(Pageable pageable);
}
