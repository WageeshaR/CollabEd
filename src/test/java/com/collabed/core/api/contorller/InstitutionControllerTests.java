package com.collabed.core.api.contorller;

import com.collabed.core.api.controller.InstitutionController;
import com.collabed.core.api.util.JwtTokenUtil;
import com.collabed.core.config.SecurityConfig;
import com.collabed.core.data.model.institution.Institution;
import com.collabed.core.service.InstitutionService;
import com.collabed.core.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static com.collabed.core.util.HttpRequestResponseUtils.countMatcher;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InstitutionController.class)
@Import(SecurityConfig.class)
public class InstitutionControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    InstitutionService institutionService;
    @MockBean
    UserService userService;
    @MockBean
    JwtTokenUtil jwtTokenUtil;

    @Test
    public void unauthenticatedAccessTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/institutions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(unauthenticated());
    }

    @ParameterizedTest
    @ValueSource(ints = {1,5,20})
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    public void allTest(int num) throws Exception {
        List<Institution> institutions = new ArrayList<>();
        for (int i=0; i< num; i++)
            institutions.add(Mockito.mock(Institution.class));
        Mockito.when(institutionService.getAll()).thenReturn(institutions);
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/institutions")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(countMatcher(num)));
    }
}
