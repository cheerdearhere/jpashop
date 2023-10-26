package jpabook.jpashop;

import jpabook.jpashop.exception.TestResponseCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HelloController {
    @GetMapping("/hello")
    public String hello(Model model){
        model.addAttribute("data","hello!!");
        model.addAttribute("code",TestResponseCode.RESPONSE01.getCode());
        model.addAttribute("message",TestResponseCode.RESPONSE01.getMessage());//enum을 사용
        return "hello";//  /templates/ + {view name} + .html
    }
}
