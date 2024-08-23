package hoang.edu.spring_java_2.web.controller;

import hoang.edu.spring_java_2.entity.User;
import hoang.edu.spring_java_2.service.impl.RoleService;
import hoang.edu.spring_java_2.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/get-all")
    public String getAll(Model model) {
        model.addAttribute("listUser", userService.findAll());
        return "admin/user/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("listRole", roleService.findAll());
        return "admin/user/create";
    }

    @PostMapping("/store")
    public String store(User user, Model model) {
        userService.save(user);
        return "redirect:/user/get-all";
    }

    @GetMapping("/edit/{id}")
    public String detail(@PathVariable int id, Model model) {
        User userDetail = userService.findById(id);
        model.addAttribute("listRole", roleService.findAll());
        model.addAttribute("userDetail", userDetail);
        return "admin/user/detail";
    }

    @PostMapping("/update/{id}")
    public String update( User user) {
        userService.update(user);
        return "redirect:/user/get-all";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        userService.delete(id);
        return "redirect:/user/get-all";
    }
}
