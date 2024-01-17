package com.collabed.core.service.intel;


import com.collabed.core.config.GatewayBeanConfig;
import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.model.user.User;
import com.collabed.core.data.model.user.profile.Profile;
import com.collabed.core.internal.SimpleIntelGateway;
import com.collabed.core.service.intel.criteria.Criteria;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;

import static com.collabed.core.service.util.SecurityUtil.withAuthentication;
import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {GatewayBeanConfig.class, CEIntelService.class})
@SpringBootTest
public class CEIntelServiceTests {
    @MockBean
    private SimpleIntelGateway intelGateway;
    @MockBean
    private MongoTemplate mongoTemplate;
    private final CEIntelService intelService;

    @Autowired
    public CEIntelServiceTests(CEIntelService intelService) {
        this.intelService = intelService;
    }

    @Test
    public void setupGatewayTest() {
        boolean bool = new Random().nextBoolean();
        Mockito.when(intelGateway.initialise()).thenReturn(bool);
        boolean init = intelService.setupGateway();
        assertEquals(init, bool);
    }

    @Test
    public void getCuratedListOfTypeTest() throws URISyntaxException {
        String oid = new ObjectId().toHexString();

        Profile profile = new Profile();
        profile.setPrimaryInterest("primary interest");

        User user = new User();
        user.setId(oid);
        user.setUsername("username");
        user.setProfile(profile);

        Mockito.when(mongoTemplate.getCollectionName(Channel.class)).thenReturn("channel");
        Mockito.when(withAuthentication().getPrincipal()).thenReturn(user);

        Mockito.doNothing().when(intelGateway).config(Mockito.any(Criteria.class));
        Mockito.when(intelGateway.fetchSync(List.class)).thenReturn(true);
        Mockito.when(intelGateway.returnListResult()).thenReturn(List.of());

        Object result = intelService.getCuratedListOfType(Channel.class);

        assertInstanceOf(List.class, result);
    }

    @Test
    public void getCuratedListOfTypeMongoErrorTest() {
        Mockito.when(mongoTemplate.getCollectionName(Channel.class)).thenThrow(MappingException.class);

        Object result = intelService.getCuratedListOfType(Channel.class);
        assertInstanceOf(List.class, result);
        assertEquals(((List<?>) result).size(), 0);
    }

    @Test
    public void getCuratedListOfTypeUriSyntaxErrorTest() throws URISyntaxException {
        Profile profile = new Profile();
        User user = new User();
        user.setProfile(profile);

        Mockito.when(mongoTemplate.getCollectionName(Channel.class)).thenReturn("channel");
        Mockito.when(withAuthentication().getPrincipal()).thenReturn(user);

        Mockito.doThrow(URISyntaxException.class).when(intelGateway).config(Mockito.any(Criteria.class));

        Object result = intelService.getCuratedListOfType(Channel.class);
        assertInstanceOf(List.class, result);
        assertEquals(((List<?>) result).size(), 0);
    }
}
