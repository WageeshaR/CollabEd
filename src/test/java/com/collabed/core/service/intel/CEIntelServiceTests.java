package com.collabed.core.service.intel;


import com.collabed.core.config.GatewayBeanConfig;
import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.model.user.User;
import com.collabed.core.data.model.user.profile.Profile;
import com.collabed.core.internal.SimpleIntelGateway;
import com.collabed.core.service.intel.criteria.Criteria;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;

import static com.collabed.core.service.util.SecurityUtil.withAuthentication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ContextConfiguration(classes = {GatewayBeanConfig.class, CEIntelService.class})
@ExtendWith(MockitoExtension.class)
class CEIntelServiceTests {
    @Mock
    private SimpleIntelGateway intelGateway;
    @Mock
    private MongoTemplate mongoTemplate;
    @InjectMocks
    private CEIntelService intelService;

    @Test
    void setupGatewayTest() {
        boolean bool = new Random().nextBoolean();
        Mockito.when(intelGateway.initialise()).thenReturn(bool);
        boolean init = intelService.setupGateway();
        assertEquals(init, bool);
    }

    @Test
    void getCuratedListOfTypeTest() throws URISyntaxException {
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
    void getCuratedListOfTypeMongoErrorTest() {
        Mockito.when(mongoTemplate.getCollectionName(Channel.class)).thenThrow(MappingException.class);

        List<?> result = intelService.getCuratedListOfType(Channel.class);
        assertInstanceOf(List.class, result);
        assertEquals(0, result.size());
    }

    @Test
    void getCuratedListOfTypeUriSyntaxErrorTest() throws URISyntaxException {
        Profile profile = new Profile();
        User user = new User();
        user.setProfile(profile);

        Mockito.when(mongoTemplate.getCollectionName(Channel.class)).thenReturn("channel");
        Mockito.when(withAuthentication().getPrincipal()).thenReturn(user);

        Mockito.doThrow(URISyntaxException.class).when(intelGateway).config(Mockito.any(Criteria.class));

        List<?> result = intelService.getCuratedListOfType(Channel.class);
        assertInstanceOf(List.class, result);
        assertEquals(0, result.size());
    }
}
