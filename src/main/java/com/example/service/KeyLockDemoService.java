package com.example.service;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.util.JsonSerialization;
import org.springframework.stereotype.Service;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class KeyLockDemoService
{
    public String createRealm(String realmName)
    {
        // Create a Keycloak client (builder pattern)
        Keycloak kc = createKeyclockClient();

        // See the contents of the access token
        printAccessToken(kc);

        // Create a realm
        String realmResponse = createRealm(kc, realmName);
        return realmResponse;
    }

    public Keycloak createKeyclockClient()
    {
        // Create a Keycloak client (builder pattern)
        Keycloak kc = KeycloakBuilder.builder() //
                .serverUrl("http://localhost:8090/auth")
                .realm("master")
                .username("admin")
                .password("admin")
                .clientId("admin-cli")
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();

        return kc;
    }

    private static void printAccessToken(Keycloak kc)
    {
        try
        {
            String accessTokenString = kc.tokenManager().getAccessToken().getToken();
            System.out.println("accessTokenString: " + accessTokenString);
            JWSInput input = new JWSInput(accessTokenString);
            AccessToken accessToken = input.readJsonContent(AccessToken.class);
            System.out.println("subject: " + accessToken.getSubject());
            System.out.println("preferredUsername: " + accessToken.getPreferredUsername());
            System.out.println("givenName: " + accessToken.getGivenName());
        }
        catch (ClientErrorException e)
        {
            handleClientErrorException(e);
        }
        catch (JWSInputException e)
        {
            e.printStackTrace();
        }
    }

    private static void handleClientErrorException(ClientErrorException e)
    {
        e.printStackTrace();
        Response response = e.getResponse();
        try
        {
            System.out.println("status: " + response.getStatus());
            System.out.println("reason: " + response.getStatusInfo().getReasonPhrase());
            Map error = JsonSerialization.readValue((ByteArrayInputStream) response.getEntity(), Map.class);
            System.out.println("error: " + error.get("error"));
            System.out.println("error_description: " + error.get("error_description"));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private static String createRealm(Keycloak kc, String realmName)
    {
        try
        {
            RealmRepresentation realmRepresentation = new RealmRepresentation();
            realmRepresentation.setRealm(realmName);
            realmRepresentation.setEnabled(Boolean.TRUE);
            kc.realms().create(realmRepresentation);
            System.out.println(realmName + " was created.");
            return realmName;
        }
        catch (ClientErrorException e)
        {
            if (e.getResponse().getStatus() == Response.Status.CONFLICT.getStatusCode())
            {
                System.out.println(realmName + " has already been created.");
            }
            else
            {
                handleClientErrorException(e);
            }
        }
        catch (Exception e)
        {
            Throwable cause = e.getCause();
            if (cause instanceof ClientErrorException)
            {
                handleClientErrorException((ClientErrorException) cause);
            }
            else
            {
                e.printStackTrace();
            }
        }
        return realmName + "has already been created";
    }

    public String createClient(String realmName, String clientName)
    {
        // Create a Keycloak client (builder pattern)
        Keycloak kc = createKeyclockClient();

        // See the contents of the access token
        printAccessToken(kc);

        // Create a realm
        String clientResponse = createClient(kc, realmName, clientName);
        return clientResponse;
    }

    private static String createClient(Keycloak kc, String realmName, String clientName)
    {
        try
        {
            ClientRepresentation clientRepresentation = new ClientRepresentation();
            clientRepresentation.setName(clientName);
            clientRepresentation.setEnabled(Boolean.TRUE);
            kc.realm(realmName).clients().create(clientRepresentation);
            System.out.println(clientName + " was created.");
            return clientName;
        }
        catch (ClientErrorException e)
        {
            if (e.getResponse().getStatus() == Response.Status.CONFLICT.getStatusCode())
            {
                System.out.println(clientName + " has already been created.");
            }
            else
            {
                handleClientErrorException(e);
            }
        }
        catch (Exception e)
        {
            Throwable cause = e.getCause();
            if (cause instanceof ClientErrorException)
            {
                handleClientErrorException((ClientErrorException) cause);
            }
            else
            {
                e.printStackTrace();
            }
        }

        return clientName + "has already been created";
    }
}
