
package com.carbontrack.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findAllByUser_EmailIgnoreCase(String email);

    Optional<Goal> findByIdAndUser_EmailIgnoreCase(
            Long id, String email);

    boolean existsByIdAndUser_EmailIgnoreCase(
            Long id, String email);

    boolean existsByUser_Id(Long userId);

    List<Goal> findAllByUserIsNull();
}
