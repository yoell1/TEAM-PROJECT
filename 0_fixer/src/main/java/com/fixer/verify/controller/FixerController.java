package com.fixer.verify.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fixer.common.TempLogin;
import com.fixer.verify.model.dto.FixerVerifyRequest;
import com.fixer.verify.service.FixerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/fixer")
@RequiredArgsConstructor
public class FixerController {

	private final FixerService service;

	/** 기사 인증 신청 화면 */
	@GetMapping("/verify")
	public String verifyForm(Model model) {

		model.addAttribute("categoryList", service.getCategoryList());
		model.addAttribute("profile", service.getMyProfile(TempLogin.USER_ID));

		return "fixer/verify";
	}

	/** 기사 인증 신청 처리 */
	@PostMapping("/verify")
	public String verify(@ModelAttribute FixerVerifyRequest request,
	                     RedirectAttributes ra) {

		try {
			service.applyVerify(TempLogin.USER_ID, request);
			ra.addFlashAttribute("message", "인증 신청이 완료되었습니다. 심사까지 1~2일 걸립니다.");

		} catch (IllegalStateException e) {
			// 중복 신청 / 필수값 누락 등 — 사용자에게 그대로 보여줄 수 있는 메시지
			ra.addFlashAttribute("message", e.getMessage());

		} catch (IOException e) {
			e.printStackTrace();
			ra.addFlashAttribute("message", "파일 저장 중 오류가 발생했습니다. 다시 시도해주세요.");
		}

		return "redirect:/fixer/verify";
	}
}