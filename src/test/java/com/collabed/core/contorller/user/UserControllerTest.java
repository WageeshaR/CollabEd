package com.collabed.core.contorller.user;

import com.collabed.core.api.controller.user.UserController;
import com.collabed.core.api.util.JwtTokenFilter;
import com.collabed.core.api.util.JwtTokenUtil;
import com.collabed.core.config.SecurityConfig;
import com.collabed.core.data.dto.UserGroupResponseDto;
import com.collabed.core.data.dto.UserResponseDto;
import com.collabed.core.data.model.User;
import com.collabed.core.data.model.UserGroup;
import com.collabed.core.service.UserService;
import org.bson.types.ObjectId;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static com.collabed.core.JacksonUtils.mapToJson;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService userService;
    @MockBean
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    private MockMvc mockMvc;

    private Matcher<?> getMatcher(int numUsers) {
        return new Matcher<List<?>>() {
            @Override
            public void describeTo(Description description) {}
            @Override
            public boolean matches(Object o) {
                return o instanceof List<?> && ((List<?>) o).size() == numUsers;
            }
            @Override
            public void describeMismatch(Object o, Description description) {}
            @Override
            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {}
        };
    }

    // users
    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_STUDENT", "ROLE_FACILITATOR"})
    public void unauthenticatedAccessTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/users")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(AccessDeniedException.class.getName()));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 20, 100})
    @WithMockUser(username = "testuser", authorities = {"ROLE_ADMIN"})
    public void getAllUsersAdminTest(int numUsers) throws Exception {
        List<User> users = new ArrayList<>();
        for (int i=0; i<numUsers; i++)
            users.add(Mockito.mock(User.class));
        Mockito.when(userService.getAll()).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(getMatcher(numUsers)));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 20, 100})
    @WithMockUser(username = "testuser", authorities = {"ROLE_SUPER_ADMIN"})
    public void getAllUsersSuperAdminTest(int numUsers) throws Exception {
        List<User> users = new ArrayList<>();
        for (int i=0; i<numUsers; i++)
            users.add(Mockito.mock(User.class));
        Mockito.when(userService.getAll()).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(getMatcher(numUsers)));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_STUDENT", "ROLE_FACILITATOR"})
    public void getAllAdminsUnauthorisedTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/admins")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(AccessDeniedException.class.getName()));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 20, 100})
    @WithMockUser(username = "testuser", authorities = {"ROLE_ADMIN"})
    public void getAllAdminsAdminTest(int numUsers) throws Exception {
        List<User> users = new ArrayList<>();
        for (int i=0; i<numUsers; i++)
            users.add(Mockito.mock(User.class));
        Mockito.when(userService.getAll("ROLE_ADMIN")).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/admins")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(getMatcher(numUsers)));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 20, 100})
    @WithMockUser(username = "testuser", authorities = {"ROLE_SUPER_ADMIN"})
    public void getAllAdminsSuperAdminTest(int numUsers) throws Exception {
        List<User> users = new ArrayList<>();
        for (int i=0; i<numUsers; i++)
            users.add(Mockito.mock(User.class));
        Mockito.when(userService.getAll("ROLE_ADMIN")).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/admins")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(getMatcher(numUsers)));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_STUDENT"})
    public void getAllStudentsUnauthorisedTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(AccessDeniedException.class.getName()));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 20, 100})
    @WithMockUser(username = "testuser", authorities = {"ROLE_FACILITATOR"})
    public void getAllStudentsFacilitatorTest(int numUsers) throws Exception {
        List<User> users = new ArrayList<>();
        for (int i=0; i<numUsers; i++)
            users.add(Mockito.mock(User.class));
        Mockito.when(userService.getAll("ROLE_STUDENT")).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(getMatcher(numUsers)));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 20, 100})
    @WithMockUser(username = "testuser", authorities = {"ROLE_ADMIN"})
    public void getAllStudentsAdminTest(int numUsers) throws Exception {
        List<User> users = new ArrayList<>();
        for (int i=0; i<numUsers; i++)
            users.add(Mockito.mock(User.class));
        Mockito.when(userService.getAll("ROLE_STUDENT")).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(getMatcher(numUsers)));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 20, 100})
    @WithMockUser(username = "testuser", authorities = {"ROLE_SUPER_ADMIN"})
    public void getAllStudentsSuperAdminTest(int numUsers) throws Exception {
        List<User> users = new ArrayList<>();
        for (int i=0; i<numUsers; i++)
            users.add(Mockito.mock(User.class));
        Mockito.when(userService.getAll("ROLE_STUDENT")).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(getMatcher(numUsers)));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_STUDENT", "ROLE_FACILITATOR"})
    public void getAllFacilitatorsUnauthorisedTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/facilitators")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(AccessDeniedException.class.getName()));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 20, 100})
    @WithMockUser(username = "testuser", authorities = {"ROLE_ADMIN"})
    public void getAllFacilitatorsAdminTest(int numUsers) throws Exception {
        List<User> users = new ArrayList<>();
        for (int i=0; i<numUsers; i++)
            users.add(Mockito.mock(User.class));
        Mockito.when(userService.getAll("ROLE_FACILITATOR")).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/facilitators")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(getMatcher(numUsers)));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 20, 100})
    @WithMockUser(username = "testuser", authorities = {"ROLE_SUPER_ADMIN"})
    public void getAllFacilitatorsSuperAdminTest(int numUsers) throws Exception {
        List<User> users = new ArrayList<>();
        for (int i=0; i<numUsers; i++)
            users.add(Mockito.mock(User.class));
        Mockito.when(userService.getAll("ROLE_FACILITATOR")).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/facilitators")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(getMatcher(numUsers)));
    }

    // groups
    @Test
    @WithMockUser
    public void createUserGroupTest() throws Exception {
        UserGroup group = new UserGroup();
        group.setName("testgroup");
        group.setRole("ROLE_STUDENT");
        group.setUserIds(List.of(new ObjectId().toHexString()));
        Mockito.when(userService.saveUserGroup(group)).thenReturn(group);
        mockMvc.perform(MockMvcRequestBuilders
                    .post("/users/groups")
                    .content(mapToJson(group))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void addUserToGroup() throws Exception {
        String userId = new ObjectId().toHexString();
        String groupId = new ObjectId().toHexString();
        User user = new User();
        user.setId(userId);
        UserGroup group = new UserGroup();
        group.setId(groupId);
        group.addUsers(userId);
        Mockito.when(userService.addToGroup(userId, groupId)).thenReturn(group);
        mockMvc.perform(MockMvcRequestBuilders
                    .post("/users/groups/add-user")
                    .content(new JSONObject()
                            .put("user_id", userId)
                            .put("group_id", groupId)
                            .toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(groupId));
    }

    @Test
    @WithMockUser
    public void getGroupDetailsTest() throws Exception {
        String id = new ObjectId().toHexString();
        UserGroup group = new UserGroup();
        group.setId(id);
        group.setName("testgroup");
        Mockito.when(userService.loadGroupById(id)).thenReturn(new UserGroupResponseDto(id, "testgroup", List.of(Mockito.mock(UserResponseDto.class))));
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/users/groups/{id}", id)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("testgroup"));
    }
}
