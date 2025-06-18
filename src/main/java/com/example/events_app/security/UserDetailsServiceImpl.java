package com.example.events_app.security;

import com.example.events_app.entity.User;
import com.example.events_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsServiceImpl")//данный класс возвращает SecurityUser, чтобы spring мог сравнить логин и пароль пользователя
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    public final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login)
                .orElseThrow(()-> new UsernameNotFoundException("User doesn`t exit"));
        return SecurityUser.fromUser(user);
    }
}
