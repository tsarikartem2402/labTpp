package ttp.lab3;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .map(user -> org.springframework.security.core.userdetails.User
                .withUsername(user.username())
                .password(user.password())
                .authorities(user.role())
                .build())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}