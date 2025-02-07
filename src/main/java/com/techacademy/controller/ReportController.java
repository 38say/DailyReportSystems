package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // レポート一覧画面
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetail userDetail) {
        if(userDetail.getEmployee().getRole() == Employee.Role.ADMIN) {
            model.addAttribute("listSize", reportService.findAll().size());
            model.addAttribute("reportList", reportService.findAll());  
        } else {
            model.addAttribute("listSize", reportService.findByEmployee(userDetail.getEmployee()).size());
            model.addAttribute("reportList", reportService.findByEmployee(userDetail.getEmployee()));  
            
        }

        return "reports/list";
    }

    // レポート詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {

        model.addAttribute("reports", reportService.findById(id));
        return "reports/detail";
    }

    // レポート新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail) {
        report.setEmployee(userDetail.getEmployee());
        return "reports/new";
    }

    // レポート新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Model model,
            @AuthenticationPrincipal UserDetail userDetail) {
        // 入力チェック
        if (res.hasErrors()) {
            return create(report, userDetail);
        }

        report.setEmployee(userDetail.getEmployee());
        ErrorKinds result = reportService.save(report);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(report, userDetail);
        }

        return "redirect:/reports";
    }

    // レポート更新画面
    @GetMapping(value = "/{id}/update")
    public String update(@PathVariable("id") Integer id, Report report, Model model) {
        if (id != null) {
            // 一覧画面から遷移した場合
            model.addAttribute("report", reportService.findById(id));
        } else {
            // postUser() から遷移した場合
            Report temp = reportService.findById(report.getId());
            report.setEmployee(temp.getEmployee());
            model.addAttribute("report", report);
        }

        return "reports/update";
    }

    // レポート更新処理
    @PostMapping(value = "/{id}/update")
    public String update2(@Validated Report report, BindingResult res, Model model) {
        // 入力チェック
        if (res.hasErrors()) {
            return update(null, report, model);
        }
        // 保存
        Report temp = reportService.findById(report.getId());
        report.setEmployee(temp.getEmployee());
        ErrorKinds result = reportService.update(report);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return update(null, report, model);
        }

        return "redirect:/reports";
    }

    // レポート削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = reportService.delete(id, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findById(id));
            return detail(id, model);
        }

        return "redirect:/reports";
    }

}
