package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {
    //log 처리
//    Logger log = LoggerFactory.getLogger(getClass()); annotation 처리 @Slf4j
    @RequestMapping("/")
    public String home(){
        log.info("home controller");
        return "home";//resources/template에 작성
    }
}
