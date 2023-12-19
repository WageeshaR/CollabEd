package com.collabed.core.api.contorller;

import com.collabed.core.api.controller.LicenseController;
import com.collabed.core.api.util.CustomHttpHeaders;
import com.collabed.core.api.util.JwtTokenUtil;
import com.collabed.core.config.SecurityConfig;
import com.collabed.core.data.model.license.LicenseModel;
import com.collabed.core.data.model.license.LicenseOption;
import com.collabed.core.data.model.license.LicenseType;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.service.license.LicenseService;
import com.collabed.core.service.UserService;
import org.junit.jupiter.api.Test;
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
import java.util.Stack;

import static com.collabed.core.HttpRequestResponseUtils.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LicenseController.class)
@Import(SecurityConfig.class)
public class LicenseControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    JwtTokenUtil jwtTokenUtil;
    @MockBean
    UserService userService;
    @MockBean
    LicenseService licenseService;
    private static final int SESSION_KEY_LEN = 14;

    @Test
    @WithMockUser
    public void initSessionTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/license/get-key")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.header()
                        .string(CustomHttpHeaders.SESSION_KEY,
                                sessionKeyValidator(SESSION_KEY_LEN)
                ));
    }

//    @Test
//    @WithMockUser
//    public void getAllOptionsTest() throws Exception {
//        List<LicenseOption> options = new ArrayList<>();
//        Stack<LicenseType> stack = new Stack<>();
//        stack.push(LicenseType.INDIVIDUAL);
//        stack.push(LicenseType.GROUP);
//        stack.push(LicenseType.INSTITUTIONAL);
//        for (int i=0; i<3; i++) {
//            LicenseModel model = new LicenseModel();
//            model.setType(stack.pop());
//            options.add(new LicenseOption(Integer.toString(i), model));
//        }
//        Mockito.when(licenseService.getAllOptions()).thenReturn(options);
//        mockMvc.perform(MockMvcRequestBuilders
//                    .get("/license/options")
//                    .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
//                .andExpect(MockMvcResultMatchers
//                        .jsonPath("$.*.license_model.license_type")
//                        .value(licenseTypesMatcher()));
//    }
//
//    @Test
//    @WithMockUser
//    public void selectOptionTest() throws Exception {
//        LicenseOption option = new LicenseOption("0", Mockito.mock(LicenseModel.class));
//        Mockito.when(licenseService.initSession(Mockito.any(LicenseOption.class))).thenReturn(true);
//        mockMvc.perform(MockMvcRequestBuilders
//                    .post("/license/select-option")
//                    .content(mapToJson(option))
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser
//    public void selectOptionErrorTest() throws Exception {
//        LicenseOption option = new LicenseOption("0", Mockito.mock(LicenseModel.class));
//        Mockito.when(licenseService.initSession(Mockito.any(LicenseOption.class))).thenThrow(Mockito.mock(CEServiceError.class));
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/license/select-option")
//                        .content(mapToJson(option))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError());
//    }
}
