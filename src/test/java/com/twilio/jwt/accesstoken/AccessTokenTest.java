package com.twilio.jwt.accesstoken;

import com.twilio.jwt.Jwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

/**
 * Test class for {@link AccessToken}.
 */
public class AccessTokenTest {

    private static final String ACCOUNT_SID = "AC123";
    private static final String SIGNING_KEY_SID = "SK123";
    private static final String SECRET = "secret";

    private void validateToken(Claims claims) {
        Assert.assertEquals(SIGNING_KEY_SID, claims.getIssuer());
        Assert.assertEquals(ACCOUNT_SID, claims.getSubject());

        Assert.assertNotNull(claims.getExpiration());
        Assert.assertNotNull(claims.getId());
        Assert.assertNotNull(claims.get("grants"));

        Assert.assertTrue(claims.getId().startsWith(claims.getIssuer() + "-"));
        Assert.assertTrue(claims.getExpiration().getTime() > new Date().getTime());
    }

    @Test
    public void testEmptyToken() {
        Jwt token =
            new AccessToken.Builder(ACCOUNT_SID, SIGNING_KEY_SID, SECRET)
                .build();

        Claims claims =
            Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token.toJwt())
                .getBody();

        validateToken(claims);
    }

    @Test
    public void testOptionalValues() {
        Jwt token =
            new AccessToken.Builder(ACCOUNT_SID, SIGNING_KEY_SID, SECRET)
                .identity(ACCOUNT_SID)
                .nbf(new Date())
                .build();

        Claims claims =
            Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token.toJwt())
                .getBody();

        validateToken(claims);
        Assert.assertTrue(claims.getNotBefore().getTime() <= new Date().getTime());
    }

    @Test
    public void testConversationGrant() {
        ConversationsGrant cg = new ConversationsGrant().setConfigurationProfileSid("CP123");
        Jwt token =
            new AccessToken.Builder(ACCOUNT_SID, SIGNING_KEY_SID, SECRET)
                .grant(cg)
                .build();

        Claims claims =
            Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token.toJwt())
                .getBody();

        validateToken(claims);

        Map<String, Object> decodedGrants = (Map<String, Object>) claims.get("grants");
        Assert.assertEquals(1, decodedGrants.size());

        Map<String, Object> grant = (Map<String, Object>) decodedGrants.get("rtc");
        Assert.assertEquals("CP123", grant.get("configuration_profile_sid"));
    }

    @Test
    public void testIpMessagingGrant() {
        IpMessagingGrant ipg = new IpMessagingGrant()
            .setDeploymentRoleSid("RL123")
            .setEndpointId("foobar")
            .setPushCredentialSid("CR123")
            .setServiceSid("IS123");
        Jwt token =
            new AccessToken.Builder(ACCOUNT_SID, SIGNING_KEY_SID, SECRET)
                .grant(ipg)
                .build();

        Claims claims =
            Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token.toJwt())
                .getBody();

        validateToken(claims);

        Map<String, Object> decodedGrants = (Map<String, Object>) claims.get("grants");
        Assert.assertEquals(1, decodedGrants.size());

        Map<String, Object> grant = (Map<String, Object>) decodedGrants.get("ip_messaging");
        Assert.assertEquals("RL123", grant.get("deployment_role_sid"));
        Assert.assertEquals("foobar", grant.get("endpoint_id"));
        Assert.assertEquals("CR123", grant.get("push_credential_sid"));
        Assert.assertEquals("IS123", grant.get("service_sid"));
    }

    @Test
    public void testCompleteToken() {
        IpMessagingGrant ipg = new IpMessagingGrant()
            .setDeploymentRoleSid("RL123")
            .setEndpointId("foobar")
            .setPushCredentialSid("CR123")
            .setServiceSid("IS123");

        ConversationsGrant cg = new ConversationsGrant().setConfigurationProfileSid("CP123");

        Jwt token =
            new AccessToken.Builder(ACCOUNT_SID, SIGNING_KEY_SID, SECRET)
                .grant(ipg)
                .grant(cg)
                .nbf(new Date())
                .build();

        Claims claims =
            Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token.toJwt())
                .getBody();

        validateToken(claims);
        Assert.assertTrue(claims.getNotBefore().getTime() <= new Date().getTime());

        Map<String, Object> decodedGrants = (Map<String, Object>) claims.get("grants");
        Assert.assertEquals(2, decodedGrants.size());

        Map<String, Object> ipmGrant = (Map<String, Object>) decodedGrants.get("ip_messaging");
        Assert.assertEquals("RL123", ipmGrant.get("deployment_role_sid"));
        Assert.assertEquals("foobar", ipmGrant.get("endpoint_id"));
        Assert.assertEquals("CR123", ipmGrant.get("push_credential_sid"));
        Assert.assertEquals("IS123", ipmGrant.get("service_sid"));

        Map<String, Object> cGrant = (Map<String, Object>) decodedGrants.get("rtc");
        Assert.assertEquals("CP123", cGrant.get("configuration_profile_sid"));
    }

}
