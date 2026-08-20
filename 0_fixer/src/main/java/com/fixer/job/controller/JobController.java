package com.fixer.job.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fixer.common.TempLogin;
import com.fixer.job.service.JobService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/fixer/jobs")
@RequiredArgsConstructor
public class JobController {

	private final JobService service;

	@GetMapping
	public String list(Model model) {

		try {
			model.addAttribute("jobList", service.getMyJobs(TempLogin.FIXER_ID));
		} catch (IllegalStateException e) {
			model.addAttribute("message", e.getMessage());
		}

		return "fixer/jobs";
	}

	@GetMapping("/{repairNo}")
	public String detail(@PathVariable("repairNo") Long repairNo,
	                     Model model,
	                     RedirectAttributes ra) {

		try {
			model.addAttribute("job", service.getMyJob(repairNo, TempLogin.FIXER_ID));
			return "fixer/jobDetail";

		} catch (IllegalStateException e) {
			ra.addFlashAttribute("message", e.getMessage());
			return "redirect:/fixer/jobs";
		}
	}

	@PostMapping("/{repairNo}/start")
	public String start(@PathVariable("repairNo") Long repairNo, RedirectAttributes ra) {
		return move(repairNo, "IN_PROGRESS", "작업을 시작했습니다.", ra);
	}

	@PostMapping("/{repairNo}/complete")
	public String complete(@PathVariable("repairNo") Long repairNo, RedirectAttributes ra) {
		return move(repairNo, "COMPLETED", "작업을 완료했습니다.", ra);
	}

	@PostMapping("/{repairNo}/cancel")
	public String cancel(@PathVariable("repairNo") Long repairNo,
	                     @RequestParam("reason") String reason,
	                     RedirectAttributes ra) {

		try {
			service.cancel(TempLogin.FIXER_ID, repairNo, reason);
			ra.addFlashAttribute("message", "작업을 취소했습니다.");
		} catch (IllegalStateException e) {
			ra.addFlashAttribute("message", e.getMessage());
		}

		return "redirect:/fixer/jobs/" + repairNo;
	}

	/** start / complete 가 공통으로 쓰는 부분 */
	private String move(Long repairNo, String toStatus, String okMessage, RedirectAttributes ra) {

		try {
			service.moveStatus(TempLogin.FIXER_ID, repairNo, toStatus);
			ra.addFlashAttribute("message", okMessage);
		} catch (IllegalStateException e) {
			ra.addFlashAttribute("message", e.getMessage());
		}

		return "redirect:/fixer/jobs/" + repairNo;
	}
}