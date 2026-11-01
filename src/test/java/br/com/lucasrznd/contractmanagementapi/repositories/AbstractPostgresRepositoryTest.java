package br.com.lucasrznd.contractmanagementapi.repositories;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for repository tests against a real PostgreSQL (Flyway migrations + pg_trgm/unaccent),
 * which H2 cannot emulate. Skipped automatically when Docker is not available.
 *
 * <p>The container is intentionally not annotated with {@code @Container}: Spring starts it once and it is
 * shared by every subclass, keeping the cached Spring context pointing to a live database.
 */
@DataJpaTest(properties = {
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.properties.hibernate.generate_statistics=true"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
abstract class AbstractPostgresRepositoryTest {

    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

}
