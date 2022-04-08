package uk.ac.shef.uniManager.data.repository;

import uk.ac.shef.uniManager.data.entity.Status;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, UUID> {

}
