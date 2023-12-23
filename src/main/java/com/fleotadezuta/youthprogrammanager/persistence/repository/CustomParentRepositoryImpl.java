package com.fleotadezuta.youthprogrammanager.persistence.repository;

import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;


@AllArgsConstructor
public class CustomParentRepositoryImpl implements CustomParentRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<ParentDocument> findByFullName(String name) {
        try {
            String[] nameParts = name.trim().split("\\s+");
            Criteria[] criteriaArray = new Criteria[nameParts.length];

            for (int i = 0; i < nameParts.length; i++) {
                criteriaArray[i] = new Criteria().orOperator(
                        Criteria.where("givenName").regex(nameParts[i], "i"),
                        Criteria.where("familyName").regex(nameParts[i], "i")
                );
            }
            Criteria finalCriteria = new Criteria().orOperator(criteriaArray);
            Query query = new Query(finalCriteria);

            return mongoTemplate.find(query, ParentDocument.class);
        } catch (Exception e) {
            return Flux.error(e);
        }
    }
}

