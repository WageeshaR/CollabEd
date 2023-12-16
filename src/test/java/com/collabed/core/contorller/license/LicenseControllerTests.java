package com.collabed.core.contorller.license;

import com.collabed.core.api.controller.license.LicenseController;
import com.collabed.core.api.util.CustomHttpHeaders;
import com.collabed.core.api.util.JwtTokenUtil;
import com.collabed.core.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.collabed.core.HttpRequestResponseUtils.sessionKeyValidator;

@WebMvcTest(controllers = LicenseController.class)
public class LicenseControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    JwtTokenUtil jwtTokenUtil;
    @MockBean
    UserService userService;
    private static final int SESSION_KEY_LEN = 14;

    @Test
    @WithMockUser
    public void initSessionTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/license/init")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.header()
                        .string(CustomHttpHeaders.SESSION_KEY,
                                sessionKeyValidator(SESSION_KEY_LEN)
                ));
    }

    @Test
    public void getOptionsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                    .get("license/options")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.individual").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.group").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.institution").exists());
    }
}
