package controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import utils.CommonUtil;

import java.io.File;
import java.io.FileInputStream;

@Controller
public class TestController {

    @RequestMapping(value="/test")
    public void test(HttpServletRequest request,HttpServletResponse response){
        try{
            response.sendRedirect("/jsp/Test.jsp");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping(value="/fe_test")
    public void fe_test(HttpServletRequest request,HttpServletResponse response){
        try{
            JSONObject data= new JSONObject();
            data.put("node","testconnect");
            data.put("value","ok");
            response.setContentType("application/json");
            //解决跨域问题，接受所有域的访问
            response.addHeader("Access-Control-Allow-Origin", "*");
            CommonUtil.renderData(response,data);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



}
