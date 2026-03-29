package com.MedConnect.controller;

import com.MedConnect.model.MedicalStore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/demo", produces = "application/json")
@CrossOrigin
public class DemoStoreController {

    private List<MedicalStore> stores;

    public DemoStoreController() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getResourceAsStream("/demoStores.json");
            stores = mapper.readValue(is, new TypeReference<List<MedicalStore>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET all stores
   @GetMapping("/stores")
public java.util.Map<String, Object> getStores() {
    long openCount = stores.stream().filter(s -> s.open).count();

    java.util.Map<String, Object> response = new java.util.HashMap<>();
    response.put("totalStores", stores.size());
    response.put("openStores", openCount);
    response.put("storeList", stores);

    return response;
}

    // Check medicine
    @GetMapping("/medicine")
    public List<MedicalStore> checkMedicine(@RequestParam String name) {
        String med = name.toLowerCase();
        return stores.stream()
                .filter(s -> s.open && s.medicines.stream()
                        .anyMatch(m -> m.toLowerCase().equals(med)))
                .collect(Collectors.toList());
    }
}
