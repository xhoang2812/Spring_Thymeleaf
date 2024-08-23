package hoang.edu.spring_java_2.repository;

import hoang.edu.spring_java_2.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OdersRepo extends JpaRepository<Orders, Integer> {
}
