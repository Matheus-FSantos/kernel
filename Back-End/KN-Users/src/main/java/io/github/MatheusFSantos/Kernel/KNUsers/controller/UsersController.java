package io.github.MatheusFSantos.Kernel.KNUsers.controller;

import io.github.MatheusFSantos.Kernel.KNUsers.model.entity.Users;
import io.github.MatheusFSantos.Kernel.KNUsers.model.exception.UsersException;
import io.github.MatheusFSantos.Kernel.KNUsers.model.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @GetMapping
    public ResponseEntity<List<Users>> findAll() throws UsersException {
        return ResponseEntity.ok().body(this.usersService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) throws UsersException {
        return ResponseEntity.ok().body(this.usersService.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> save() {
        //complete save method
        return ResponseEntity.ok().body("save");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id) {
        //complete update method
        return ResponseEntity.ok().body("update");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) throws UsersException {
        this.usersService.delete(id);
        return ResponseEntity.noContent().build();
    }

}