package br.com.lucasrznd.contractmanagementapi.repositories;

import br.com.lucasrznd.contractmanagementapi.entities.Seller;
import br.com.lucasrznd.contractmanagementapi.specifications.TextSearchSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SellerRepositorySearchTest extends AbstractPostgresRepositoryTest {

    private static final String[] SEARCH_FIELDS = {"name", "phoneNumber"};

    @Autowired
    private SellerRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.saveAll(List.of(
                new Seller(null, "João Silva", "43988887777", "http://img/joao.png"),
                new Seller(null, "Joao Pereira", "43911112222", null),
                new Seller(null, "Maria Conceição", "11977776666", null),
                new Seller(null, "Ana 100% Vendas", "21955554444", null)
        ));
    }

    @Test
    void search_WithoutAccent_FindsAccentedAndUnaccentedValues() {
        assertThat(searchNames("joao")).containsExactlyInAnyOrder("João Silva", "Joao Pereira");
    }

    @Test
    void search_WithAccentAndUpperCase_FindsUnaccentedValues() {
        assertThat(searchNames("JOÃO")).containsExactlyInAnyOrder("João Silva", "Joao Pereira");
        assertThat(searchNames("conceicao")).containsExactly("Maria Conceição");
    }

    @Test
    void search_WithMultipleTerms_RequiresAllTermsToMatch() {
        assertThat(searchNames("silva joao")).containsExactly("João Silva");
    }

    @Test
    void search_ByPartialPhoneNumber_FindsSeller() {
        assertThat(searchNames("98888")).containsExactly("João Silva");
    }

    @Test
    void search_DoesNotMatchFieldsOutsideTheSearchList() {
        assertThat(searchNames("img")).isEmpty();
    }

    @Test
    void search_WithLikeWildcards_TreatsThemAsLiterals() {
        assertThat(searchNames("100%")).containsExactly("Ana 100% Vendas");
        assertThat(searchNames("%")).containsExactly("Ana 100% Vendas");
        assertThat(searchNames("_")).isEmpty();
    }

    @Test
    void search_WithBlankText_ReturnsAll() {
        assertThat(searchNames(null)).hasSize(4);
        assertThat(searchNames("   ")).hasSize(4);
    }

    @Test
    void search_IsPaginated() {
        var page = repository.findAll(TextSearchSpecification.of("joao", SEARCH_FIELDS),
                PageRequest.of(0, 1, Sort.by("name")));

        assertThat(page.getContent()).extracting(Seller::getName).containsExactly("Joao Pereira");
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    void searchIndexes_AreUsedByTheSearchExpression() {
        // Few rows make the planner prefer a seq scan; disable it to check the index matches the expression.
        jdbcTemplate.execute("SET enable_seqscan = off");

        String plan = String.join("\n", jdbcTemplate.queryForList(
                "EXPLAIN SELECT * FROM tb_seller WHERE immutable_unaccent(lower(name)) LIKE '%joao%' " +
                        "OR immutable_unaccent(lower(phone_number)) LIKE '%joao%'", String.class));

        assertThat(plan).contains("idx_seller_name_search").contains("idx_seller_phone_number_search");
    }

    private List<String> searchNames(final String text) {
        return repository.findAll(TextSearchSpecification.of(text, SEARCH_FIELDS))
                .stream().map(Seller::getName).toList();
    }

}
