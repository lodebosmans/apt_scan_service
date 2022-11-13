package com.example.scanservice;

import com.example.scanservice.model.Scan;
import com.example.scanservice.repository.ScanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class ScanControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScanRepository scanRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void givenScan_whenGetScansByUserName_thenReturnJsonScans() throws Exception {
        Scan scanUser1Car1 = new Scan("Lode","Audi",5);
        Scan scanUser1Car2 = new Scan("Lode","Tesla",3);

        List<Scan> scanList = new ArrayList<>();
        scanList.add(scanUser1Car1);
        scanList.add(scanUser1Car2);

        given(scanRepository.findScansByUserName("LODE")).willReturn(scanList);

        mockMvc.perform(get("/scans/user/{userName}","Lode"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userName",is("Lode")))
                .andExpect(jsonPath("$[0].carBrand",is("Audi")))
                .andExpect(jsonPath("$[0].scoreNumber",is(5)))
                .andExpect(jsonPath("$[1].userName",is("Lode")))
                .andExpect(jsonPath("$[1].carBrand",is("Tesla")))
                .andExpect(jsonPath("$[1].scoreNumber",is(3)));
    }

    @Test
    void givenScan_whenGetScansByCarBrand_thenReturnJsonScans() throws Exception {
        Scan scanUser1Car1 = new Scan("Lode","Tesla",5);
        Scan scanUser1Car2 = new Scan("Johnny","Tesla",3);

        List<Scan> scanList = new ArrayList<>();
        scanList.add(scanUser1Car1);
        scanList.add(scanUser1Car2);

        given(scanRepository.findScansByCarBrand("Tesla")).willReturn(scanList);

        mockMvc.perform(get("/scans/{carBrand}","Tesla"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userName",is("Lode")))
                .andExpect(jsonPath("$[0].carBrand",is("Tesla")))
                .andExpect(jsonPath("$[0].scoreNumber",is(5)))
                .andExpect(jsonPath("$[1].userName",is("Johnny")))
                .andExpect(jsonPath("$[1].carBrand",is("Tesla")))
                .andExpect(jsonPath("$[1].scoreNumber",is(3)));
    }

    @Test
    void givenScan_whenGetScanByUserNameAndCarBrand_thenReturnJsonScan() throws Exception {
        Scan scanUser1Car1 = new Scan("Lode","Tesla",3);

        given(scanRepository.findScanByUserNameAndAndCarBrand("LODE","Tesla")).willReturn(scanUser1Car1);

        mockMvc.perform(get("/scans/user/{userName}/car/{carBrand}","Lode","Tesla"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("Lode")))
                .andExpect(jsonPath("$.carBrand",is("Tesla")))
                .andExpect(jsonPath("$.scoreNumber",is(3)));
    }

    @Test
    void givenScan_whenGetScans_thenReturnJsonScans() throws Exception {
        Scan scanUser1Car1 = new Scan("Lode","Tesla",5);
        Scan scanUser1Car2 = new Scan("Johnny","Tesla",3);

        List<Scan> scanList = new ArrayList<>();
        scanList.add(scanUser1Car1);
        scanList.add(scanUser1Car2);

        given(scanRepository.findAll()).willReturn(scanList);

        mockMvc.perform(get("/scans"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userName",is("Lode")))
                .andExpect(jsonPath("$[0].carBrand",is("Tesla")))
                .andExpect(jsonPath("$[0].scoreNumber",is(5)))
                .andExpect(jsonPath("$[1].userName",is("Johnny")))
                .andExpect(jsonPath("$[1].carBrand",is("Tesla")))
                .andExpect(jsonPath("$[1].scoreNumber",is(3)));
    }



    @Test
    void whenPostScan_thenReturnJsonScan() throws Exception{
        Scan ScanUser3Car1 = new Scan("MICHAEL","Audi",4);

        mockMvc.perform(post("/scans")
                        .content(mapper.writeValueAsString(ScanUser3Car1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("Michael")))
                .andExpect(jsonPath("$.carBrand",is("Audi")))
                .andExpect(jsonPath("$.scoreNumber",is(4)));
    }

    @Test
    void givenScan_whenPutScan_thenReturnJsonScan() throws Exception{
        Scan ScanUser3Car1 = new Scan("Michael","Audi",4);

        given(scanRepository.findScanByUserNameAndAndCarBrand("MICHAEL","Audi")).willReturn(ScanUser3Car1);

        Scan updatedReview = new Scan("Michael","Audi",5);

        mockMvc.perform(put("/scans")
                        .content(mapper.writeValueAsString(updatedReview))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("Michael")))
                .andExpect(jsonPath("$.carBrand",is("Audi")))
                .andExpect(jsonPath("$.scoreNumber",is(5)));
    }

    @Test
    void givenScan_whenDeleteScan_thenStatusOk() throws Exception{
        Scan ScanUser3Car1 = new Scan("Michael","Audi",4);

        given(scanRepository.findScanByUserNameAndAndCarBrand("MICHAEL","Audi")).willReturn(ScanUser3Car1);

        mockMvc.perform(delete("/scans/user/{userName}/car/{carBrand}","Michael","Audi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void givenNoScan_whenDeleteScan_thenStatusNotFound() throws Exception{
        given(scanRepository.findScanByUserNameAndAndCarBrand("LODE","Volvo")).willReturn(null);

        mockMvc.perform(delete("/scans/user/{userName}/car/{carBrand}","Lode","Volvo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // Extra test because of the DTO scan
    @Test
    void givenNewScanIsPut_thenReturnJsonScan() throws Exception{
        Scan newScan = new Scan();
        newScan.setUserName("Michael");
        newScan.setCarBrand("Audi");
        newScan.setScoreNumber(4);

        given(scanRepository.findScanByUserNameAndAndCarBrand("MICHAEL","Audi")).willReturn(newScan);

        Scan updatedReview = new Scan("Michael","Audi",5);

        mockMvc.perform(put("/scans")
                        .content(mapper.writeValueAsString(updatedReview))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("Michael")))
                .andExpect(jsonPath("$.carBrand",is("Audi")))
                .andExpect(jsonPath("$.scoreNumber",is(5)));
    }
}
