package com.fleotadezuta.youthprogrammanager.unit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleotadezuta.youthprogrammanager.config.Auth0Service;
import com.fleotadezuta.youthprogrammanager.controller.graphql.ChildController;
import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.fixtures.service.ChildFixture;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;


@GraphQlTest(ChildController.class)
@Import({ChildService.class, ChildParentFacade.class})
public class ChildControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private ChildService childService;

    @MockBean
    private ChildParentFacade childParentFacade;

    @Test
    public void testMyQuery_returnsExpectedData() {

        //language=GraphQL
        String document = "query { getAll }";

        when(childService.getAll())
                .thenReturn(Flux.just("Hello", "World"));

        graphQlTester.document(document)
                .execute()
                .path("getAll")
                .entityList(String.class)
                .satisfies(hello -> assertThat(hello).isEqualTo(List.of("Hello", "World")));
    }
}



/*

        //language=GraphQL
        /*String document = """
                                query {
                                    getAllChildren {
                                        id
                                        familyName
                                        givenName
                                        birthDate
                                        birthPlace
                                        address
                                        diagnosedDiseases {
                                            name
                                            diagnosedAt
                                        }
                                        regularMedicines {
                                            name
                                            dose
                                            takenSince
                                        }
                                        relativeParents {
                                            id
                                            isEmergencyContact
                                        }
                                        hasDiagnosedDiseases
                                        hasRegularMedicines
                                        createdDate
                                        modifiedDate
                                    }
                                }
                """;*/
//language=GraphQL
        /*when(childService.getAllChildren(any()))
                .thenReturn(Flux.fromIterable(ChildFixture.getChildDtoList()));
*/

/*
 public void testGetAllChildren() throws JsonProcessingException {
 *//*
        String query = "{\"query\":\"query { getAllChildren { id name } }\"}";

        when(childService.getAllChildren(any()))
                .thenReturn(Flux.fromIterable(ChildFixture.getChildDtoList()));

       /* client.post()
                .uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildRequest(query))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.getAllChildren").isArray()
                .jsonPath("$.data.getAllChildren[0].id").isNumber()
                .jsonPath("$.data.getAllChildren[0].familyName").isNotEmpty()
                .json(objectMapper.writeValueAsString(ChildFixture.getChildDtoList()));

        verify(childService, times(1)).getAllChildren(any());*/
//}
/*
    private Map<String, Object> buildRequest(String query) {
        Map<String, Object> request = new HashMap<>();
        request.put("query", query);
        return request;
    }
}*/

/*
    @Test
    void testGetAllChildre2n() throws Exception {
        when(childService.getAllChildren(any()))
                .thenReturn(Flux.fromIterable(ChildFixture.getChildDtoList()));

        webClient.post().uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .bodyValue("{\"query\":\"query { getAllChildren { id name } }\"}")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(objectMapper.writeValueAsString(ChildFixture.getChildDtoList()));

        verify(childService, times(1)).getAllChildren(any());
    }

    /*@Test
    void testGetChildById() throws Exception {
        when(childService.getChildById(anyString())).thenReturn();

        mockMvc.perform(
                        post("/graphql")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"query\":\"query { getChildById(id: \\\"1\\\") { id name } }\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.getChildById").isNotEmpty());
    }*/

    /*@Test
    void testAddChild() throws Exception {
        when(childService.addChild(any(ChildCreateDto.class))).thenReturn();

        mockMvc.perform(
                        post("/graphql")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"query\":\"mutation { addChild(child: { name: \\\"Test\\\", age: 10 }) { id name } }\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.addChild").isNotEmpty());
    }*/
/*
    @Test
    void testUpdateChild() throws Exception {
        when(childService.updateChild(any(ChildUpdateDto.class)))
                .thenReturn(Mono.just(ChildFixture.getChildUpdateDto()));

        /*mockMvc.perform(
                        post("/graphql")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"query\":\"mutation { updateChild(child: { id: \\\"1\\\", name: \\\"Updated\\\", age: 12 }) { id name } }\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.updateChild").isNotEmpty());*/
// }
/*
    @Test
    void testDeleteChild() throws Exception {
        when(childService.deleteChild(anyString()))
                .thenReturn(Mono.just(ChildFixture.getChildDto()));

       /* mockMvc.perform(
                        post("/graphql")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"query\":\"mutation { deleteChild(id: \\\"1\\\") { id name } }\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.deleteChild").isNotEmpty());*/
//}

    /*@Test
    void testGetPotentialParents() throws Exception {
        // Mock your service method for fetching potential parents

        mockMvc.perform(
                        post("/graphql")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"query\":\"query { getPotentialParents(name: \\\"John\\\") { id givenName familyName } }\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.getPotentialParents").isArray());
    }*/
//}
