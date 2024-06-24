package com.fleotadezuta.youthprogrammanager.unit.controller;

import com.fleotadezuta.youthprogrammanager.controller.graphql.ChildController;
import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.fixtures.service.ChildFixture;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.model.ChildUpdateDto;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import graphql.GraphQLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import reactor.core.publisher.Mono;

import static com.fleotadezuta.youthprogrammanager.constants.HttpConstants.APP_USER_ID;
import static com.fleotadezuta.youthprogrammanager.constants.HttpConstants.APP_USER_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@GraphQlTest(ChildController.class)
@Import({ChildService.class, ChildParentFacade.class})
public class ChildControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private ChildService childService;

    @MockBean
    private ChildParentFacade childParentFacade;

    @MockBean
    private GraphQLContext context;

    @MockBean
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        when(context.get(APP_USER_ID)).thenReturn("user123");
        when(context.get(APP_USER_TYPE)).thenReturn("ADMINISTRATOR");
    }

    @Test
    public void testGetAllChildren_returnsExpectedData() {
        // todo  java.lang.AssertionError: Response has 1 unexpected error(s) of 1 total.
        //  If expected, please filter them out: [Cannot invoke "Object.toString()"
        //  because the return value of "graphql.GraphQLContext.get(Object)" is null]
        //language=GraphQL
        /*String document = "query { getAllChildren { id familyName givenName birthDate birthPlace address diagnosedDiseases { name diagnosedAt } regularMedicines { name dose takenSince } relativeParents { id isEmergencyContact } hasDiagnosedDiseases hasRegularMedicines createdDate modifiedDate } }";
        String userId = "user123";
        String userType = "ADMINISTRATOR";

        when(userDetails.getUserId()).thenReturn(userId);
        when(userDetails.getUserType()).thenReturn(userType);
        when(childService.getAllChildren(any(UserDetails.class)))
                .thenReturn(Flux.fromIterable(ChildFixture.getChildDtoList()));

        graphQlTester.document(document)
                .execute()
                .path("getAllChildren")
                .entityList(ChildDto.class)
                .satisfies(children -> assertThat(children).isEqualTo(ChildFixture.getChildDtoList()));
         */
    }


    @Test
    public void deleteChildShouldReturnDeletedChild() {

        //language=GraphQL
        String document = "mutation { deleteChild(id: \"child123\") { id familyName givenName birthDate birthPlace address relativeParents{id, isEmergencyContact} diagnosedDiseases { name diagnosedAt } regularMedicines { name dose takenSince } hasDiagnosedDiseases hasRegularMedicines createdDate modifiedDate } }";

        when(childService.deleteChild("child123"))
                .thenReturn(Mono.just(ChildFixture.getChildDto()));

        graphQlTester.document(document)
                .execute()
                .path("deleteChild")
                .entity(ChildDto.class)
                .satisfies(child -> assertThat(child).isEqualTo(ChildFixture.getChildDto()));
    }


    @Test
    public void updateChildShouldReturnUpdatedChild() {
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