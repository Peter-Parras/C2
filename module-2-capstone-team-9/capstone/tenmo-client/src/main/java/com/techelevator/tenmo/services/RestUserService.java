package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
public class RestUserService implements UserService {
    private RestTemplate restTemplate;
    private final String BASE_URL = "http://localhost:8080/";
    public RestUserService(String apiBaseUrl) {
        this.restTemplate = new RestTemplate();
    }
    @Override
    public User[] getAllUsers(AuthenticatedUser authenticatedUser) {
        User[] users = null;
        try {users = restTemplate.exchange(BASE_URL + "/users", HttpMethod.GET, makeEntity(authenticatedUser), User[].class).getBody();
        } catch(RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        } catch(ResourceAccessException e) {
            System.out.println("Could not complete request due to server network issue. Please try again.");
        }
        return users;
    }
    @Override
    public User getUserByUserId(AuthenticatedUser authenticatedUser, int id) {
        User user = null;
        try {
            user = restTemplate.exchange(BASE_URL + "/users/" + id, HttpMethod.GET, makeEntity(authenticatedUser), User.class).getBody();
        } catch(RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        } catch(ResourceAccessException e) {
            System.out.println("Could not complete request due to server network issue. Please try again.");
        }
        return user;
    }
    private HttpEntity makeEntity(AuthenticatedUser authenticatedUser) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(authenticatedUser.getToken());
        HttpEntity entity = new HttpEntity(httpHeaders);
        return entity;
    }
}
