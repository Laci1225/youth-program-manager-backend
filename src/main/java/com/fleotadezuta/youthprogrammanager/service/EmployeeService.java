package com.fleotadezuta.youthprogrammanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleotadezuta.youthprogrammanager.mapper.EmployeeMapper;
import com.fleotadezuta.youthprogrammanager.model.EmployeeDto;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.persistence.document.EmployeeType;
import com.fleotadezuta.youthprogrammanager.persistence.document.Role;
import com.fleotadezuta.youthprogrammanager.persistence.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Value("${auth0.management.audience}")
    private String audience;
    @Value("${auth0.management.api.token}")
    private String accessToken;

    public Flux<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll()
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public Mono<EmployeeDto> deleteEmployee(UserDetails userDetails, String id) {
        return employeeRepository.findById(id)
                .flatMap(employee -> employeeRepository.deleteById(id)
                        .then(Mono.just(employee)))
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public Mono<EmployeeDto> updateEmployee(UserDetails userDetails, EmployeeDto employeeDto) {
        if (!userDetails.getUserType().equals(employeeDto.getType().name()))
            return Mono.error(new RuntimeException("User not authorized to update employee"));
        return employeeRepository.save(employeeMapper.fromEmployeeDtoToEmployeeDocument(employeeDto))
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public Mono<EmployeeDto> addEmployee(UserDetails userDetails, EmployeeDto employeeDto) {
        log.error(userDetails.getUserType());
        log.error(EmployeeType.ADMINISTRATOR.name());
        if (userDetails.getUserType().equals(EmployeeType.ADMINISTRATOR.name())) {
            return employeeRepository.save(employeeMapper.fromEmployeeDtoToEmployeeDocument(employeeDto))
                    .doOnSuccess((employeeDocument) -> CreateProps(employeeDocument.getEmail(), employeeDocument.getId(), employeeDocument.getGivenName(), employeeDocument.getFamilyName(), Role.ADMINISTRATOR))
                    .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
        }
        if (userDetails.getUserType().equals(EmployeeType.RECEPTIONIST.name())) {
            return employeeRepository.save(employeeMapper.fromEmployeeDtoToEmployeeDocument(employeeDto))
                    .doOnSuccess((employeeDocument) -> CreateProps(employeeDocument.getEmail(), employeeDocument.getId(), employeeDocument.getGivenName(), employeeDocument.getFamilyName(), Role.RECEPTIONIST))
                    .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
        }
        if (userDetails.getUserType().equals(EmployeeType.TEACHER.name())) {
            return employeeRepository.save(employeeMapper.fromEmployeeDtoToEmployeeDocument(employeeDto))
                    .doOnSuccess((employeeDocument) -> CreateProps(employeeDocument.getEmail(), employeeDocument.getId(), employeeDocument.getGivenName(), employeeDocument.getFamilyName(), Role.TEACHER))
                    .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
        } else {
            return Mono.error(new RuntimeException("User not authorized to add employee"));
        }
    }

    public void CreateProps(String email, String id, String givenName, String familyName, Role role) {
        log.error("Audience: " + audience);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(
                "{\"email\":\"" + email + "\"," +
                        "\"connection\":\"email\"," +
                        "\"app_metadata\":{\"app_user_id\":\"" + id + "\", \"app_user_type\":\"" + role.name() + "\"}," +
                        "\"given_name\":\"" + givenName + "\"," +
                        "\"family_name\":\"" + familyName + "\"}",
                mediaType);
        Request request = new Request.Builder()
                .url(audience + "users")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = Objects.requireNonNull(response.body()).string();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String userId = jsonNode.get("user_id").asText();
                log.error("User created with id: " + userId);
                RequestBody body2 = RequestBody.create(
                        "{\"users\":[\"" + userId + "\"]}",
                        mediaType);
                Request request2 = new Request.Builder()
                        .url(audience + "roles/" + role.getRoleId() + "/users")
                        .method("POST", body2)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();
                Response response2 = client.newCall(request2).execute();
                log.error(response2.body().string());
            } else {
                System.err.println("Error: " + response.code() + ", " + response.message());
                System.err.println(response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Mono<EmployeeDto> getEmployeeById(UserDetails userDetails, String id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }
}
