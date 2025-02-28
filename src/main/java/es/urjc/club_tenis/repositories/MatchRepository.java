package es.urjc.club_tenis.repositories;

import es.urjc.club_tenis.model.TennisMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<TennisMatch, Long> {

}
