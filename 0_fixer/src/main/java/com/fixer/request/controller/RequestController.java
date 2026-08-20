package com.fixer.request.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fixer.common.TempLogin;
import com.fixer.request.service.RequestService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/fixer/requests")
@RequiredArgsConstructor
public class RequestController {

	private final RequestService service;

	/** 내 주변 새 접수 목록 */
	@GetMapping
	public String list(Model model) {

		try {
			model.addAttribute("requestList",
					service.getNearbyRequests(TempLogin.FIXER_ID));
		} catch (IllegalStateException e) {
			model.addAttribute("message", e.getMessage());
		}

		return "fixer/requests";
	}

	/** 접수 상세 */
	@GetMapping("/{repairNo}")
	public String detail(@PathVariable("repairNo") Long repairNo,
	                     Model model,
	                     RedirectAttributes ra) {

		try {
			Map<String, Object> result =
					service.getRequestDetail(repairNo, TempLogin.FIXER_ID);

			model.addAttribute("req", result.get("request"));
			model.addAttribute("photos", result.get("photos"));

			return "fixer/requestDetail";

		} catch (IllegalStateException e) {
			ra.addFlashAttribute("message", e.getMessage());
			return "redirect:/fixer/requests";
		}
	}
}
/*
@GetMapping 에 값이 없는 것에 주목해. 
클래스의 @RequestMapping("/fixer/requests") 만으로 주소가 완성돼서 
/fixer/requests 가 돼.

@PathVariable — /fixer/requests/1 의 1 을 repairNo 에 꽂아줘.
 목록에서 ?no=1 같은 쿼리스트링을 쓸 수도 있지만,
 "자원 하나를 가리키는 주소" 는 경로에 넣는 게 요즘 관례야.
*/