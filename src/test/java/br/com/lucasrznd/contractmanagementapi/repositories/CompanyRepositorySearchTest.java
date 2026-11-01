package br.com.lucasrznd.contractmanagementapi.repositories;

import br.com.lucasrznd.contractmanagementapi.entities.ClientCompany;
import br.com.lucasrznd.contractmanagementapi.specifications.TextSearchSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyRepositorySearchTest extends AbstractPostgresRepositoryTest {

    private static final String[] SEARCH_FIELDS = {"businessName", "tradeName", "registrationNumber", "cpf", "city"};

    @Autowired
    private ClientCompanyRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        repository.saveAll(List.of(
                company("Padaria São José Ltda", "Padaria São José", "11222333000144", null, "Jacarezinho", "padaria@mail.com"),
                company("Auto Peças Ávila Ltda", "Ávila Auto Peças", "55666777000188", null, "Londrina", "avila@mail.com"),
                company("José da Silva", "Mercadinho do Zé", null, "12345678900", "Ourinhos", "ze@mail.com")
        ));
    }

    @Test
    void search_ByTradeNameWithoutAccent_FindsCompany() {
        assertThat(searchTradeNames("avila")).containsExactly("Ávila Auto Peças");
    }

    @Test
    void search_ByTermPresentInDifferentFields_FindsAllMatches() {
        // "jose" is in the trade name of one company and in the business name of another
        assertThat(searchTradeNames("jose")).containsExactlyInAnyOrder("Padaria São José", "Mercadinho do Zé");
    }

    @Test
    void search_WithTermsInDifferentFields_RequiresAllTermsToMatch() {
        assertThat(searchTradeNames("jose jacarezinho")).containsExactly("Padaria São José");
    }

    @Test
    void search_ByPartialCnpjOrCpf_FindsCompany() {
        assertThat(searchTradeNames("666777")).containsExactly("Ávila Auto Peças");
        assertThat(searchTradeNames("456789")).containsExactly("Mercadinho do Zé");
    }

    @Test
    void search_ByCity_FindsCompany() {
        assertThat(searchTradeNames("LONDRINA")).containsExactly("Ávila Auto Peças");
    }

    @Test
    void search_DoesNotMatchFieldsOutsideTheSearchList() {
        assertThat(searchTradeNames("mail.com")).isEmpty();
    }

    @Test
    void searchIndexes_AreUsedByTheSearchExpression() {
        jdbcTemplate.execute("SET enable_seqscan = off");

        String plan = String.join("\n", jdbcTemplate.queryForList(
                "EXPLAIN SELECT * FROM tb_company WHERE " +
                        "immutable_unaccent(lower(business_name)) LIKE '%x%' OR immutable_unaccent(lower(trade_name)) LIKE '%x%' " +
                        "OR immutable_unaccent(lower(registration_number)) LIKE '%x%' OR immutable_unaccent(lower(cpf)) LIKE '%x%' " +
                        "OR immutable_unaccent(lower(city)) LIKE '%x%'", String.class));

        assertThat(plan).contains(
                "idx_company_business_name_search", "idx_company_trade_name_search",
                "idx_company_registration_number_search", "idx_company_cpf_search", "idx_company_city_search");
    }

    private List<String> searchTradeNames(final String text) {
        return repository.findAll(TextSearchSpecification.of(text, SEARCH_FIELDS))
                .stream().map(ClientCompany::getTradeName).toList();
    }

    private static ClientCompany company(String businessName, String tradeName, String cnpj, String cpf, String city, String email) {
        return new ClientCompany(null, businessName, tradeName, cnpj, cpf, null, null, email,
                "Rua Principal", "Centro", 100, city, "Parana", "86400000");
    }

}
