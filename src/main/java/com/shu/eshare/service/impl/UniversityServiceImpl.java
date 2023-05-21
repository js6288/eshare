package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.model.domain.University;
import com.shu.eshare.service.UniversityService;
import com.shu.eshare.mapper.UniversityMapper;
import org.springframework.stereotype.Service;

/**
* @author ljs
* @description 针对表【university(院校库)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:26
*/
@Service
public class UniversityServiceImpl extends ServiceImpl<UniversityMapper, University>
    implements UniversityService{

}




