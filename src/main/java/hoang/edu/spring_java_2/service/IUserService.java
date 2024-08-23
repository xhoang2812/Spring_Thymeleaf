package hoang.edu.spring_java_2.service;

import hoang.edu.spring_java_2.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.List;


public interface IUserService {
    List<User> findAll();
    User findById(int id);
    User save(User user);
    User update(User user);
    void delete(int id);

}
