package com.example.scanservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.example.scanservice.model.Scan;

@Repository
public interface ScanRepository extends MongoRepository<Scan, String> {
    List<Scan> findScansByUserName(String userName);
    List<Scan> findScansByCarBrand(String carBrand);
//    List<Scan> findAll();
    Scan findScanByUserNameAndAndCarBrand(String userName, String carBrand);
}
