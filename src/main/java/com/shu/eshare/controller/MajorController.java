package com.shu.eshare.controller;

import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.model.domain.Major;
import com.shu.eshare.service.MajorService;
import com.shu.eshare.utils.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/major")
public class MajorController {

    @Resource
    private MajorService majorService;

    /**
     * 查询所有专业
     * @return list返回
     */
    @GetMapping("/list")
    public BaseResponse<List<Major>> majorList(){
        List<Major> list = majorService.list();
        return ResultUtils.success(list);
    }
}
