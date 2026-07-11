package com.poverty.modules.news.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.poverty.common.result.R;
import com.poverty.modules.news.entity.DataDict;
import com.poverty.modules.news.service.DataDictService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/api/v1/news/dict")
public class DictController {

    @Autowired
    private DataDictService dataDictService;

    @GetMapping("/type")
    public R<List<DataDict>> type(@RequestParam String dictType) {
        LambdaQueryWrapper<DataDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDict::getDictType, dictType);
        wrapper.orderByAsc(DataDict::getSortOrder);
        List<DataDict> list = dataDictService.list(wrapper);
        return R.ok(list);
    }

    @PostMapping("/")
    public R<Void> save(@RequestBody DataDict dict) {
        dataDictService.save(dict);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody DataDict dict) {
        dict.setDictId(id);
        dataDictService.updateById(dict);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        dataDictService.removeById(id);
        return R.ok();
    }
}
