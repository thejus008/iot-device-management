package com.iot.device.controller;

import com.iot.device.exception.DeviceNotFoundException;
import com.iot.device.model.Device;
import com.iot.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    public ResponseEntity<Device> registerDevice(@RequestBody Device device) {
        return ResponseEntity.ok(deviceService.registerDevice(device));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Device> getDevice(@PathVariable UUID id) {
        log.info("Fetching device with id: {}", id);
        Device device = deviceService.getDevice(id).orElseThrow(() -> new DeviceNotFoundException("Device not found for ID ::" +id));
        return ResponseEntity.ok(device);
    }
    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }
    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable UUID id,
                                               @RequestBody Device device) {
        return ResponseEntity.ok(deviceService.updateDevices(id,device));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDevice(@PathVariable UUID id) {
        log.warn("Deleting device with id: {}", id);
        if (!deviceService.getDevice(id).isPresent()) {
            throw new DeviceNotFoundException("Device not found for ID ::" +id);
        }
        deviceService.deleteDevice(id);
        return ResponseEntity.ok("Device deleted successfully");
    }

}
