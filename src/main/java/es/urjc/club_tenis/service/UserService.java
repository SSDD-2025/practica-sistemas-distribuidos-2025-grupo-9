package es.urjc.club_tenis.service;

import es.urjc.club_tenis.dto.user.UserBasicDTO;
import es.urjc.club_tenis.dto.user.UserMapper;
import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private MatchService matchService;

    @Autowired
    private UserMapper userMapper;

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

    public Page<UserBasicDTO> findAllDTO(int page){
        return repo.findAll(PageRequest.of(page - 1, User.PAGE_SIZE)).map(userMapper::toBasicDTO);
    }


    public Page<User> findAll(int page){
        return repo.findAll(PageRequest.of(page - 1, User.PAGE_SIZE));
    }

    @Transactional
    public void addPlayedMatch(TennisMatch match){
        User local = match.getLocal();
        User visitor = match.getVisitor();
        if(local.getPlayedMatches() == null){
            local.setPlayedMatches(new HashSet<>());
        }
        if(visitor.getPlayedMatches() == null){
            visitor.setPlayedMatches(new HashSet<>());
        }
        if(!local.getPlayedMatches().contains(match)) addMatch(match.getLocal().getUsername(), match);
        if(!visitor.getPlayedMatches().contains(match)) addMatch(match.getVisitor().getUsername(), match);
    }

    private void addMatch(String username, TennisMatch match){
        User savedUser = findByUsername(username);
        savedUser.getPlayedMatches().add(match);
        save(savedUser);
    }

    public User modify(String oldUsername, String newUsername, String newName, MultipartFile profilePicture) throws ChangeSetPersister.NotFoundException {
        User user = findByUsername(oldUsername);
        if(user == null){
            throw new ChangeSetPersister.NotFoundException();
        }
        user.setUsername(newUsername);
        user.setName(newName);
        if(profilePicture != null && !profilePicture.isEmpty()) {
            try {
                user.setProfilePicture(BlobProxy.generateProxy(profilePicture.getInputStream(),
                        profilePicture.getSize()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return repo.save(user);
    }

    public User modify(String oldUsername, String newUsername, String newName, String oldPassword, String newPassword, MultipartFile profilePicture) throws ChangeSetPersister.NotFoundException {
        User user = findByUsername(oldUsername);
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

    @Transactional
    public void delete(String username) {
        User user = findByUsername(username);
        for(TennisMatch match : user.getPlayedMatches()){
            matchService.deleteUser(match.getId(), user);
        }
        repo.delete(user);
    }

    public void updateProfilePicture(User user, MultipartFile profilePicture) throws IOException {
            user.setProfilePicture(BlobProxy.generateProxy(
                    profilePicture.getInputStream(),
                    profilePicture.getSize()
            ));
            repo.save(user);

    }

    @Transactional
    public void removeMatch(String username, TennisMatch match){
        User user = repo.findByUsername(username);
        user.getPlayedMatches().remove(match);
        repo.save(user);
    }
}
