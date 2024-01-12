package com.fleotadezuta.youthprogrammanager.persistence.repository;

import com.fleotadezuta.youthprogrammanager.persistence.document.TicketDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketTypeDocument;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import reactor.core.publisher.Flux;


@AllArgsConstructor
public class CustomTicketTypeRepositoryImpl implements CustomTicketTypeRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<TicketTypeDocument> findByName(String name) {
        try {
            BasicQuery query = new BasicQuery("{\"$expr\":{\"$regexMatch\":{\"input\":\"$name\",\"regex\":\"" + name + "\",\"options\":\"i\"}}}");
            return mongoTemplate.find(query, TicketTypeDocument.class);
        } catch (Exception e) {
            return Flux.error(e);
        }
    }
}

