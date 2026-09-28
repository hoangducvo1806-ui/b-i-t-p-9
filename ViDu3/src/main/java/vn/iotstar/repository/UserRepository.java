package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.iotstar.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<User> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.user.id = :userId")
    long countProductsByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT u.id AS id, COUNT(p.id) AS productCount
        FROM User u LEFT JOIN u.products p
        GROUP BY u.id
    """)
    List<Object[]> countProductsForUsers();
}
