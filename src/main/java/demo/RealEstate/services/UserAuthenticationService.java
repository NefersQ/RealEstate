package demo.RealEstate.services;

import demo.RealEstate.model.UserDAO;
import demo.RealEstate.repository.UserRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserAuthenticationService implements UserDetailsService {
    private final UserRep userRepository;

    @Autowired
    public UserAuthenticationService(UserRep userRepository) {

        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDAO user = userRepository.findByUsername(username).get();
        if (user != null){
            return new User(user.getUsername(), user.getPassword(), Collections.emptyList());
        }
        else {
            throw new UsernameNotFoundException("user is not found");
        }
    }
}