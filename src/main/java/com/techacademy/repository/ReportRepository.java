package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    boolean existsByEmployeeAndReportDate(Employee employee, LocalDate reportDate);
    List<Report> findByEmployee(Employee employee);
}