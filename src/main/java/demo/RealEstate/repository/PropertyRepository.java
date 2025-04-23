package demo.RealEstate.repository;

import demo.RealEstate.model.PropertyDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<PropertyDAO, Long> {
    List<PropertyDAO> findAllByUser_UserId(Long userId);
}
