package com.fleotadezuta.youthprogrammanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleotadezuta.youthprogrammanager.mapper.EmployeeMapper;
import com.fleotadezuta.youthprogrammanager.model.EmployeeDto;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.persistence.document.EmployeeDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.EmployeeType;
import com.fleotadezuta.youthprogrammanager.persistence.document.Role;
import com.fleotadezuta.youthprogrammanager.persistence.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.slf4j.Logger;
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
    private EmployeeRepository employeeRepository;
    private EmployeeMapper employeeMapper;

    @Value("${auth0.management.audience}")
    private static String audience;
    @Value("${auth0.management.api.token}")
    private static String accessToken;

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
        if (userDetails.getUserType().equals(EmployeeType.ADMINISTRATOR.name())) {
            return employeeRepository.save(employeeMapper.fromEmployeeDtoToEmployeeDocument(employeeDto))
                    .doOnSuccess((employeeDocument) -> {
                        CreateProps(employeeDocument.getEmail(), employeeDocument.getId(), employeeDocument.getGivenName(), employeeDocument.getFamilyName(), audience, accessToken, log, Role.ADMINISTRATOR.getRoleId());
                    })
                    .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
        }
        if (userDetails.getUserType().equals(EmployeeType.RECEPTIONIST.name())) {
            return employeeRepository.save(employeeMapper.fromEmployeeDtoToEmployeeDocument(employeeDto))
                    .doOnSuccess((employeeDocument) -> {
                        CreateProps(employeeDocument.getEmail(), employeeDocument.getId(), employeeDocument.getGivenName(), employeeDocument.getFamilyName(), audience, accessToken, log, Role.RECEPTIONIST.getRoleId());
                    })
                    .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
        }
        if (userDetails.getUserType().equals(EmployeeType.TEACHER.name())) {
            return employeeRepository.save(employeeMapper.fromEmployeeDtoToEmployeeDocument(employeeDto))
                    .doOnSuccess((employeeDocument) -> {
                        CreateProps(employeeDocument.getEmail(), employeeDocument.getId(), employeeDocument.getGivenName(), employeeDocument.getFamilyName(), audience, accessToken, log, Role.TEACHER.getRoleId());
                    })
                    .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
        } else {
            return Mono.error(new RuntimeException("User not authorized to add employee"));
        }
    }

    public static void CreateProps(String email, String id, String givenName, String familyName, String audience, String accessToken, Logger log, String role) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(
                "{\"email\":\"" + email + "\"," +
                        "\"connection\":\"email\"," +
                        "\"app_metadata\":{\"app_user_id\":\"" + id + "\", \"app_user_type\":\"PARENT\"}," +
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
                        .url(audience + "roles/" + role + "/users")
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
