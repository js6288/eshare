package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.model.domain.Major;
import com.shu.eshare.service.MajorService;
import com.shu.eshare.mapper.MajorMapper;
import org.springframework.stereotype.Service;

/**
* @author ljs
* @description 针对表【major(专业库)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:25
*/
@Service
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major>
    implements MajorService{

}




