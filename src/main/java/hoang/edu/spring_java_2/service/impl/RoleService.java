package hoang.edu.spring_java_2.service.impl;

import hoang.edu.spring_java_2.entity.Role;
import hoang.edu.spring_java_2.repository.RoleRepo;
import hoang.edu.spring_java_2.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepo roleRepo;

    @Override
    public List<Role> findAll() {
        return roleRepo.findAll();
    }

    @Override
    public Role findById(int id) {
        return roleRepo.findById(id).get();
    }

    @Override
    public Role save(Role role) {
        return roleRepo.save(role);
    }

    @Override
    public void delete(int id) {
        roleRepo.deleteById(id);
    }

    @Override
    public Role update(Role role) {
        return roleRepo.findById(role.getId()).map(roleRepo::save).orElse(null);
    }
}
