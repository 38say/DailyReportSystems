package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.EmployeeRepository;
import com.techacademy.repository.ReportRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;


    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;

    }

 // レポート保存
    @Transactional
    public ErrorKinds save(Report report) {

       if(reportRepository.existsByEmployeeAndReportDate(report.getEmployee(), report.getReportDate())) {
           return ErrorKinds.DATECHECK_ERROR;
       }
        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);


        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }


    //レポート更新
    @Transactional
    public ErrorKinds update(Report report) {
        if(reportRepository.existsByEmployeeAndReportDate(report.getEmployee(), report.getReportDate())) {
            return ErrorKinds.DATECHECK_ERROR;
        }
        Report reportInDB = findById(report.getId());

       report.setDeleteFlg(reportInDB.isDeleteFlg());

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(reportInDB.getCreatedAt());
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // レポート削除
    @Transactional
    public ErrorKinds delete(Integer Id, UserDetail userDetail) {


        Report report = findById(Id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // レポート一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }
    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }


}
