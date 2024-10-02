package br.com.innoact.multi_tanancy.infra.controllers;

import br.com.innoact.multi_tanancy.infra.configs.TenantContext;
import br.com.innoact.multi_tanancy.infra.persistences.models.User;
import br.com.innoact.multi_tanancy.infra.persistences.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/{brand}/user")
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/{id}")
    public User getCoupons(@PathVariable String brand, @PathVariable Long id) {
        TenantContext.getInstance().setCurrentTenant(brand);

        return userRepository.findById(id).get();
    }
}
