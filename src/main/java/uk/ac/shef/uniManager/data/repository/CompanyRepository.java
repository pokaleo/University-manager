package uk.ac.shef.uniManager.data.repository;

import uk.ac.shef.uniManager.data.entity.Company;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

}
