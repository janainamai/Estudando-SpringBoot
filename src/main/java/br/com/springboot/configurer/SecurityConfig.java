package br.com.springboot.configurer;

import br.com.springboot.service.SystemUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Log4j2
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SystemUserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
//              .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//              .and()
                .authorizeRequests()
                .antMatchers("/books/admin/**").hasRole("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
//                .formLogin()
//                .and()
                .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        log.info("Password encoded {}", passwordEncoder.encode("admin"));

        // Aqui possuímos duas configurações de autenticação
        // O Spring sempre tentará primeiro autenticar em memória, caso não consiga, ele autentica no banco

        // Autenticação em memória
        auth.inMemoryAuthentication()
                .withUser("janainamai")
                .password(passwordEncoder.encode("admin"))
                .roles("USER", "ADMIN")
                .and()
                .withUser("heloisatheiss")
                .password(passwordEncoder.encode("admin"))
                .roles("USER");

        // Autenticação no database
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

}
