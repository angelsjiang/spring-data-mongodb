package org.springframework.data.mongodb.core.query;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class QueryFiniteStateTests {
    @Test
    void testQueryAddCriteria() {
        Query q = new Query(where("name").is("Thomas").and("age").lt(80));
        q.addCriteria(where("value").is("1"));
        assertThat(q.getQueryObject()).isEqualTo(Document.parse("{\"name\" : \"Thomas\", \"age\" : { \"$lt\" : 80}, \"value\" : \"1\"}"));
    }

    @Test
    void testQuerySetSkip() {
        Query q = new Query(where("name").is("Thomas").and("age").lt(80));
        q.skip(5);
        assertThat(q.getQueryObject()).isEqualTo(Document.parse("{\"name\" : \"Thomas\", \"age\" : { \"$lt\" : 80}}"));
        assertThat(q.getSkip()).isEqualTo(5);
    }
}
