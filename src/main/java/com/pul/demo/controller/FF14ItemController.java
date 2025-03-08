package com.pul.demo.controller;

import cn.hutool.core.text.CharSequenceUtil;
import com.pul.demo.annotations.FF14ResponseBody;
import com.pul.demo.dto.ItemDTO;
import com.pul.demo.service.FF14ItemService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ff14/item")
@FF14ResponseBody
public class FF14ItemController {

    @Resource
    private FF14ItemService ff14ItemService;

    @GetMapping("/search")
    public List<ItemDTO> searchItems(@RequestParam String name) {
        if (CharSequenceUtil.isBlank(name)){
            return new ArrayList<>();
        }
        return ff14ItemService.searchItems(name);
    }
}