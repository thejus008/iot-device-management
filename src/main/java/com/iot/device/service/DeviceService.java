package com.iot.device.service;

import com.iot.device.model.Device;
import com.iot.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final KafkaProducerService kafkaProducerService;

    public Device registerDevice(Device device) {
        log.info("Registering new device: {}", device);
        Device savedDevice= deviceRepository.save(device);
        kafkaProducerService.sendMessage("thingwire.devices.events", savedDevice.toString());
        log.info("Device registered with ID: {}", savedDevice.getId());
        return savedDevice;
    }
    public void sendCommandToDevice(UUID id, String command) {
        kafkaProducerService.sendMessage("thingwire.devices.commands", Map.of("deviceId", id.toString(), "command", command).toString());
    }
    public Optional<Device> getDevice(UUID id) {
        log.info("Getting device with ID: {}", id);
        return deviceRepository.findById(id);
    }
    public List<Device> getAllDevices() {
        log.info("Getting all devices");
        return deviceRepository.findAll();
    }
    public Device updateDevices(UUID id,Device devices) {
        log.info("Updating device for ID: {}", id);
        return deviceRepository.findById(id).map(device -> {
            device.setName(devices.getName());
            device.setMetadata(devices.getMetadata());
            return deviceRepository.save(device);
        }).orElseThrow(() -> new RuntimeException("Device not found"));
    }
    public void deleteDevice(UUID id) {
        log.warn("Deleting device with ID: {}", id);
        deviceRepository.deleteById(id);
        log.info("Device deleted with ID: {}", id);
    }
}
