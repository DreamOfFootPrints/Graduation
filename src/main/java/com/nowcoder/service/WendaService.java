package com.nowcoder.service;

import org.springframework.stereotype.Service;

/**
 * Created by nowcoder on 2016/7/10.
 */

//http://blog.csdn.net/archer119/article/details/51814788


@Service
public class WendaService {
    public String getMessage(int userId) {
        return "Hello Message:  " + String.valueOf(userId);//将数字转换为字符串
    }
}
