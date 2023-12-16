package com.collabed.core.api.contorller.auth;

import com.collabed.core.data.dto.UserResponseDto;
import com.collabed.core.data.model.Institution;
import com.collabed.core.data.model.User;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.InstitutionService;
import com.collabed.core.service.UserService;
import static com.collabed.core.HttpRequestResponseUtils.mapFromJson;
import static com.collabed.core.HttpRequestResponseUtils.mapToJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Profile({"test"})
public class RegistrationControllerTests {
    /*
    Usual boilerplate code
     */
    @MockBean
    UserService userService;
    @MockBean
    InstitutionService institutionService;
    @MockBean
    BCryptPasswordEncoder passwordEncoder;
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private User user;
    private Institution institution;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // setup institution
        institution = new Institution();
        institution.setName("The University of York");

        //setup user
        user = new User();
        user.setUsername("n.elliot");
        user.setPassword("password1234");
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
    public void registerStudentTest(String param) throws Exception {
        String role = "ROLE_" + param.toUpperCase();
        String userString = mapToJson(user);
        User copiedUser = mapFromJson(userString, User.class);
        copiedUser.addRole(role);
        UserResponseDto dto = new UserResponseDto(copiedUser);

        Mockito.when(userService.saveUser(Mockito.any(User.class), Mockito.eq(role))).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/" + param)
                        .content(userString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("n.elliot"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0]").value(role));
    }

    @Test
    public void registerStudentWithNullUsernameTest() throws Exception {
        user.setUsername(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/student")
                        .content(mapToJson(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value("username: must not be null"));
    }

    @Test
    public void registerStudentWithNullPasswordTest() throws Exception {
        user.setPassword(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/student")
                        .content(mapToJson(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value("password: must not be null"));
    }

    @Test
    public void registerStudentWithNullEmailTest() throws Exception {
        user.setEmail(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/student")
                        .content(mapToJson(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value("email: must not be null"));
    }

    @Test
    public void registerStudentWithDuplicateEmailTest() throws Exception {
        String userString = mapToJson(user);
        User copiedUser = mapFromJson(userString, User.class);
        copiedUser.addRole("ROLE_STUDENT");

        Mockito.when(userService.saveUser(Mockito.any(User.class), Mockito.eq("ROLE_STUDENT"))).thenThrow(DuplicateKeyException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/student")
                        .content(mapToJson(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(DuplicateKeyException.class.getName()));
    }

    // institutions
    @ParameterizedTest
    @ValueSource(strings = {"The University of York", "The University of Manchester"})
    public void registerInstitutionTest(String param) throws Exception {
        institution.setName(param);
        String institutionString = mapToJson(institution);
        Mockito.when(institutionService.save(institution)).thenReturn(institution);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/institution")
                        .content(institutionString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(param));
    }

    @Test
    public void registerInstitutionWithoutNameTest() throws Exception {
        institution.setName(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/institution")
                        .content(mapToJson(institution))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value("name: must not be null"));
    }

    @Test
    public void registerInstitutionWithoutAddressTest() throws Exception {
        Mockito.when(institutionService.save(institution)).thenThrow(CEWebRequestError.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register/institution")
                        .content(mapToJson(institution))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(CEWebRequestError.class.getName()));;
    }
}
