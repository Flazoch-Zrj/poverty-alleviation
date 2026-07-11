package com.poverty.modules.news.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.news.entity.NewsArticle;
import com.poverty.modules.news.mapper.NewsArticleMapper;
import com.poverty.modules.news.service.NewsArticleService;
import org.springframework.stereotype.Service;

@Service
public class NewsArticleServiceImpl extends ServiceImpl<NewsArticleMapper, NewsArticle> implements NewsArticleService {
}
