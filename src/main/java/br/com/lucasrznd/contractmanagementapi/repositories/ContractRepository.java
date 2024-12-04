package br.com.lucasrznd.contractmanagementapi.repositories;

import br.com.lucasrznd.contractmanagementapi.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    @Query("SELECT c FROM Contract c ORDER BY c.id DESC LIMIT 5")
    List<Contract> findLastFiveOrderByIdDesc();

    @Query("SELECT c FROM Contract c WHERE (:companyId IS NULL OR c.clientCompany.id = :companyId) " +
            "AND (:sellerId IS NULL OR c.seller.id = :sellerId) " +
            "AND c.startDate BETWEEN :startDate AND :endDate")
    List<Contract> findByCompanyAndSellerAndDateRange(LocalDate startDate, LocalDate endDate, Long companyId, Long sellerId);

    List<Contract> findByClientCompanyIdAndSellerId(Long companyId, Long sellerId);

}
