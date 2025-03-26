package com.iot.device.controller;

import com.iot.device.model.Device;
import com.iot.device.model.DeviceStatus;
import com.iot.device.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DeviceControllerTest {

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private DeviceController deviceController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
    }

    @Test
    void testRegisterDevice() throws Exception {
        Device device = new Device(UUID.randomUUID(), "Device1", DeviceStatus.ONLINE, Instant.now(), "{}");
        when(deviceService.registerDevice(any(Device.class))).thenReturn(device);

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Device1\",\"status\":\"ONLINE\",\"lastSeen\":\"2023-10-10T10:00:00Z\",\"metadata\":\"{}\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Device1"))
                .andExpect(jsonPath("$.status").value("ONLINE"));
    }

    @Test
    void testGetDevice() throws Exception {
        UUID id = UUID.randomUUID();
        Device device = new Device(id, "Device1", DeviceStatus.ONLINE, Instant.now(), "{}");
        when(deviceService.getDevice(id)).thenReturn(Optional.of(device));

        mockMvc.perform(get("/devices/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Device1"))
                .andExpect(jsonPath("$.status").value("ONLINE"));
    }

    @Test
    void testGetDeviceNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(deviceService.getDevice(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/devices/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllDevices() throws Exception {
        Device device1 = new Device(UUID.randomUUID(), "Device1", DeviceStatus.ONLINE, Instant.now(), "{}");
        Device device2 = new Device(UUID.randomUUID(), "Device2", DeviceStatus.OFFLINE, Instant.now(), "{}");
        when(deviceService.getAllDevices()).thenReturn(Arrays.asList(device1, device2));

        mockMvc.perform(get("/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Device1"))
                .andExpect(jsonPath("$[1].name").value("Device2"));
    }

    @Test
    void testUpdateDevice() throws Exception {
        UUID id = UUID.randomUUID();
        Device device = new Device(id, "Device1", DeviceStatus.ONLINE, Instant.now(), "{}");
        when(deviceService.updateDevices(any(UUID.class), any(Device.class))).thenReturn(device);

        mockMvc.perform(put("/devices/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Device1\",\"status\":\"ONLINE\",\"lastSeen\":\"2023-10-10T10:00:00Z\",\"metadata\":\"{}\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Device1"))
                .andExpect(jsonPath("$.status").value("ONLINE"));
    }

    @Test
    void testDeleteDevice() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(deviceService).deleteDevice(id);

        mockMvc.perform(delete("/devices/{id}", id))
                .andExpect(status().isNoContent());
    }
}