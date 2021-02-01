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

	// --------- new JUnit test Null Check-----------
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


	// --------- new JUnit test -----------
	@Test // DATAMONGO-850
	void shouldCreateSearchFieldCorrectlyForSingleTermCorrectly() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny("bake");

		assertThat(criteria.getCriteriaObject()).isEqualTo(searchObject("{ \"$search\" : \"bake\" }"));
	}

	// --------- new JUnit test -----------
	@Test // DATAMONGO-850
	void shouldThrowExceptionWhenTermDoesNotCompletelyMatch() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny("bake");

		assertThat(criteria.getCriteriaObject()).isNotEqualTo(searchObject("{ \"$search\" : \"coffee bake\" }"));
	}


	// --------- new JUnit test -----------
	@Test // DATAMONGO-850
	void shouldNotEqualWhenTermOrderDoesNotMatchInOrder() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny("coffee", "bake", "cake");

		assertThat(criteria.getCriteriaObject()).isNotEqualTo(searchObject("{ \"$search\" : \"cake coffee bake\" }"));
	}



	@Test // DATAMONGO-850
	void shouldCreateSearchFieldForPhraseCorrectly() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase("coffee cake");

		assertThat(DocumentTestUtils.getAsDocument(criteria.getCriteriaObject(), "$text"))
				.isEqualTo(new Document("$search", "\"coffee cake\""));
	}


	// --------- new JUnit test -----------
	@Test // DATAMONGO-850
	void shouldCreateSearchFieldForLongPhraseCorrectly() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase("coffee cake chicken sandwich orange juice");

		assertThat(DocumentTestUtils.getAsDocument(criteria.getCriteriaObject(), "$text"))
				.isEqualTo(new Document("$search", "\"coffee cake chicken sandwich orange juice\""));
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


	// ------------ New JUnit Test -------------
	@Test // DATAMONGO-850
	void shouldNotPassIfOneTermMatches() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().notMatchingAny("bake", "coffee", "cake");

		assertThat(criteria.getCriteriaObject()).isNotEqualTo(searchObject("{ \"$search\" : \"bake -coffee -cake\" }"));
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

	// ----------- New JUnit test -------------
	@Test // DATAMONGO-1455
	void caseSensitiveOperatorShouldBeSetCorrectlyAndThusNotEqual() {

		TextCriteria criteria = TextCriteria.forDefaultLanguage().matching("coffee").caseSensitive(true);

		assertThat(DocumentTestUtils.getAsDocument(criteria.getCriteriaObject(), "$text"))
				.isNotEqualTo(new Document("$search", "coffee").append("$caseSensitive", false));
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


	@Test // DATAMONGO-2504
	void twoIdenticalCriteriaWithDiffCaseSensitiveShouldNotBeEqual() {

		TextCriteria criteriaOne = TextCriteria.forDefaultLanguage().matching("coffee");
		TextCriteria criteriaTwo = TextCriteria.forDefaultLanguage().matching("coffee");

		assertThat(criteriaOne).isNotEqualTo(criteriaTwo.caseSensitive(false));
	}


	private Document searchObject(String json) {
		return new Document("$text", Document.parse(json));
	}

}
