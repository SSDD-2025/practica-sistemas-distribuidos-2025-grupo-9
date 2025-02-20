package es.urjc.club_tenis.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabasePopulator {

    //Services
    @Autowired
    private UserService service;

    @PostConstruct
    public void init(){
        // Create dummys for demo version
        if(service.findByUsername("admin") == null)
            service.save("admin", "Admin", "admin");
        for(int i = 0; i < 10; i++){
            if(service.findByUsername("user"+i) == null)
                service.save("user"+i, "Usuario " + i, "user"+i);
        }
    }

}
