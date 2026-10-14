
package com.carbontrack.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findAllByUser_IdOrderByActivityDateDesc(Long userId);

    Optional<Activity> findByIdAndUser_Id(Long id, Long userId);
}
