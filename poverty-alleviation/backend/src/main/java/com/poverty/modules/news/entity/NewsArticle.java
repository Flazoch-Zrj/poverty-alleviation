package com.poverty.modules.news.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("news_article")
public class NewsArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long articleId;

    private String title;

    private String content;

    private String type;

    private Long publisherId;

    private Integer viewCount;

    private Integer isTop;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
