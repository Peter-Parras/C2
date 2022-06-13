package com.techelevator.tenmo.services;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
public class RestTransferService implements TransferService {
    private String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();
    public RestTransferService(String url) {
        this.baseUrl = url;
    }
    @Override
    public void createTransfer(AuthenticatedUser authenticatedUser, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity(transfer, headers);
        String url = baseUrl + "/transfers/" + transfer.getTransferId();

        try { restTemplate.exchange(url, HttpMethod.POST, entity, Transfer.class);
        } catch(RestClientResponseException e) {
            if (e.getMessage().contains("You're broke, bud")) {
                System.out.println("You don't have enough money for that transaction.");
            } else {
                System.out.println(e.getMessage());
            }
        } catch(ResourceAccessException e) {
            System.out.println("Could not complete request due to server network issue. Please try again.");
        }
    }
    @Override
    public Transfer[] getTransfersFromUserId(AuthenticatedUser authenticatedUser, long userId) {
        Transfer[] transfers = null;

        try {
            transfers = restTemplate.exchange(baseUrl + "/transfers/tenmo_user/" + userId,
                    HttpMethod.GET, makeEntity(authenticatedUser), Transfer[].class).getBody();
        } catch(RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        } catch(ResourceAccessException e) {
            System.out.println("Could not complete request due to server network issue. Please try again.");
        }
        return transfers;
    }

    @Override
    public Transfer getTransferFromTransferId(AuthenticatedUser authenticatedUser, int id) {
        Transfer transfer = null;
        try {
            transfer = restTemplate.exchange(baseUrl + "/transfers/" + id,
                    HttpMethod.GET, makeEntity(authenticatedUser), Transfer.class).getBody();
        } catch(RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        } catch(ResourceAccessException e) {
            System.out.println("Could not complete request due to server network issue. Please try again.");
        }
        return transfer;
    }

    @Override
    public Transfer[] getAllTransfers(AuthenticatedUser authenticatedUser) {
        Transfer[] transfers = new Transfer[0];
        try {
            transfers = restTemplate.exchange(baseUrl + "/transfers",
                    HttpMethod.GET, makeEntity(authenticatedUser), Transfer[].class
            ).getBody();
        } catch(RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        } catch(ResourceAccessException e) {
            System.out.println("Could not complete request due to server network issue. Please try again.");
        }
        return transfers;
    }
    private HttpEntity makeEntity(AuthenticatedUser authenticatedUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }
    @Override
    public Transfer[] getPendingTransfersByUserId(AuthenticatedUser authenticatedUser) {
        Transfer[] transfers = null;
        try {
            transfers = restTemplate.exchange(baseUrl + "/transfers/user/" + authenticatedUser.getUser().getId() + "/pending",
                    HttpMethod.GET, makeEntity(authenticatedUser), Transfer[].class).getBody();
        } catch(RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        } catch(ResourceAccessException e) {
            System.out.println("Could not complete request due to server network issue. Please try again.");
        }
        return transfers;
    }
    @Override
    public void updateTransfer(AuthenticatedUser authenticatedUser, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity(transfer, headers);

        String url = baseUrl + "/transfers/" + transfer.getTransferId();
        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, Transfer.class);
        } catch(RestClientResponseException e) {
            if (e.getMessage().contains("You're broke, bud")) {
                System.out.println("You don't have enough money for that transaction.");
            } else {
                System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
            }
        } catch(ResourceAccessException e) {
            System.out.println("Could not complete request due to server network issue. Please try again.");
        }
    }
}


