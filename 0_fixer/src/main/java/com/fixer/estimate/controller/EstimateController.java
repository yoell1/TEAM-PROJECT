package com.fixer.estimate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fixer.common.TempLogin;
import com.fixer.estimate.model.dto.EstimateForm;
import com.fixer.estimate.model.dto.EstimateOption;
import com.fixer.estimate.service.EstimateService;
import com.fixer.request.service.RequestService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/fixer/estimates")
@RequiredArgsConstructor
public class EstimateController {

	private final EstimateService service;
	private final RequestService  requestService;   // 폼에 접수 정보를 같이 띄우려고

	/** 내 견적 목록 */
	@GetMapping
	public String list(Model model) {

		try {
			model.addAttribute("estimateList", service.getMyEstimates(TempLogin.FIXER_ID));
		} catch (IllegalStateException e) {
			model.addAttribute("message", e.getMessage());
		}

		return "fixer/estimates";
	}

	/** 견적 작성/수정 화면 */
	@GetMapping("/new/{repairNo}")
	public String form(@PathVariable("repairNo") Long repairNo,
	                   Model model,
	                   RedirectAttributes ra) {

		try {
			model.addAttribute("req",
					requestService.getRequestDetail(repairNo, TempLogin.FIXER_ID).get("request"));
			model.addAttribute("estimate",
					service.getMyEstimate(repairNo, TempLogin.FIXER_ID));
			model.addAttribute("optionList", EstimateOption.values());

			return "fixer/estimateForm";

		} catch (IllegalStateException e) {
			ra.addFlashAttribute("message", e.getMessage());
			return "redirect:/fixer/requests";
		}
	}

	/** 견적 제시 처리 */
	@PostMapping
	public String submit(@ModelAttribute EstimateForm form,
	                     RedirectAttributes ra) {

		try {
			service.submit(TempLogin.FIXER_ID, form);
			ra.addFlashAttribute("message", "견적을 제시했습니다.");
			return "redirect:/fixer/estimates";

		} catch (IllegalStateException e) {
			ra.addFlashAttribute("message", e.getMessage());
			return "redirect:/fixer/estimates/new/" + form.getRepairNo();
		}
	}

	/** 견적 철회 */
	@PostMapping("/{estimatesId}/withdraw")
	public String withdraw(@PathVariable("estimatesId") Long estimatesId,
	                       RedirectAttributes ra) {

		try {
			service.withdraw(TempLogin.FIXER_ID, estimatesId);
			ra.addFlashAttribute("message", "견적을 철회했습니다.");
		} catch (IllegalStateException e) {
			ra.addFlashAttribute("message", e.getMessage());
		}

		return "redirect:/fixer/estimates";
	}
}