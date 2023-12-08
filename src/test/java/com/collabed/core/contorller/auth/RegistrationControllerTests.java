package com.collabed.core.contorller.auth;

import com.collabed.core.api.controller.auth.RegistrationController;
import com.collabed.core.data.dto.UserResponseDto;
import com.collabed.core.data.model.Institution;
import com.collabed.core.data.model.User;
import com.collabed.core.service.InstitutionService;
import com.collabed.core.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebAppConfiguration
@Profile({"test"})
public class RegistrationControllerTests {
    private final RegistrationController registrationController;
    private final UserService userService;
    private final InstitutionService institutionService;
    private final BCryptPasswordEncoder passwordEncoder;
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private User user;
    private Institution institution;

    public RegistrationControllerTests() {
        this.userService = Mockito.mock(UserService.class);
        this.institutionService = Mockito.mock(InstitutionService.class);
        this.passwordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
        this.registrationController = new RegistrationController(userService, institutionService, passwordEncoder);
    }
    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }

    @BeforeEach
    public void setup() {
        // setup institution
        institution = new Institution();
        institution.setName("The University of York");
        institution.setAddressId(new ObjectId().toHexString());
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

    @Test
    public void registerStudentTest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Mockito.when(userService.saveUser(user, "STUDENT")).thenReturn(new UserResponseDto(user));
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/register/student")
                        .content(mapToJson(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }
}
