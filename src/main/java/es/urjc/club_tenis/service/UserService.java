package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

@Service
public class UserService {

    Logger logger = Logger.getLogger("es.urjc.club_tenis.controller");


    @Autowired
    private UserRepository repo;

    @Autowired
    private TournamentService tournamentService;

    public User save(User user){
        return repo.save(user);
    }

    public User findByUsername(String username){
        return repo.findByUsername(username);
    }

    public List <User> findAll(){
        return repo.findAll();
    }

    @Transactional
    public void addPlayedMatch(TennisMatch match){
        addMatch(match, findByUsername(match.getLocal().getUsername()));
        addMatch(match, findByUsername(match.getVisitor().getUsername()));
    }

    private void addMatch(TennisMatch match, User user){
        User savedUser = findByUsername(user.getUsername());
        if(savedUser.getPlayedMatches() == null){
            savedUser.setPlayedMatches(new ArrayList<>());
        }
        if(!savedUser.getPlayedMatches().contains(match)){
            savedUser.getPlayedMatches().add(match);
        }
        save(savedUser);
    }

    public User modify(User oldUser, String newUsername, String newName, MultipartFile profilePicture) throws ChangeSetPersister.NotFoundException {
        User user = findByUsername(oldUser.username);
        if(user == null){
            throw new ChangeSetPersister.NotFoundException();
        }
        user.setUsername(newUsername);
        user.setName(newName);
        if(!profilePicture.isEmpty()) {
            try {
                user.setProfilePicture(BlobProxy.generateProxy(profilePicture.getInputStream(),
                        profilePicture.getSize()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            user.setProfilePicture(null);
        }
        return repo.save(user);
    }

    public User modify(User oldUser, String newUsername, String newName, String oldPassword, String newPassword, MultipartFile profilePicture) throws ChangeSetPersister.NotFoundException {
        User user = findByUsername(oldUser.username);
        if(user == null){
            throw new ChangeSetPersister.NotFoundException();
        }
        if(oldPassword.equals(user.getPassword())){
            user.setPassword(newPassword);
        }
        user.setUsername(newUsername);
        user.setName(newName);
        if(!profilePicture.isEmpty()) {
            try {
                user.setProfilePicture(BlobProxy.generateProxy(profilePicture.getInputStream(),
                        profilePicture.getSize()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            user.setProfilePicture(null);
        }
        return repo.save(user);
    }


    public byte[] getProfilePicture(String username) {
        Blob profilePictureBlob = repo.findByUsername(username).getProfilePicture();
        if(profilePictureBlob == null){
            try {
                return IOUtils.toByteArray(new ClassPathResource("static/images/default_profile.png").getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            return IOUtils.toByteArray(profilePictureBlob.getBinaryStream());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void delete(User user) {
        User deleted = findByUsername("deleted_user");

        List<Tournament> tournaments = user.getTournaments();
        for(int i=0; i<tournaments.size();i++){
            Tournament currentTournament = tournaments.get(i);

            currentTournament.getParticipants().remove(user);
            currentTournament.getParticipants().add(deleted);

            tournamentService.save(currentTournament);
        }

        repo.delete(user);
    }

    @Transactional
    public void removeTournament(User user, Tournament tournament) {
        User saved = findByUsername(user.getUsername());
        saved.getTournaments().remove(tournament);
        repo.save(saved);
    }
}
