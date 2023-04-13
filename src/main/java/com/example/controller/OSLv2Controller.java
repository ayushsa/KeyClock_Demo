package com.example.controller;

import com.example.service.KeyLockDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test/v2")
public class OSLv2Controller
{
    @Autowired
    KeyLockDemoService keyLockDemoService;

    @GetMapping(value = "/createRealm/{realmName}")
    public ResponseEntity<String> createRealm(@PathVariable("realmName") String realmName)
    {
        String realmResponse = "Realm name: "+
                keyLockDemoService.createRealm(realmName) + " created successfully.";

        return ResponseEntity.ok(realmResponse);
    }

    @GetMapping(value = "/createClient/{realmName}/{clientName}")
    public ResponseEntity<String> createClient(@PathVariable("realmName") String realmName,
                                               @PathVariable("clientName") String clientName)
    {
        String clientResponse = "Client name: "+
                keyLockDemoService.createClient(realmName, clientName) + " created successfully.";

        return ResponseEntity.ok(clientResponse);
    }
}
