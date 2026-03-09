package com.nlizzard.horizonhub.controller;

import com.nlizzard.horizonhub.controller.basecontroller.ABaseController;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController extends ABaseController {
    @GetMapping("/hello")
    public ResponseVO<String> hello() {
        String data = "Hello, HorizonHub! admin";
        return getSuccessResponseVO(data);
    }
}
