package hoang.edu.spring_java_2.repository;

import hoang.edu.spring_java_2.entity.OderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OderDetailRepo extends JpaRepository<OderDetail, Integer> {
}
