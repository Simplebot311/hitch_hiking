package study.hitchhiking.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import study.hitchhiking.VO.OrderVO;
import study.hitchhiking.VO.UserCenterVO;
import study.hitchhiking.clientUtils.UserUtils;
import study.hitchhiking.pojo.*;
import study.hitchhiking.service.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 吴建豪
 * @since 2023-03-24
 */
@Controller
@RequestMapping("/orders")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ManageService manageService;
    @Autowired
    private UserService userService;
    @Autowired
    private CarService carService;

    @RequestMapping("/all")
    public String getALl(HttpServletRequest request, Model model){
        QueryWrapper<Orders> ordersQueryWrapper = new QueryWrapper<>();
        ordersQueryWrapper.eq("orderstatus","议价中");
        model.addAttribute("orderList", orderVOList(ordersService.list(ordersQueryWrapper)));
        UserUtils.getUser(request,model,userService);
        return "orderSelect";
    }

    @RequestMapping("/create")
    public String create(HttpServletRequest request, Model model){
        User user = UserUtils.getUser(request,model,userService);
        model.addAttribute("one",new UserCenterVO(user,carService,ordersService));
        return "createOrder";
    }


    @RequestMapping("/select")
    public String getOrders(@RequestParam(name = "start", required = false) String start,
                            @RequestParam(name = "end", required = false) String end,
                            @RequestParam(name = "topPrice", required = false) int topPrice,
                            @RequestParam(name = "botPrice", required = false) int botPrice,
                            @RequestParam(name = "seat", required = false) int seat,
                            @RequestParam(name = "target", required = false) String target,
                            HttpServletRequest request, Model model) {
        UserUtils.getUser(request,model,userService);
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.between("orderprice",botPrice,topPrice);
        List<Orders> targetList = new ArrayList<>();
        wrapper.eq("orderstatus","议价中");
        if("找司机".equals(target)){
            wrapper.eq("role","司机");
            wrapper.like("threshold",start);
            wrapper.like("destination",end);
            List<Orders> orderList = ordersService.list(wrapper);
            for (Orders orders : orderList) {
                if(carService.getById(orders.getCarID()).getSeatnumber() >= seat){
                    targetList.add(orders);
                }
            }
        }else if("找乘客".equals(target)){
            wrapper.eq("role","乘客");
            wrapper.like("getonposition",start);
            wrapper.like("getoffposition",end);
            targetList = ordersService.list(wrapper);
        }
        model.addAttribute("orderList", orderVOList(targetList));
        return "orderSelect";
    }

    @RequestMapping("/insert")
    public String insertOrder(@RequestParam(name = "orderprice") String orderprice,
                              @RequestParam(name = "carID") String carID,
                              @DateTimeFormat(fallbackPatterns = {"yyyy-MM-dd'T'HH:mm"})
                              @RequestParam(name = "departtime") Date departtime,

                              @RequestParam(name = "threshold") String threshold,
                              @RequestParam(name = "destination") String destination,
                              @RequestParam(name = "role")String role,
                              HttpServletRequest request,
                              Model model) {
        User user = UserUtils.getUser(request,model,userService);
        Orders order = new Orders();
        order.setOrderstatus("议价中");
        order.setRole(role);
        order.setUserID(user.getUserID());

        try {
            order.setOrderprice(new BigDecimal(orderprice));
        } catch (Exception e) {
            order.setOrderprice(new BigDecimal(0));
        }
        if("司机".equals(role)){
            order.setCarID(carID);
            order.setDestination(destination);
            order.setThreshold(threshold);
            order.setDeparttime(departtime);
        }else{
            order.setGetoffposition(destination);
            order.setGetonposition(threshold);
            order.setGetontime(departtime);
        }
        ordersService.save(order);

        model.addAttribute("orderList", orderVOList(ordersService.list(null)));
        return "redirect:/index/user";
    }

    @RequestMapping("/delete")
    public String deleteOrder(@RequestParam(name = "orderID") String orderID,
                              HttpServletRequest request, Model model) {
        UserUtils.getUser(request,model,userService);
        ordersService.removeById(Long.valueOf(orderID));
        model.addAttribute("orderList",orderVOList(ordersService.list(null)));
        return "orderSelect";
    }

    @RequestMapping("/details")
    public String getDetails(@RequestParam(name = "orderID") String orderID,
                             HttpServletRequest request, Model model) {
        UserUtils.getUser(request,model,userService);
        model.addAttribute("one",new OrderVO(ordersService.getById(orderID),userService,carService));
        return "order";
    }

    @RequestMapping("/over")
    public String over(@RequestParam(name = "orderID") String orderID,
                             HttpServletRequest request, Model model) {
        UserUtils.getUser(request,model,userService);
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("orderID",orderID);
        Orders orders = ordersService.getById(orderID);
        orders.setOrderstatus("已完成");
        ordersService.updateById(orders);
        return "redirect:/userCenter/details";
    }

    @RequestMapping("/update")
    public String updateOrder(@RequestParam(name = "orderID") String orderID,
                              @RequestParam(name = "orderstatus") String orderstatus,
                              @RequestParam(name = "orderprice") String orderprice,
                              @RequestParam(name = "carID") String carID,
                              @RequestParam(name = "getonposition") String getonposition,
                              @DateTimeFormat(fallbackPatterns = {"yyyy-MM-dd'T'HH:mm"})
                              @RequestParam(name = "getontime",required = false) Date getontime,
                              @RequestParam(name = "getoffposition") String getoffposition,
                              @RequestParam(name = "threshold") String threshold,
                              @RequestParam(name = "destination") String destination,
                              @DateTimeFormat(fallbackPatterns = {"yyyy-MM-dd'T'HH:mm"})
                              @RequestParam(name = "departtime",required = false) Date departtime,
                              @RequestParam(name = "UserID") String UserID,
                              @RequestParam(name = "role")String role,
                              HttpServletRequest request,
                              Model model) {
        UserUtils.getUser(request,model,userService);
        Orders order = ordersService.getById(orderID);
        if(!"不修改".equals(orderstatus)){
            order.setOrderstatus(orderstatus);
        }
        try {
            order.setOrderprice(new BigDecimal(orderprice));
        } catch (Exception e) {
            order.setOrderprice(new BigDecimal(0));
        }
        order.setCarID(carID);
        order.setGetonposition(getonposition);
        if(null != getontime){
            order.setGetontime(getontime);
        }
        order.setGetoffposition(getoffposition);
        order.setDestination(destination);
        order.setThreshold(threshold);
        if(null != departtime){
            order.setDeparttime(departtime);
        }
        order.setUserID(Long.valueOf(UserID));
        if(!"不修改".equals(role)){
            order.setRole(role);
        }
        ordersService.updateById(order);

        model.addAttribute("orderList", orderVOList(ordersService.list(null)));
        //addUserVOList("userList", userService.list(null), model);
        return "orderSelect";
    }

    private List<OrderVO> orderVOList(List<Orders> list){
        if(null == list || list.size()==0)return new ArrayList<>();
        List<OrderVO> voList = new ArrayList<>();
        for (Orders orders : list) {
            voList.add(new OrderVO(orders,userService,carService));
        }
        return voList;
    }
}

