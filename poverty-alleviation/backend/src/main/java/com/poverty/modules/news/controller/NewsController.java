package com.poverty.modules.news.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.PageResult;
import com.poverty.common.result.R;
import com.poverty.modules.news.entity.NewsArticle;
import com.poverty.modules.news.service.NewsArticleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api(tags = "信息公开接口")
@RestController
@RequestMapping("/api/v1/news/article")
public class NewsController {

    @Autowired
    private NewsArticleService newsArticleService;

    @GetMapping("/page")
    public R<PageResult<NewsArticle>> page(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) String type,
                                     @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<NewsArticle> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(NewsArticle::getType, type);
        }
        if (status != null) {
            wrapper.eq(NewsArticle::getStatus, status);
        }
        wrapper.orderByDesc(NewsArticle::getCreateTime);
        Page<NewsArticle> pageResult = newsArticleService.page(new Page<>(page, size), wrapper);
        return R.ok(PageResult.of(pageResult));
    }

    @GetMapping("/{id}")
    public R<NewsArticle> get(@PathVariable Long id) {
        NewsArticle article = newsArticleService.getById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            newsArticleService.updateById(article);
        }
        return R.ok(article);
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('admin') or hasRole('volunteer')")
    public R<Void> save(@RequestBody NewsArticle article) {
        newsArticleService.save(article);
        return R.ok();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('volunteer')")
    public R<Void> update(@PathVariable Long id, @RequestBody NewsArticle article) {
        article.setArticleId(id);
        newsArticleService.updateById(article);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('volunteer')")
    public R<Void> delete(@PathVariable Long id) {
        newsArticleService.removeById(id);
        return R.ok();
    }
}
