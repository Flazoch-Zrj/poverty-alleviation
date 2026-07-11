package com.poverty.modules.news.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.news.entity.DataDict;
import com.poverty.modules.news.mapper.DataDictMapper;
import com.poverty.modules.news.service.DataDictService;
import org.springframework.stereotype.Service;

@Service
public class DataDictServiceImpl extends ServiceImpl<DataDictMapper, DataDict> implements DataDictService {
}
