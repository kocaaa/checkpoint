package imi.spring.backend.services.implementations;

import imi.spring.backend.models.*;
import imi.spring.backend.repositories.AppUserRepository;
import imi.spring.backend.services.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
//@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public AppUser saveUser(AppUser user) {

        if(appUserRepository.findByUsername(user.getUsername()) != null){
            log.error("Username {} is taken", user.getUsername());
            throw new BadCredentialsException("Username is taken");
        }

        if(appUserRepository.findByEmail(user.getEmail()) != null){
            log.error("Email {} is taken", user.getEmail());
            throw new BadCredentialsException("Email is taken");
        }

        log.info("Saving user {} to database.", user.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return appUserRepository.save(user);
    }

    @Override
    public AppUser getUserByUsername(String username) {
        log.info("Getting user - {} from database.", username);
        return appUserRepository.findByUsername(username);
    }

    @Override
    public AppUser getUserById(Long id) {
        log.info("Getting user with id {} from database.", id);
        return appUserRepository.findById(id).orElse(null);
    }

    @Override
    public List<AppUser> getAllUsers() {
        log.info("Getting all users from database.");
        return appUserRepository.findAll();
    }

    @Override
    public AppUser updateUser(AppUser user) {
        return appUserRepository.save(user);
    }

    @Override
    public String changeUserEmail(AppUser appUser, String newEmail) {
        //regex, provera duzina...
        appUser.setEmail(newEmail.trim());
        updateUser(appUser);
        return "Changed";
    }

    @Override
    public String changeUserPassword(AppUser appUser, String[] passwords) {
        String oldPassword = passwords[0];
        String newPassword = passwords[1];

        if (bCryptPasswordEncoder.matches(oldPassword, appUser.getPassword())) {
            appUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
            log.info("Changing user password for user [{}].", appUser.getUsername());
            updateUser(appUser);
            return "Changed";
        }

        log.error("Incorrect old password.");
        return "Invalid old password";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByUsername(username);

        log.info("Username is {}", username);

        if(user==null){
            log.error("User {} not found in database", username);
            throw new UsernameNotFoundException("User not found.");
        }
        else{
            log.info("User {} found in database", username);
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));

        return new User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public UserDetailedDTO convertAppUserToUserDetailedDTO(AppUser appUser) {
        return new UserDetailedDTO(appUser);
    }

    @Override
    public List<UserDetailedDTO> convertListOfAppUsersToUserDetailedDTOs(List<AppUser> appUsers) {
        List<UserDetailedDTO> userDetailedDTOs = new ArrayList<>();

        for(AppUser appUser : appUsers){
            userDetailedDTOs.add(convertAppUserToUserDetailedDTO(appUser));
        }

        return userDetailedDTOs;
    }
}
