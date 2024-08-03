package br.com.lucasrznd.contractmanagementapi.repositories;

import br.com.lucasrznd.contractmanagementapi.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
}
