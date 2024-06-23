package com.fleotadezuta.youthprogrammanager.unit.controller;

import com.fleotadezuta.youthprogrammanager.controller.graphql.ChildController;
import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.fixtures.service.ChildFixture;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

    /*@Test
    public void testGetAllChildren_returnsExpectedData() {

        //language=GraphQL
        String document =
                """
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
                        }""";
        //query { getAllChildren { id familyName givenName birthDate birthPlace address diagnosedDiseases { name diagnosedAt } regularMedicines { name dose takenSince } relativeParents { id isEmergencyContact } hasDiagnosedDiseases hasRegularMedicines createdDate modifiedDate } }""";

        when(childService.getAllChildren(any(UserDetails.class)))
                .thenReturn(Flux.just(ChildFixture.getChildDto()));

        graphQlTester.document(document)
                .execute()
                .path("getAllChildren")
                .entityList(ChildDto.class)
                .satisfies(children -> assertThat(children).containsExactly(ChildFixture.getChildDto()));
    }
*/
    @Test
    public void testUpdateChild_returnsExpectedData() {
        //language=GraphQL
        String document = "mutation { updateChild(child: { id: \"child123\", familyName: \"Doe\", givenName: \"John\", birthDate: \"2010-05-15T00:00:00\", birthPlace: \"Anytown, USA\", address: \"123 Main St, Anytown, USA\", relativeParents: [{ id: \"parent123\", isEmergencyContact: true }], diagnosedDiseases: [{ name: \"Flu\", diagnosedAt: \"2023-01-01T00:00:00\" }], regularMedicines: [{ name: \"Medicine A\", dose: \"10mg\", takenSince: \"2023-01-01T00:00:00\" }] }) { id familyName givenName birthDate birthPlace address diagnosedDiseases { name diagnosedAt } regularMedicines { name dose takenSince } relativeParents { id isEmergencyContact } hasDiagnosedDiseases hasRegularMedicines createdDate modifiedDate } }";

        when(childService.updateChild(any(ChildUpdateDto.class)))
                .thenReturn(Mono.just(ChildFixture.getChildUpdateDto()));

        graphQlTester.document(document)
                .execute()
                .path("updateChild")
                .entity(ChildUpdateDto.class)
                .satisfies(child -> assertThat(child).isEqualTo(ChildFixture.getChildUpdateDto()));
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
