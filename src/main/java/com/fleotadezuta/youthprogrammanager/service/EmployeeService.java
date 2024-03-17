package com.fleotadezuta.youthprogrammanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleotadezuta.youthprogrammanager.mapper.EmployeeMapper;
import com.fleotadezuta.youthprogrammanager.model.EmployeeDto;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.persistence.document.EmployeeDocument;
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

    public Flux<EmployeeDto> getAllEmployees(UserDetails userDetails) {
        if (!userDetails.getUserType().equals(Role.ADMINISTRATOR.name()))
            return Flux.error(new RuntimeException("User not authorized to get all employees"));
        return employeeRepository.findAll()
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public Mono<EmployeeDto> deleteEmployee(UserDetails userDetails, String id) {
        if (!userDetails.getUserType().equals(Role.ADMINISTRATOR.name()))
            return Mono.error(new RuntimeException("User not authorized to delete employee"));
        if (id.equals(userDetails.getUserId()))
            return Mono.error(new RuntimeException("Cannot delete self"));
        return employeeRepository.findById(id)
                .flatMap(employee -> {
                    if (employee.getType().equals(Role.ADMINISTRATOR)) {
                        return employeeRepository.findAll()
                                .map(EmployeeDocument::getType)
                                .filter(Role.ADMINISTRATOR::equals)
                                .count()
                                .flatMap(count -> {
                                    if (count < 2)
                                        return Mono.error(new RuntimeException("Cannot delete the last administrator"));
                                    else
                                        return Mono.just(employee);
                                });
                    } else {
                        return Mono.just(employee);
                    }
                }).flatMap(employee -> employeeRepository.deleteById(id).then(Mono.just(employee)))
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto)
                .doOnSuccess((employeeDto -> {
                    OkHttpClient client = new OkHttpClient().newBuilder().build();
                    MediaType mediaType = MediaType.parse("text/plain");
                    RequestBody body = RequestBody.create(mediaType, "");
                    Request request = new Request.Builder()
                            .url(audience + id)
                            .method("DELETE", body)
                            .addHeader("Authorization", "Bearer " + accessToken)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    public Mono<EmployeeDto> updateEmployee(UserDetails userDetails, EmployeeDto employeeDto) {
        if (!userDetails.getUserType().equals(Role.ADMINISTRATOR.name()))
            return Mono.error(new RuntimeException("User not authorized to update employee"));
        return employeeRepository.save(employeeMapper.fromEmployeeDtoToEmployeeDocument(employeeDto))
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public Mono<EmployeeDto> addEmployee(UserDetails userDetails, EmployeeDto employeeDto) {
        if (!userDetails.getUserType().equals(Role.ADMINISTRATOR.name()))
            return Mono.error(new RuntimeException("User not authorized to add employee"));
        return employeeRepository.save(employeeMapper.fromEmployeeDtoToEmployeeDocument(employeeDto))
                .doOnSuccess((employeeDocument) -> CreateProps(employeeDocument.getEmail(), employeeDocument.getId(), employeeDocument.getGivenName(), employeeDocument.getFamilyName(), employeeDocument.getType()))
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public void CreateProps(String email, String id, String givenName, String familyName, Role role) {
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
        if (!userDetails.getUserType().equals(Role.ADMINISTRATOR.name()))
            return Mono.error(new RuntimeException("User not authorized to get employee by id"));
        return employeeRepository.findById(id)
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }
}
