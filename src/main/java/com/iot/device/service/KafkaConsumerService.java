package com.iot.device.service;

import com.iot.device.model.DeviceStatus;
import com.iot.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final DeviceRepository repository;

    @KafkaListener(topics = "thingwire.devices.responses", groupId = "device-group")
    public void listenDeviceResponse(Map<String,String> message) {
        UUID deviceId = UUID.fromString(message.get("deviceId"));
        String status = message.get("status");
        repository.findById(deviceId).ifPresent(device -> {
            device.setStatus(DeviceStatus.valueOf(status));
            repository.save(device);
        });
    }
}
