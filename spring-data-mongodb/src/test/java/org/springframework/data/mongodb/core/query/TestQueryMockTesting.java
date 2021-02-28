package org.springframework.data.mongodb.core.query;

import org.bson.Document;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.springframework.data.mongodb.test.util.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestQueryMockTesting {

    @Mock
    TextCriteria textCriteriaMock;

    @BeforeEach
    void setUp() {
        textCriteriaMock = TextCriteria.forLanguage("Spanish");
    }

    @Test
    public void testTextQuery() {
        textCriteriaMock = TextCriteria.forLanguage("Spanish")
                                .matching("cake")
                                .caseSensitive(true)
                                .diacriticSensitive(false);

        assertEquals(searchObject(
                "{ \"$language\" : \"Spanish\"," +
                        "\"$search\" : \"cake\"," +
                        "\"$caseSensitive\" : true," +
                        "\"$diacriticSensitive\" : false }"), textCriteriaMock.getCriteriaObject());

        TextQuery tq = TextQuery.queryText(textCriteriaMock);
        tq.includeScore();

        assertThat(tq.getQueryObject()).containsEntry("$text.$language", LANGUAGE_SPANISH);
        assertThat(tq.getQueryObject()).containsEntry("$text.$search", QUERY);
        assertThat(tq.getQueryObject()).containsEntry("$text.$caseSensitive", true);
        assertThat(tq.getQueryObject()).containsEntry("$text.$diacriticSensitive", false);
        assertTrue(tq.getFieldsObject().containsKey("score"));

        tq.setScoreFieldName("foo");

        assertEquals(tq.getScoreFieldName(), "foo");
        assertFalse(tq.getFieldsObject().containsKey("score"));
    }



    private static final String QUERY = "cake";
    private static final String LANGUAGE_SPANISH = "Spanish";

    private Document searchObject(String json) {
        return new Document("$text", Document.parse(json));
    }

}
