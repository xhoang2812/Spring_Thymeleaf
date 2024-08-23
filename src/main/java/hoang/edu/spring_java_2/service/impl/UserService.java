package hoang.edu.spring_java_2.service.impl;

import hoang.edu.spring_java_2.entity.User;
import hoang.edu.spring_java_2.repository.UserRepo;
import hoang.edu.spring_java_2.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Service
public class UserService implements IUserService {
    private final UserRepo userRepo;

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public User findById(int id) {
        return userRepo.findById(id).orElse(null);
    }

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public User update(User user) {
      return userRepo.findById(user.getId())
                .map(user1->{
                    user1.setUserName(user.getUserName());
                    user1.setPassword(user.getPassword());
                    user1.setEmail(user.getEmail());
                    user1.setAvatar(user.getAvatar());
                    user1.setFullName(user.getFullName());
                    user1.setPhoneNumber(user.getPhoneNumber());
                    user1.setRole(user.getRole());
                    return userRepo.save(user1);
                }).orElseThrow(()-> new NullPointerException());
    }

    @Override
    public void delete(int id) {
        userRepo.deleteById(id);
    }
}
