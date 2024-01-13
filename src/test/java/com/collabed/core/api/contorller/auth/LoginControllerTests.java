package com.collabed.core.api.contorller.auth;

import com.collabed.core.api.controller.auth.LoginController;
import com.collabed.core.api.util.JwtTokenUtil;
import com.collabed.core.config.SecurityConfig;
import com.collabed.core.data.model.user.User;
import com.collabed.core.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = LoginController.class)
@Import(SecurityConfig.class)
public class LoginControllerTests {
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private Authentication authentication;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    JwtTokenUtil jwtTokenUtil;
    @MockBean
    UserService userService;
    private User user;

    @BeforeEach
    public void setup() {
        user = new User("testuser", "STUDENT");
    }

    @Test
    public void authenticationTest() throws Exception {
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);

        Mockito.when(jwtTokenUtil.generateToken(user)).thenReturn(
                Jwts.builder()
                        .setClaims(Jwts.claims())
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000))
                        .signWith(SignatureAlgorithm.HS256, "dummy-secret")
                        .compact()
        );

        mockMvc.perform(MockMvcRequestBuilders
                    .post("/login")
                    .content(new JSONObject()
                            .put("username", "testuser")
                            .put("password", "password")
                            .toString()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION));
    }

    @Test
    public void failedAuthenticationTest() throws Exception {
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);
        mockMvc.perform(MockMvcRequestBuilders
                    .post("/login")
                    .content(new JSONObject()
                            .put("username", "testuser")
                            .put("password", "pasword")
                            .toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
