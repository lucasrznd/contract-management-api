package br.com.lucasrznd.contractmanagementapi.specifications;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TextSearchSpecificationTest {

    @Test
    void toTerms_RemovesAccentsLowerCasesAndSplitsByWhitespace() {
        assertThat(TextSearchSpecification.toTerms("  João   CONCEIÇÃO\tÁvila "))
                .containsExactly("joao", "conceicao", "avila");
    }

    @Test
    void toTerms_WithBlankText_ReturnsNoTerms() {
        assertThat(TextSearchSpecification.toTerms(null)).isEmpty();
        assertThat(TextSearchSpecification.toTerms("   ")).isEmpty();
    }

    @Test
    void escapeLike_EscapesWildcardsAndEscapeChar() {
        assertThat(TextSearchSpecification.escapeLike("100%_a\\b")).isEqualTo("100\\%\\_a\\\\b");
    }

    @Test
    void of_WithoutFields_ThrowsException() {
        assertThatThrownBy(() -> TextSearchSpecification.of("text"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
