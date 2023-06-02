package study.hitchhiking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import study.hitchhiking.pojo.User;
import study.hitchhiking.service.UserService;
import study.hitchhiking.utils.JWTUtil;
import study.hitchhiking.utils.response.ResponseData;

import java.util.Date;

@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private UserService userService;


    @ResponseBody
    @RequestMapping("/login")
    public ResponseData userLogin(@RequestParam(value = "phonenumberL", required = true) String phonenumber,
                                  @RequestParam(value = "passwordL", required = true) String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phonenumber", phonenumber);
        User user = userService.getOne(wrapper);
        System.out.println(user.getPassword());
        String userToken;
        ResponseData responseData = new ResponseData();
        if (null != user && null != user.getPassword() && user.getPassword().equals(password)) {
            userToken = JWTUtil.signWithUID(user.getUserID().toString());
            responseData.put("token", userToken);
            System.out.println(phonenumber + " login");
        }else{
            return ResponseData.failed(-2,"账号或密码错误！");
        }
        return responseData;
    }

    @ResponseBody
    @RequestMapping("/signup")
    public ResponseData userSignup(@RequestParam(value = "phonenumberS", required = true) String phonenumber,
                                  @RequestParam(value = "passwordS", required = true) String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phonenumber", phonenumber);
        if(userService.count(wrapper) == 0){
            User user = new User();
            user.setPhonenumber(phonenumber);
            user.setPassword(password);
            user.setCreatetime(new Date());
            user.setName(phonenumber);

            userService.save(user);
            String userToken = JWTUtil.signWithUID(userService.getOne(wrapper).getUserID().toString());
            ResponseData responseData = new ResponseData();
            responseData.put("token", userToken);
            return responseData;
        }else{
            return ResponseData.failed(-3,"该手机号已被注册！");
        }
    }

}
