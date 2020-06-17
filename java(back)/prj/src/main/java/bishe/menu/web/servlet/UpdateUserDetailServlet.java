package bishe.menu.web.servlet;

import bishe.menu.domain.User;
import bishe.menu.service.IsLoginService;
import bishe.menu.service.UserService;
import bishe.menu.service.impl.IsLoginServiveImpl;
import bishe.menu.service.impl.UserServiceImpl;
import bishe.menu.util.JedisPoolUtils;
import bishe.menu.util.SerializeUtil;
import bishe.menu.util.UUIDUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 更新用户信息
 * post
 */
@WebServlet("/updateUserDetailServlet")
public class UpdateUserDetailServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //设置编码
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        ObjectMapper mapper = new ObjectMapper();//初始化mapper
        Map<String, Object> responseMap = new HashMap<String, Object>();//初始化用于向前端返回json数据的HashMap
        IsLoginService isLoginService = new IsLoginServiveImpl();   //实例化IsLoginService对象
        String token = request.getHeader("token");  //获取请求头中的token
        Jedis jedis = JedisPoolUtils.getJedis();    //初始化jedis
        UserService userService = new UserServiceImpl();
        //token判断是否已经登录
        if (isLoginService.islogin(token) != 1){   //未登录或登录状态已过期(即token无效时)
            responseMap.put("status",401);
            responseMap.put("message","请先登录");
            mapper.writeValue(response.getWriter(),responseMap);
            jedis.close();
            return;
        }

        User user = UserServiceImpl.getUser(token, jedis);
        //Jedis

        System.out.println("updateUserDetail:"+request.getParameter("age")+" "+request.getParameter("tel"));
        user.setAge(Integer.valueOf(request.getParameter("age")));
        user.setTel(request.getParameter("tel"));

        userService.updateUser(user);
        System.out.println(user.getAge()+" 1 "+user.getTel());
        //更新jedis中缓存的user对象
        System.out.println("jedis:"+jedis.set(token.getBytes(), SerializeUtil.serialize(user),"XX".getBytes(),"EX".getBytes(),60*60*24));
        user = UserServiceImpl.getUser(token, jedis);
        System.out.println(user.getAge()+" 2 "+user.getTel());
        responseMap.put("status",1);
        responseMap.put("message","success");
        mapper.writeValue(response.getWriter(),responseMap);
        jedis.close();
        return;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}