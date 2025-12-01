package com.collabed.core.api.contorller.auth;

import com.collabed.core.api.controller.auth.RegistrationController;
import com.collabed.core.api.util.JwtTokenUtil;
import com.collabed.core.config.SecurityConfig;
import com.collabed.core.data.model.institution.Institution;
import com.collabed.core.data.model.user.User;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.InstitutionService;
import com.collabed.core.service.UserService;
import com.collabed.core.service.util.CEServiceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.collabed.core.util.HttpRequestResponseUtils.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RegistrationController.class)
@Import(SecurityConfig.class)
class RegistrationControllerTests {
    @MockBean
    InstitutionService institutionService;
    @MockBean
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    JwtTokenUtil jwtTokenUtil;
    @MockBean
    UserService userService;
    private User user;
    private Institution institution;

    @BeforeEach
    void setup() {
        // setup institution
        institution = new Institution();
        institution.setName("The University of York");

        //setup user
        user = new User();
        user.setUsername("n.elliot");
        user.setPassword("Password@1234");
        user.setFirstName("Nathon");
        user.setLastName("Elliot");
        user.setEmail("n.elliot@collabed.org");
        user.setInstitution(institution);
        user.setHasAgreedTerms(true);
        user.setHasConsentForDataSharing(false);
    }

    // users
    @ParameterizedTest
    @ValueSource(strings = {"student", "facilitator", "admin"})
    void registerStudentTest(String param) throws Exception {
        String role = "ROLE_" + param.toUpperCase();
        String userString = mapToJson(user);
        User copiedUser = mapFromJson(userString, User.class);
        copiedUser.addRole(role);
        Mockito.when(userService.saveUser(Mockito.any(User.class), Mockito.eq(role))).thenReturn(
                CEServiceResponse.success().data(copiedUser)
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/" + param)
                        .content(userString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("n.elliot"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].authority").value(role));
    }

    @Test
    void registerStudentWithNullUsernameTest() throws Exception {
        user.setUsername(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/student")
                        .content(mapToJson(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[username: must not be null]"));
    }

    @Test
    void registerStudentWithNullPasswordTest() throws Exception {
        user.setPassword(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/student")
                        .content(mapToJson(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[password: must not be null]"));
    }

    @Test
    void registerStudentWithNullEmailTest() throws Exception {
        user.setEmail(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/student")
                        .content(mapToJson(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[email: must not be null]"));
    }

    @Test
    void registerStudentWithDuplicateEmailTest() throws Exception {
        String userString = mapToJson(user);
        User copiedUser = mapFromJson(userString, User.class);
        copiedUser.addRole("ROLE_STUDENT");

        Mockito.when(userService.saveUser(Mockito.any(User.class), Mockito.eq("ROLE_STUDENT"))).thenReturn(
                CEServiceResponse
                        .error()
                        .data(String.format(CEUserErrorMessage.ENTITY_ALREADY_EXISTS, "role"))
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/student")
                        .content(mapToJson(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(String.format(CEUserErrorMessage.ENTITY_ALREADY_EXISTS, "role")));
    }

    // institutions
    @ParameterizedTest
    @ValueSource(strings = {"The University of York", "The University of Manchester"})
    void registerInstitutionTest(String param) throws Exception {
        institution.setName(param);
        String institutionString = mapToJson(institution);
        Mockito.when(institutionService.save(Mockito.any(Institution.class))).thenReturn(
                CEServiceResponse.success().data(institution)
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/institution")
                        .content(institutionString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(param));
    }

    @Test
    void registerInstitutionWithoutNameTest() throws Exception {
        institution.setName(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/institution")
                        .content(mapToJson(institution))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[name: must not be null]"));
    }

    @Test
    void registerInstitutionWithoutAddressTest() throws Exception {
        Mockito.when(institutionService.save(Mockito.any(Institution.class))).thenReturn(
                CEServiceResponse.error().data(new CEWebRequestError(""))
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/institution")
                        .content(mapToJson(institution))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(isException()));
    }
}
