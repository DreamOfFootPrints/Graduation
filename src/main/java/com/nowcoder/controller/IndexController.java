package com.nowcoder.controller;

import com.nowcoder.aspect.LogAspect;
import com.nowcoder.model.User;
import com.nowcoder.service.WendaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by nowcoder on 2016/7/10.
 */


//http://blog.csdn.net/archer119/article/details/51814788
    //这些注解的区别是什么？
//注解就是表明这是一个Controller，且会被spring容器进行初始化//表明这是一个控制层
    ////http://blog.csdn.net/cnmm22/article/details/47020717
//service  层可以看做是另一个 DAO 层，只是在里面封装了另一些逻辑。而 Controller 和 service 区别就大了，Controller 要处理请求映射， service 不会
@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
//https://zhidao.baidu.com/question/617952452151078572.html
    //每一个类用一个日志实例维护

//http://bbs.csdn.net/topics/390282583
    @Autowired
    WendaService wendaService;
    //使用IOC的方式


    //ResponseBody是返回的不是一个模版，而直接就是一个字符串
    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET})//区分唯一的映射
    @ResponseBody
    public String index(HttpSession httpSession) {
        logger.info("VISIT HOME");//打日志

        return wendaService.getMessage(2) + "Hello NowCoder" + httpSession.getAttribute("   msg   ");//最后一个函数默认是取到session的值
  //HttpSession类它提供了setAttribute()和getAttribute()方法存储和检索对象。  //http://www.111cn.net/jsp/Application/38662.htm+
    }

    @RequestMapping(path = {"/profile/{groupId}/{userId}"})//获取路径中的参数
    @ResponseBody
    public String profile(@PathVariable("userId") int userId,  //简单的使用路径中的参数
                          @PathVariable("groupId") String groupId,
                          @RequestParam(value = "type", defaultValue = "1") int type,//这里是拥有默认参数的//
                          @RequestParam(value = "key", required = false) String key) {//required是指明参数是否必须，默认为true
        return String.format("Profile Page of %s / %d, t:%d k: %s", groupId, userId, type, key);//按照格式化进行输出
        //http://blog.sina.com.cn/s/blog_b89b57410102w4wa.html
    }

    @RequestMapping(path = {"/vm"}, method = {RequestMethod.GET})
    public String template(Model model) {//Model类型的变量，是用来给模版html中进行参数传递的
        model.addAttribute("value1", "vvvvv1");
        List<String> colors = Arrays.asList(new String[]{"RED", "GREEN", "BLUE"});
        model.addAttribute("colors", colors);

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 4; ++i) {
            map.put(String.valueOf(i), String.valueOf(i * i));//数字转化为字符串
        }
        model.addAttribute("map", map);
        model.addAttribute("user", new User("LEE"));
        return "home";
    }

    @RequestMapping(path = {"/request"}, method = {RequestMethod.GET})
    @ResponseBody
    public String request(Model model, HttpServletResponse response,//响应   //http://blog.sina.com.cn/s/blog_b03b091f0102vzbq.html
                           HttpServletRequest request,
                           HttpSession httpSession,
                          @CookieValue("JSESSIONID") String sessionId) { //请求的cookie，设置的key值为JSESSIONID
        StringBuilder sb = new StringBuilder();  // 字符变量（非线程安全的）   //http://blog.csdn.net/rmn190/article/details/1492013
        sb.append("COOKIEVALUE:" + sessionId);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                sb.append("Cookie:" + cookie.getName() + " value:" + cookie.getValue());
            }
        }
        sb.append(request.getMethod() + "<br>");
        sb.append(request.getQueryString() + "<br>");
        sb.append(request.getPathInfo() + "<br>");
        sb.append(request.getRequestURI() + "<br>");

        response.addHeader("nowcoderId", "hello");//响应中添加这两个值
        response.addCookie(new Cookie("username", "nowcoder"));//cookie中添加这两个值

        return sb.toString();
    }

    @RequestMapping(path = {"/redirect/{code}"}, method = {RequestMethod.GET})
    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession httpSession) {
        httpSession.setAttribute("msg", "jump from redirect");
        RedirectView red = new RedirectView("/", true);
        if (code == 301) {
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return  red;
    }

    @RequestMapping(path = {"/admin"}, method = {RequestMethod.GET})
    @ResponseBody
    public String admin(@RequestParam("key") String key) {
        if ("admin".equals(key)) {
            return "hello admin";
        }
        throw  new IllegalArgumentException("参数不对");
    }

    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e) {
        return "error:" + e.getMessage();
    }
}
