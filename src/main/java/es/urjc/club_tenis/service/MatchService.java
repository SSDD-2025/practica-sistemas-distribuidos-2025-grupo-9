package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.Court;
import es.urjc.club_tenis.model.TennisMatch;
import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.repositories.MatchRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import java.util.List;
import java.util.logging.Logger;

@Service
public class MatchService {

    Logger logger = Logger.getLogger("es.urjc.club_tenis.controller");

    @Autowired
    private MatchRepository repo;

    @Autowired
    private UserService userService;
    @Autowired
    private ResourceUrlProvider mvcResourceUrlProvider;

    public List<TennisMatch> findAll() {
        return repo.findAll();
    }

    @Transactional
    public TennisMatch save(TennisMatch match){
        User localUser = userService.findByUsername(match.getLocal().getUsername());
        User visitorUser = userService.findByUsername(match.getVisitor().getUsername());

        if (localUser != null && visitorUser != null) {
            TennisMatch aux = repo.save(match);

            logger.info(localUser.toString());
            logger.info(localUser.getPlayedMatches().toString());
            localUser.getPlayedMatches().add(match);

            logger.info(visitorUser.toString());
            logger.info(visitorUser.getPlayedMatches().toString());
            visitorUser.getPlayedMatches().add(match);

            userService.save(localUser);
            userService.save(visitorUser);

            return aux;
        }
        return null;
    }

    public TennisMatch findById(long id) {
        return repo.findById(id).orElse(null);
    }

    public void delete(TennisMatch match) {
        User localUser = userService.findByUsername(match.getLocal().getUsername());
        User visitorUser = userService.findByUsername(match.getVisitor().getUsername());
        localUser.getPlayedMatches().remove(match);
        visitorUser.getPlayedMatches().remove(match);
        userService.save(localUser);
        userService.save(visitorUser);
        repo.delete(match);
    }

    public TennisMatch createMatch(User local, User visitor, Court courtObj, User winner, String result) {
        TennisMatch newMatch = new TennisMatch(local, visitor, courtObj, winner, result);
        return repo.save(newMatch);
    }
}
