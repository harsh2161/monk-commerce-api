package com.monkcommerce.monkcommerceapi.configurations;

import com.monkcommerce.monkcommerceapi.data_objects.AuthRegisterResponse;
import com.monkcommerce.monkcommerceapi.database_layer.AuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.ExecutionException;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration
{
    @Autowired
    private final AuthenticationRepository authenticateUserRepository;
    @Bean
    public UserDetailsService userDetailsService()
    {
        return username ->
        {
            String userMail = "" + username;
            AuthRegisterResponse user = null;
            try
            {
                user = authenticateUserRepository.findUserByEmail(userMail);
            }
            catch (ExecutionException e)
            {
                throw new RuntimeException(e);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            if(user != null && user.getEmail().equals(userMail))
            {
                return user;
            }
            else
            {
                throw new UsernameNotFoundException("User not found");
            }
        };
    }
    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception
    {
        return config.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
