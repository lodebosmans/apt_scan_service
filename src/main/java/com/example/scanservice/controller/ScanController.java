package com.example.scanservice.controller;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import com.example.scanservice.model.Scan;
import com.example.scanservice.repository.ScanRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

class ScanDto {
    private String userName;
    private String carBrand;
    private Integer scoreNumber;

    public String getUserName() {
        return userName;
    }
    public String getCarBrand() {
        return carBrand;
    }
    public Integer getScoreNumber() {
        return scoreNumber;
    }
}

@RestController
public class ScanController {

    @Autowired
    private ScanRepository scanRepository;

    @PostConstruct
    public void fillDB(){
        if(scanRepository.count()==0){
            scanRepository.save(new Scan("Lode", "Audi A4",5));
            scanRepository.save(new Scan("Lode", "Traktor",2));
            scanRepository.save(new Scan("Johnny", "Lamborghini",5));
            scanRepository.save(new Scan("Lode", "Volkswagen Golf",3));
        }
    }

    @GetMapping("/scans/user/{userName}")
    public List<Scan> getScansByUserName(@PathVariable String userName){
        return scanRepository.findScansByUserName(userName.toUpperCase() );
    }

    @GetMapping("/scans/{carBrand}")
    public List<Scan> getScansByCarBrand(@PathVariable String carBrand){
        return scanRepository.findScansByCarBrand(carBrand);
    }

    @GetMapping("/scans/user/{userName}/car/{carBrand}")
    public Scan getScanByUserNameAndCarBrand(@PathVariable String userName, @PathVariable String carBrand){
        return scanRepository.findScanByUserNameAndAndCarBrand(userName.toUpperCase(), carBrand);
    }

    @GetMapping("/scans")
    public List<Scan> getScans(){
        return scanRepository.findAll();
    }

    @PostMapping("/scans")
    @ResponseBody
    public Scan addScan(@RequestBody ScanDto scanDto){

        Scan newScan = new Scan(scanDto.getUserName().toUpperCase(),scanDto.getCarBrand(),scanDto.getScoreNumber());

        scanRepository.save(newScan);
        return newScan;
    }

    @PutMapping("/scans")
    public Scan updateScan(@RequestBody ScanDto updatedScan){
        Scan retrievedScan = scanRepository.findScanByUserNameAndAndCarBrand(updatedScan.getUserName().toUpperCase(),updatedScan.getCarBrand());

        retrievedScan.setCarBrand(updatedScan.getCarBrand());
        retrievedScan.setScoreNumber(updatedScan.getScoreNumber());

        scanRepository.save(retrievedScan);

        return retrievedScan;
    }

    @DeleteMapping("/scans/user/{userName}/car/{carBrand}")
    public <T> ResponseEntity<T> deleteScan(@PathVariable String userName, @PathVariable String carBrand){
        Scan scan = scanRepository.findScanByUserNameAndAndCarBrand(userName.toUpperCase(), carBrand);
        if(scan!=null){
            scanRepository.delete(scan);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}
