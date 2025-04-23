package demo.RealEstate.repository;

import demo.RealEstate.model.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDAO, Long> {

    Optional<UserDAO> findByUsername(String username);

    Optional<UserDAO> findByEmail(String email);

    Optional<UserDAO> findByUsernameOrEmail(String username, String email);

}