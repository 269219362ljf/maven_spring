package utils;




import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;


public class CommonUtil {

   /**
     * 通过PrintWriter将响应数据写入response，ajax可以接受到这个数据
     *
     * @param response
     * @param data
     */
    public static void renderData(HttpServletResponse response, Object data) {
        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
            printWriter.print(data);
        } catch (IOException ex) {
            ex.printStackTrace();
            //LogUtil.ErrorLogAdd(Constants.LOG_ERROR,"renderData ","返回数据",ex.getCause().toString(),true);
        } finally {
            if (null != printWriter) {
                printWriter.flush();
                printWriter.close();
            }
        }
    }

    //list转化为JSONArray
    public static JSONArray list2JSONResult(List<?> list) {
        JSONArray result = new JSONArray();
        if (list.size() == 0) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject=object2JSONObject(list.get(i));
            result.put(jsonObject);
        }
        return result;
    }

    //由于原有的初始化函数会导致未初始化属性掉失，所以截取JSONObject内部代码来补充
    //当未初始化属性时应如何处理
    private static JSONObject object2JSONObject(Object bean) {
        JSONObject jresult = new JSONObject();
        Class klass = bean.getClass();
        boolean includeSuperClass = klass.getClassLoader() != null;
        Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();

        for (int i = 0; i < methods.length; ++i) {
            try {
                Method ignore = methods[i];
                if (Modifier.isPublic(ignore.getModifiers())) {
                    String name = ignore.getName();
                    String key = "";
                    if (name.startsWith("get")) {
                        if (!"getClass".equals(name) && !"getDeclaringClass".equals(name)) {
                            key = name.substring(3);
                        } else {
                            key = "";
                        }
                    } else if (name.startsWith("is")) {
                        key = name.substring(2);
                    }

                    if (key.length() > 0 && Character.isUpperCase(key.charAt(0)) && ignore.getParameterTypes().length == 0) {
                        if (key.length() == 1) {
                            key = key.toLowerCase();
                        } else if (!Character.isUpperCase(key.charAt(1))) {
                            key = key.substring(0, 1).toLowerCase() + key.substring(1);
                        }

                        Object result = ignore.invoke(bean, (Object[]) null);
                        //在此增加处理代码，未初始化的属性设为""
                        if (result != null) {
                            jresult.put(key, JSONObject.wrap(result));
                        } else {
                            jresult.put(key, "");
                        }
                    }
                }
            } catch (Exception var10) {
                var10.printStackTrace();
            }
        }
        return jresult;
    }

    //获取自动注入类
    public static Object getBean(Class c){
        //经过使用context.getBeanNamesForType()获取bean名
        //再由bean名获取自动注入的bean
            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            String bean_name = (context.getBeanNamesForType(c))[0];
            return context.getBean(bean_name);
    }

    //date转String8位
    public static String date2string8(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }

   //string8位转date
    public static Date string82date(String string){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.parse(string);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //计算date的days间隔日期
    public static Date dateChangeByDays(Date date,int days){
        Calendar calendar   =   new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE,days);
        return calendar.getTime();
    }


    //获取propertiesfile的所有属性键值对
    public static HashMap<String,String> getPropertiesData(String propertiesfile){
        try {
            HashMap<String,String> result=new HashMap<String, String>();
            Properties prop = new Properties();
            InputStream in = new BufferedInputStream(new FileInputStream(propertiesfile));
            prop.load(in);
            Enumeration<Object> propertieskeys = prop.keys();
            while(propertieskeys.hasMoreElements()){
                String name=(String)propertieskeys.nextElement();
                String value=prop.getProperty(name);
                result.put(name,value);
            }
            in.close();
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //将前端获取的数据转化为json类型
    public static JSONObject requestdata2JSON(HttpServletRequest request){
        JSONObject result=new JSONObject();
        Enumeration<String> names=request.getParameterNames();
        while(names.hasMoreElements()){
            String temp=names.nextElement();
            result.put(temp,request.getParameter(temp));
        }
        return result;
    }

    //检查文件夹是否存在，不存在就创建
    public static boolean checkDir(String path){
        File dir=new File(path);
        if(!dir.exists()){
            return dir.mkdir();
        }
        if(!dir.isDirectory()){
            return false;
        }else {
            return true;
        }
    }



}
