package com.example.scanservice;

import com.example.scanservice.model.Scan;
import com.example.scanservice.repository.ScanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ScanControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScanRepository scanRepository;

    private Scan scanUser1Car1 = new Scan("Lode", "Traktor", 1);
    private Scan scanUser1Car2 = new Scan("Lode", "Tesla", 2);
    private Scan scanUser2Car1 = new Scan("Johnny", "Traktor", 3);

    private Scan scanToBeDeleted = new Scan("Michiel", "Tesla", 4);

    @BeforeEach
    public void beforeAllTests() {
        scanRepository.deleteAll();
        scanRepository.save(scanUser1Car1);
        scanRepository.save(scanUser1Car2);
        scanRepository.save(scanUser2Car1);
        scanRepository.save(scanToBeDeleted);
    }

    @AfterEach
    public void afterAllTests() {
        //Watch out with deleteAll() methods when you have other data in the test database!
        scanRepository.deleteAll();
    }

    private ObjectMapper mapper = new ObjectMapper();


    @Test
    void givenScan_whenGetScansByUserName_thenReturnJsonScans() throws Exception {
        mockMvc.perform(get("/scans/user/{userName}","lode"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userName",is("lode")))
                .andExpect(jsonPath("$[0].carBrand",is("traktor")))
                .andExpect(jsonPath("$[0].scoreNumber",is(1)))
                .andExpect(jsonPath("$[1].userName",is("lode")))
                .andExpect(jsonPath("$[1].carBrand",is("tesla")))
                .andExpect(jsonPath("$[1].scoreNumber",is(2)));
    }

    @Test
    void givenScan_whenGetScansByCarBrand_thenReturnJsonScans() throws Exception {
        mockMvc.perform(get("/scans/{carBrand}","tesla"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userName",is("lode")))
                .andExpect(jsonPath("$[0].carBrand",is("tesla")))
                .andExpect(jsonPath("$[0].scoreNumber",is(2)))
                .andExpect(jsonPath("$[1].userName",is("michiel")))
                .andExpect(jsonPath("$[1].carBrand",is("tesla")))
                .andExpect(jsonPath("$[1].scoreNumber",is(4)));
    }

    @Test
    void givenScan_whenGetScanByUserNameAndCarBrand_thenReturnJsonScan() throws Exception {
        mockMvc.perform(get("/scans/user/{userName}/car/{carBrand}","lode","tesla"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("lode")))
                .andExpect(jsonPath("$.carBrand",is("tesla")))
                .andExpect(jsonPath("$.scoreNumber",is(2)));
    }

    @Test
    void givenScan_whenGetScans_thenReturnJsonScans() throws Exception {
        mockMvc.perform(get("/scans"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].userName",is("lode")))
                .andExpect(jsonPath("$[0].carBrand",is("traktor")))
                .andExpect(jsonPath("$[0].scoreNumber",is(1)))
                .andExpect(jsonPath("$[1].userName",is("lode")))
                .andExpect(jsonPath("$[1].carBrand",is("tesla")))
                .andExpect(jsonPath("$[1].scoreNumber",is(2)))
                .andExpect(jsonPath("$[2].userName",is("johnny")))
                .andExpect(jsonPath("$[2].carBrand",is("traktor")))
                .andExpect(jsonPath("$[2].scoreNumber",is(3)))
                .andExpect(jsonPath("$[3].userName",is("michiel")))
                .andExpect(jsonPath("$[3].carBrand",is("tesla")))
                .andExpect(jsonPath("$[3].scoreNumber",is(4)));
    }


    @Test
    void whenPostScan_thenReturnJsonScan() throws Exception{
        mockMvc.perform(post("/scans")
                        .content(mapper.writeValueAsString(scanUser1Car1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("lode")))
                .andExpect(jsonPath("$.carBrand",is("traktor")))
                .andExpect(jsonPath("$.scoreNumber",is(1)));
    }

    @Test
    void givenScan_whenPutScan_thenReturnJsonScan() throws Exception{
        Scan updatedScan = new Scan("Lode","Traktor",2);

        mockMvc.perform(put("/scans")
                        .content(mapper.writeValueAsString(updatedScan))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("lode")))
                .andExpect(jsonPath("$.carBrand",is("traktor")))
                .andExpect(jsonPath("$.scoreNumber",is(2)));
    }

    @Test
    void givenScan_whenDeleteScan_thenStatusOk() throws Exception{
        mockMvc.perform(delete("/scans/user/{userName}/car/{carBrand}","michiel","tesla")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void givenNoScan_whenDeleteScan_thenStatusNotFound() throws Exception{
        mockMvc.perform(delete("/scans/user/{userName}/car/{carBrand}","michiel","volvo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // Extra test because of the DTo scan
    @Test
    void givenScan_whenUpdateUserNameScan_thenReturnJsonScan() throws Exception{
        Scan updatedScan = new Scan("Lode", "Traktor", 1);

        updatedScan.setUserName("Johnny");

        mockMvc.perform(put("/scans")
                        .content(mapper.writeValueAsString(updatedScan))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("johnny")))
                .andExpect(jsonPath("$.carBrand",is("traktor")))
                .andExpect(jsonPath("$.scoreNumber",is(1)));
    }


}
