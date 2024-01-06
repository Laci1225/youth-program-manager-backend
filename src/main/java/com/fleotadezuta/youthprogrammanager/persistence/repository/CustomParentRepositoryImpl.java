package com.fleotadezuta.youthprogrammanager.persistence.repository;

import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;


@AllArgsConstructor
public class CustomParentRepositoryImpl implements CustomParentRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<ParentDocument> findByFullName(String name) {
        try {
            BasicQuery query = new BasicQuery("{\"$expr\":{\"$or\":[{\"$regexMatch\":{\"input\":{\"$concat\":[\"$familyName\",\" \",\"$givenName\"]},\"regex\":\"" + name + "\",\"options\":\"i\"}},{\"$regexMatch\":{\"input\":{\"$concat\":[\"$givenName\",\" \",\"$familyName\"]},\"regex\":\"" + name + "\",\"options\":\"i\"}}]}}");
            return mongoTemplate.find(query, ParentDocument.class);
        } catch (Exception e) {
            return Flux.error(e);
        }
    }
}

