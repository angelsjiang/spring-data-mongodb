package org.springframework.data.mongodb.core.query;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.InvalidMongoDbApiUsageException;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Meta;
import org.springframework.data.mongodb.core.query.Meta.CursorOption;
import org.springframework.data.mongodb.core.query.Query;

public class QueryFiniteStateTests {
    @Test
    void testQueryAddCriteria() {
        Query q = new Query(where("name").is("Thomas").and("age").lt(80));
        q.addCriteria(where("value").is("1"));
        assertThat(q.getQueryObject()).isEqualTo(Document.parse("{\"name\" : \"Thomas\", \"age\" : { \"$lt\" : 80}, \"value\" : \"1\"}"));
    }

    @Test
    void testQuerySkip() {
        Query q = new Query(where("name").is("Thomas").and("age").lt(80));
        q.skip(5);
        assertThat(q.getQueryObject()).isEqualTo(Document.parse("{\"name\" : \"Thomas\", \"age\" : { \"$lt\" : 80}}"));
        assertThat(q.getSkip()).isEqualTo(5);
    }

    @Test
    void testQueryWithHint() {
        Query q = new Query(where("name").is("Thomas").and("age").lt(80));
        q.withHint("this is a hint");
        assertThat(q.getQueryObject()).isEqualTo(Document.parse("{\"name\" : \"Thomas\", \"age\" : { \"$lt\" : 80}}"));
        assertThat(q.getHint()).isEqualTo("this is a hint");
    }

    @Test
    void testQueryAddCriteriaAndSetSkip() {
        Query q = new Query(where("name").is("Thomas").and("age").lt(80));
        q.addCriteria(where("value").is("1"));
        q.skip(5);
        assertThat(q.getQueryObject()).isEqualTo(Document.parse("{\"name\" : \"Thomas\", \"age\" : { \"$lt\" : 80}, \"value\" : \"1\"}"));
        assertThat(q.getSkip()).isEqualTo(5);
    }

    @Test
    void testQueryCombineOperation() {
        Query q = new Query(where("name").is("Thomas").and("age").lt(80));
        q.skip(5).limit(1);
        assertThat(q.getQueryObject()).isEqualTo(Document.parse("{ \"name\" : \"Thomas\" , \"age\" : { \"$lt\" : 80}}"));

        assertThat(q.getSkip()).isEqualTo(5);
        assertThat(q.getLimit()).isEqualTo(1);
    }

    @Test
    void testQueryMetaMaxTime(){
        Query q = new Query();
        Duration time = Duration.ofSeconds(10);
        q.maxTime(time);
        assertThat(q.getMeta().getMaxTimeMsec()).isEqualTo(time.toMillis());
    }

    @Test
    void testQueryNotSorted(){
        Query q = new Query();
        assertThat(q.isSorted()).isEqualTo(false);
    }

    @Test
    void testQueryRestrictedTypes() {
        Query q = new Query();
        Set<Class<?>> c = new HashSet<>();
        c.add(String.class);
        c.add(Integer.class);
        c.add(Long.class);
        q.restrict(String.class, Integer.class, Long.class);
        assertThat(q.getRestrictedTypes()).isEqualTo(c);
    }

    @Test
    void testQueryMetaComment(){
        Query q = new Query();
        String com = "this is a comment";
        q.comment(com);
        assertThat(q.getMeta().getComment()).isEqualTo(com);
    }

    @Test
    void testQueryMetaExhaust(){
        Query q = new Query();
        q.exhaust();
        Set<CursorOption> c = new HashSet<>();
        c.add(Meta.CursorOption.EXHAUST);
        assertThat(q.getMeta().getFlags()).isEqualTo(c);
    }
}
