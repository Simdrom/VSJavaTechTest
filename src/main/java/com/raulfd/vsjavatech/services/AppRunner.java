package com.raulfd.vsjavatech.services;

import com.raulfd.vsjavatech.domain.User;
import com.raulfd.vsjavatech.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AppRunner implements CommandLineRunner {
    private final UserRepository repository;

    public AppRunner(UserRepository repository) {
        this.repository = repository;
    }

    private void deleteAllAndInsertNewUsers(UserRepository repository) {
        repository.deleteAll();
        User[] users = {
                new User("Thomson, Elias", "555-8596", "Diamond St. 4G3 NY"),
                new User("Simond, Ella", "415-9687", "Fleet st. 45 B, 56 BR-NY"),
                new User("Clifford, Thomas", "416-69883", "Meet town, 45 - FL")
        };
        repository.saveAll(Stream.of(users).toList());
    }

    @Override
    public void run(String... args) throws Exception {
        deleteAllAndInsertNewUsers(repository);
    }
}
