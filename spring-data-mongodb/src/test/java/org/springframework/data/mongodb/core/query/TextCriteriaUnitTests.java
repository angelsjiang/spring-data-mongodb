/*
 * Copyright 2014-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mongodb.core.query;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.DocumentTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Unit tests for {@link TextCriteria}.
 *
 * @author Christoph Strobl
 * @author Daniel Debray
 */
class TextCriteriaUnitTests {

	@Test // DATAMONGO-850
	void shouldNotHaveLanguageField() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage();

		assertThat(criteria.getCriteriaObject()).isEqualTo(searchObject("{ }"));
	}

	@Test // DATAMONGO-850
	void shouldNotHaveLanguageForNonDefaultLanguageField() {

		TextCriteria criteria = TextCriteria.forLanguage("spanish");

		assertThat(criteria.getCriteriaObject()).isEqualTo(searchObject("{ \"$language\" : \"spanish\" }"));
	}

	// --------- new JUnit test -----------
	@Test // DATAMONGO-850
	void shouldNotTakeEmptyStringForLanguageField() {

		try {
			TextCriteria criteria = TextCriteria.forLanguage("");
			fail("java.lang.IllegalArgumentException: Language must not be null or empty!");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage());
		}
	}


	@Test // DATAMONGO-850
	void shouldCreateSearchFieldForSingleTermCorrectly() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().matching("cake");

		assertThat(criteria.getCriteriaObject()).isEqualTo(searchObject("{ \"$search\" : \"cake\" }"));
	}


	// --------- new JUnit test -----------
	@Test // DATAMONGO-850
	void shouldCreateSearchFieldForPhraseCorrectlyForMatching() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().matching("coffee cake");

		assertThat(criteria.getCriteriaObject()).isEqualTo(searchObject("{ \"$search\" : \"coffee cake\" }"));
	}


	@Test // DATAMONGO-850
	void shouldCreateSearchFieldCorrectlyForMultipleTermsCorrectly() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny("bake", "coffee", "cake");

		assertThat(criteria.getCriteriaObject()).isEqualTo(searchObject("{ \"$search\" : \"bake coffee cake\" }"));
	}

	@Test // DATAMONGO-850
	void shouldCreateSearchFieldForPhraseCorrectly() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase("coffee cake");

		assertThat(DocumentTestUtils.getAsDocument(criteria.getCriteriaObject(), "$text"))
				.isEqualTo(new Document("$search", "\"coffee cake\""));
	}

	@Test // DATAMONGO-850
	void shouldCreateNotFieldCorrectly() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().notMatching("cake");

		assertThat(criteria.getCriteriaObject()).isEqualTo(searchObject("{ \"$search\" : \"-cake\" }"));
	}

	@Test // DATAMONGO-850
	void shouldCreateSearchFieldCorrectlyForNotMultipleTermsCorrectly() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().notMatchingAny("bake", "coffee", "cake");

		assertThat(criteria.getCriteriaObject()).isEqualTo(searchObject("{ \"$search\" : \"-bake -coffee -cake\" }"));
	}

	@Test // DATAMONGO-850
	void shouldCreateSearchFieldForNotPhraseCorrectly() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().notMatchingPhrase("coffee cake");

		assertThat(DocumentTestUtils.getAsDocument(criteria.getCriteriaObject(), "$text"))
				.isEqualTo(new Document("$search", "-\"coffee cake\""));
	}

	@Test // DATAMONGO-1455
	void caseSensitiveOperatorShouldBeSetCorrectly() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().matching("coffee").caseSensitive(true);

		assertThat(DocumentTestUtils.getAsDocument(criteria.getCriteriaObject(), "$text"))
				.isEqualTo(new Document("$search", "coffee").append("$caseSensitive", true));
	}

	@Test // DATAMONGO-1456
	void diacriticSensitiveOperatorShouldBeSetCorrectly() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().matching("coffee").diacriticSensitive(true);

		assertThat(DocumentTestUtils.getAsDocument(criteria.getCriteriaObject(), "$text"))
				.isEqualTo(new Document("$search", "coffee").append("$diacriticSensitive", true));
	}

	@Test // DATAMONGO-2504
	void twoIdenticalCriteriaShouldBeEqual() {

		TextCriteria criteriaOne = TextCriteria.forDefaultLanguage().matching("coffee");
		TextCriteria criteriaTwo = TextCriteria.forDefaultLanguage().matching("coffee");

		assertThat(criteriaOne).isEqualTo(criteriaTwo);
		assertThat(criteriaOne).hasSameHashCodeAs(criteriaTwo);
		assertThat(criteriaOne).isNotEqualTo(criteriaTwo.diacriticSensitive(false));
		assertThat(criteriaOne.hashCode()).isNotEqualTo(criteriaTwo.diacriticSensitive(false).hashCode());
	}

	private Document searchObject(String json) {
		return new Document("$text", Document.parse(json));
	}

}
