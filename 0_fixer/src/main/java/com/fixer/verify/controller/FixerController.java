package com.fixer.verify.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fixer.verify.model.dto.FixerVerifyRequest;
import com.fixer.verify.service.FixerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/fixer")
@RequiredArgsConstructor
public class FixerController {

	private final FixerService service;

	// 기사 인증 신청 화면
	@GetMapping("/verify")
	public String verifyForm(Model model) {  // Model model — 화면으로 데이터를 실어 보내는 상자

		model.addAttribute("categoryList", service.getCategoryList());
//                             ↑                     ↑
//                          꺼낼 때 쓸 이름         실제 데이터
		//      jsp 에서 꺼내쓸 이름 :  ${categoryList} 
		return "fixer/verify";
	}
	
	
	// 기사 인증 신청 처리
	@PostMapping("/verify")
	public String verify(@ModelAttribute FixerVerifyRequest request,
						 RedirectAttributes ra) {

		String userId = "fixer01";		// TODO: 나중에 로그인 세션에서 가져오기

		try {
			service.applyVerify(userId, request);
			ra.addFlashAttribute("message", "인증 신청이 완료되었습니다.");

		} catch (IllegalStateException e) {
			// 이미 심사 중 / 이미 인증됨 / 자격증명 누락
			ra.addFlashAttribute("message", e.getMessage());

		} catch (IOException e) {
			e.printStackTrace();
			ra.addFlashAttribute("message", "파일 저장 중 오류가 발생했습니다.");
		}

		return "redirect:/fixer/verify";
	}
}