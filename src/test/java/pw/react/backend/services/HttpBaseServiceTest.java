package pw.react.backend.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import pw.react.backend.security.jwt.models.JwtRequest;
import pw.react.backend.security.jwt.models.JwtResponse;
import pw.react.backend.web.CompanyDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles({"it","jwt"})
class HttpBaseServiceTest {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${azure.backend.url}")
    private String azureUrl;

    @Test
    void givenUserNameAndPassword_whenLogin_thenReceiveToken() {
        final ResponseEntity<JwtResponse> response1 = restTemplate.postForEntity(
                azureUrl + "/auth/login", new JwtRequest("pawg", "pw2021"), JwtResponse.class
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + response1.getBody().jwttoken());

        final ResponseEntity<List<CompanyDto>> response3 = restTemplate.exchange(
                azureUrl + "/companies",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );

        assertNotNull(response3.getBody());
    }
}