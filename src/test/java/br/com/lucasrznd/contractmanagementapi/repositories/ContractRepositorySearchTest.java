package br.com.lucasrznd.contractmanagementapi.repositories;

import br.com.lucasrznd.contractmanagementapi.entities.ClientCompany;
import br.com.lucasrznd.contractmanagementapi.entities.Contract;
import br.com.lucasrznd.contractmanagementapi.entities.Seller;
import br.com.lucasrznd.contractmanagementapi.specifications.TextSearchSpecification;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ContractRepositorySearchTest extends AbstractPostgresRepositoryTest {

    private static final String[] SEARCH_FIELDS = {"clientCompany.tradeName", "clientCompany.businessName", "seller.name", "advertisingOrder"};

    @Autowired
    private ContractRepository repository;

    @Autowired
    private ClientCompanyRepository companyRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        ClientCompany padaria = companyRepository.save(company("Padaria São José Ltda", "Padaria São José"));
        ClientCompany avila = companyRepository.save(company("Auto Peças Ávila Ltda", "Ávila Auto Peças"));
        Seller joao = sellerRepository.save(new Seller(null, "João Teodoro", "43988887777", null));
        Seller maria = sellerRepository.save(new Seller(null, "Maria Conceição", "43911112222", null));

        repository.saveAll(List.of(
                contract(padaria, joao, "PI-2026-001"),
                contract(padaria, maria, "PI-2026-002"),
                contract(avila, joao, "PI-2026-003"),
                contract(null, null, "PI-SEM-EMPRESA")
        ));

        // Forces the searches to hit the database instead of the persistence context
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void search_ByCompanyTradeName_FindsContracts() {
        assertThat(searchOrders("padaria")).containsExactlyInAnyOrder("PI-2026-001", "PI-2026-002");
    }

    @Test
    void search_ByCompanyBusinessName_FindsContracts() {
        assertThat(searchOrders("avila ltda")).containsExactly("PI-2026-003");
    }

    @Test
    void search_BySellerNameWithoutAccent_FindsContracts() {
        assertThat(searchOrders("joao")).containsExactlyInAnyOrder("PI-2026-001", "PI-2026-003");
    }

    @Test
    void search_WithTermsInDifferentAssociations_RequiresAllTermsToMatch() {
        assertThat(searchOrders("padaria conceicao")).containsExactly("PI-2026-002");
    }

    @Test
    void search_ByAdvertisingOrder_KeepsContractsWithoutCompanyOrSeller() {
        assertThat(searchOrders("sem-empresa")).containsExactly("PI-SEM-EMPRESA");
    }

    @Test
    void search_WithBlankText_ReturnsAllContracts() {
        assertThat(searchOrders(null)).hasSize(4);
    }

    @Test
    void findAll_FetchesCompanyAndSellerWithoutNPlusOneQueries() {
        Statistics statistics = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        var page = repository.findAll(TextSearchSpecification.of(null, SEARCH_FIELDS), PageRequest.of(0, 10, Sort.by("id")));
        page.getContent().forEach(contract -> {
            if (contract.getClientCompany() != null) contract.getClientCompany().getTradeName();
            if (contract.getSeller() != null) contract.getSeller().getName();
        });

        assertThat(page.getContent()).hasSize(4);
        // A single select for the page (no count is needed because the page is not full)
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    void findAll_WithTextAndPagination_ReturnsTotalCount() {
        var page = repository.findAll(TextSearchSpecification.of("pi-2026", SEARCH_FIELDS), PageRequest.of(0, 2, Sort.by("id")));

        assertThat(page.getContent()).extracting(Contract::getAdvertisingOrder).containsExactly("PI-2026-001", "PI-2026-002");
        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    private List<String> searchOrders(final String text) {
        return repository.findAll(TextSearchSpecification.of(text, SEARCH_FIELDS), PageRequest.of(0, 50))
                .stream().map(Contract::getAdvertisingOrder).toList();
    }

    private static ClientCompany company(String businessName, String tradeName) {
        return new ClientCompany(null, businessName, tradeName, null, null, null, null, null,
                null, null, null, "Jacarezinho", "Parana", null);
    }

    private static Contract contract(ClientCompany company, Seller seller, String advertisingOrder) {
        return new Contract(null, company, seller, advertisingOrder, 30.0, 1, 0.0, 0,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 600.0, 0, null, null, 10, null, null, null);
    }

}
