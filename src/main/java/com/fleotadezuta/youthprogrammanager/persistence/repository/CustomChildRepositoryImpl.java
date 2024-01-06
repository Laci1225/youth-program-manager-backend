package com.fleotadezuta.youthprogrammanager.persistence.repository;

import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import reactor.core.publisher.Flux;


@AllArgsConstructor
public class CustomChildRepositoryImpl implements CustomChildRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<ChildDocument> findByFullName(String name) {
        try {
            BasicQuery query = new BasicQuery("{\"$expr\":{\"$or\":[{\"$regexMatch\":{\"input\":{\"$concat\":[\"$familyName\",\" \",\"$givenName\"]},\"regex\":\"" + name + "\",\"options\":\"i\"}},{\"$regexMatch\":{\"input\":{\"$concat\":[\"$givenName\",\" \",\"$familyName\"]},\"regex\":\"" + name + "\",\"options\":\"i\"}}]}}");
            return mongoTemplate.find(query, ChildDocument.class);
        } catch (Exception e) {
            return Flux.error(e);
        }
    }
}

