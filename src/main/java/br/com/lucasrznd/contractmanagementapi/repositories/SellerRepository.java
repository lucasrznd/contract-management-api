package br.com.lucasrznd.contractmanagementapi.repositories;

import br.com.lucasrznd.contractmanagementapi.entities.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}
