package es.urjc.club_tenis.repositories;

import es.urjc.club_tenis.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRespository extends JpaRepository<Tournament, Long>  {

    //Tournament findById(long id);
}
