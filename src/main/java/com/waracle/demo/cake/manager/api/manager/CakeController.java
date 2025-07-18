package com.waracle.demo.cake.manager.api.manager;

import com.waracle.demo.cake.manager.models.manager.CmCake;
import com.waracle.demo.cake.manager.repository.manager.CmCakeRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cake")
public class CakeController {

    private final CmCakeRepository cakeRepository;

    public CakeController(CmCakeRepository cakeRepository) {
        this.cakeRepository = cakeRepository;
    }

    @GetMapping
    public ResponseEntity<List<CmCake>> getAllCakes() {
        return ResponseEntity.ok(this.cakeRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CmCake> getCakeById(@PathVariable Long id) {
        return this.cakeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CmCake> createCake(@Valid @RequestBody CmCake cmCake) {
        CmCake savedCake = this.cakeRepository.save(cmCake);
        return ResponseEntity.ok(savedCake);
    }

    @PutMapping
    public ResponseEntity<CmCake> updateCake(@RequestBody CmCake cmCake) {
        if (!this.cakeRepository.existsById(cmCake.getId())) {
            return ResponseEntity.notFound().build();
        }
        CmCake updatedCake = this.cakeRepository.save(cmCake);
        return ResponseEntity.ok(updatedCake);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCake(@PathVariable Long id) {
        if (!this.cakeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        this.cakeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
