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
public class ScanControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScanRepository scanRepository;

    private Scan scanUser1Car1 = new Scan("Lode", "Traktor", 1);
    private Scan scanUser1Car2 = new Scan("Lode", "Tesla", 2);
    private Scan scanUser2Car1 = new Scan("Johnny", "Lamborghini", 3);
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
    public void givenScan_whenGetScansByUserName_thenReturnJsonScans() throws Exception {
        mockMvc.perform(get("/scans/user/{userName}","Lode"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userName",is("Lode")))
                .andExpect(jsonPath("$[0].carBrand",is("Traktor")))
                .andExpect(jsonPath("$[0].scoreNumber",is(1)))
                .andExpect(jsonPath("$[1].userName",is("Lode")))
                .andExpect(jsonPath("$[1].carBrand",is("Tesla")))
                .andExpect(jsonPath("$[1].scoreNumber",is(2)));
    }

    @Test
    public void givenScan_whenGetScansByCarBrand_thenReturnJsonScans() throws Exception {
        mockMvc.perform(get("/scans/{carBrand}","Tesla"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userName",is("Lode")))
                .andExpect(jsonPath("$[0].carBrand",is("Tesla")))
                .andExpect(jsonPath("$[0].scoreNumber",is(2)))
                .andExpect(jsonPath("$[1].userName",is("Michiel")))
                .andExpect(jsonPath("$[1].carBrand",is("Tesla")))
                .andExpect(jsonPath("$[1].scoreNumber",is(4)));
    }

    @Test
    public void givenScan_whenGetScanByUserNameAndCarBrand_thenReturnJsonScan() throws Exception {
        mockMvc.perform(get("/scans/user/{userName}/car/{carBrand}","Lode","Tesla"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("Lode")))
                .andExpect(jsonPath("$.carBrand",is("Tesla")))
                .andExpect(jsonPath("$.scoreNumber",is(2)));
    }

    @Test
    public void givenScan_whenGetScans_thenReturnJsonScans() throws Exception {
        mockMvc.perform(get("/scans"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].userName",is("Lode")))
                .andExpect(jsonPath("$[0].carBrand",is("Traktor")))
                .andExpect(jsonPath("$[0].scoreNumber",is(1)))
                .andExpect(jsonPath("$[1].userName",is("Lode")))
                .andExpect(jsonPath("$[1].carBrand",is("Tesla")))
                .andExpect(jsonPath("$[1].scoreNumber",is(2)))
                .andExpect(jsonPath("$[2].userName",is("Johnny")))
                .andExpect(jsonPath("$[2].carBrand",is("Lamborghini")))
                .andExpect(jsonPath("$[2].scoreNumber",is(3)))
                .andExpect(jsonPath("$[3].userName",is("Michiel")))
                .andExpect(jsonPath("$[3].carBrand",is("Tesla")))
                .andExpect(jsonPath("$[3].scoreNumber",is(4)));
    }


    @Test
    public void whenPostScan_thenReturnJsonScan() throws Exception{
        mockMvc.perform(post("/scans")
                        .content(mapper.writeValueAsString(scanUser1Car1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("Lode")))
                .andExpect(jsonPath("$.carBrand",is("Traktor")))
                .andExpect(jsonPath("$.scoreNumber",is(1)));
    }

    @Test
    public void givenScan_whenPutScan_thenReturnJsonScan() throws Exception{
        Scan updatedReview = new Scan("Lode","Traktor",2);

        mockMvc.perform(put("/scans")
                        .content(mapper.writeValueAsString(updatedReview))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("Lode")))
                .andExpect(jsonPath("$.carBrand",is("Traktor")))
                .andExpect(jsonPath("$.scoreNumber",is(2)));
    }

    @Test
    public void givenScan_whenDeleteScan_thenStatusOk() throws Exception{
        mockMvc.perform(delete("/scans/user/{userName}/car/{carBrand}","Michiel","Tesla")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenNoScan_whenDeleteScan_thenStatusNotFound() throws Exception{
        mockMvc.perform(delete("/scans/user/{userName}/car/{carBrand}","Michiel","Volvo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }



















//    @Test
//    public void givenScan_whenGetScanByUserNameAndISBN_thenReturnJsonScan() throws Exception {
//
//        mockMvc.perform(get("/scans/user/{userId}/book/{ISBN}", 001, "ISBN1"))
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.userId", is(001)))
//                .andExpect(jsonPath("$.isbn", is("ISBN1")))
//                .andExpect(jsonPath("$.scoreNumber", is(1)));
//    }
//
//    @Test
//    public void givenScan_whenGetScansByISBN_thenReturnJsonScans() throws Exception {
//
//        List<Scan> scanList = new ArrayList<>();
//        scanList.add(scanUser1Car1);
//        scanList.add(scanUser2Car1);
//
//        mockMvc.perform(get("/scans/{ISBN}", "ISBN1"))
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].userId", is(001)))
//                .andExpect(jsonPath("$[0].isbn", is("ISBN1")))
//                .andExpect(jsonPath("$[0].scoreNumber", is(1)))
//                .andExpect(jsonPath("$[1].userId", is(002)))
//                .andExpect(jsonPath("$[1].isbn", is("ISBN1")))
//                .andExpect(jsonPath("$[1].scoreNumber", is(2)));
//    }
//
//    @Test
//    public void givenScan_whenGetScansByUserName_thenReturnJsonScans() throws Exception {
//
//        List<Scan> scanList = new ArrayList<>();
//        scanList.add(scanUser1Car1);
//        scanList.add(scanUser1Car2);
//
//        mockMvc.perform(get("/scans/user/{userId}", 001))
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].userId", is(001)))
//                .andExpect(jsonPath("$[0].isbn", is("ISBN1")))
//                .andExpect(jsonPath("$[0].scoreNumber", is(1)))
//                .andExpect(jsonPath("$[1].userId", is(001)))
//                .andExpect(jsonPath("$[1].isbn", is("ISBN2")))
//                .andExpect(jsonPath("$[1].scoreNumber", is(2)));
//    }
//
//    @Test
//    public void whenPostScan_thenReturnJsonScan() throws Exception {
//        Scan scanUser3Car1 = new Scan(003, "ISBN1", 1);
//
//        mockMvc.perform(post("/scans")
//                        .content(mapper.writeValueAsString(scanUser3Car1))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.userId", is(003)))
//                .andExpect(jsonPath("$.isbn", is("ISBN1")))
//                .andExpect(jsonPath("$.scoreNumber", is(1)));
//    }
//
//    @Test
//    public void givenScan_whenPutScan_thenReturnJsonScan() throws Exception {
//
//        Scan updatedScan = new Scan(001, "ISBN1", 2);
//
//        mockMvc.perform(put("/scans")
//                        .content(mapper.writeValueAsString(updatedScan))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.userId", is(001)))
//                .andExpect(jsonPath("$.isbn", is("ISBN1")))
//                .andExpect(jsonPath("$.scoreNumber", is(2)));
//    }
//
//    @Test
//    public void givenScan_whenDeleteScan_thenStatusOk() throws Exception {
//
//        mockMvc.perform(delete("/scans/user/{userId}/book/{ISBN}", 999, "ISBN9")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void givenNoScan_whenDeleteScan_thenStatusNotFound() throws Exception {
//
//        mockMvc.perform(delete("/scans/user/{userId}/book/{ISBN}", 888, "ISBN8")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }

}
