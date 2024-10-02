package br.com.innoact.multi_tanancy.infra.persistences.repositories;

import br.com.innoact.multi_tanancy.infra.persistences.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
