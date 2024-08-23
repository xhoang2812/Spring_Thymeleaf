package hoang.edu.spring_java_2.service;

import hoang.edu.spring_java_2.entity.Role;

import java.util.List;

public interface IRoleService {
    List<Role> findAll();
    Role findById(int id);
    Role save(Role role);
    void delete(int id);
    Role update(Role role);
}
