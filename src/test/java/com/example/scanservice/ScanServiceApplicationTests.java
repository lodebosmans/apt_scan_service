package com.example.scanservice;

import com.example.scanservice.controller.ScanController;
import com.example.scanservice.repository.ScanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ScanServiceApplicationTests {

    @Autowired
    private ScanController scanController;

    @Autowired
    private ScanRepository scanRepository;

    @Test
    void contextLoads() throws Exception {
        assertThat(scanController).isNotNull();
        assertThat(scanRepository).isNotNull();
    }

}
