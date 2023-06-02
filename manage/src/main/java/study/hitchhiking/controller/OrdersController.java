package study.hitchhiking.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import study.hitchhiking.VO.orderVO;
import study.hitchhiking.pojo.Manage;
import study.hitchhiking.pojo.Orders;
import study.hitchhiking.service.CommentService;
import study.hitchhiking.service.ManageService;
import study.hitchhiking.service.OrdersService;

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

    @RequestMapping("/select")
    public String getOrders(@RequestParam(name = "target", required = false) String target,
                            @RequestParam(name = "typeOfSelect", required = false) String typeOfSelect,
                            @RequestParam(name = "orderstatus", required = false) String[] status,
                            @RequestParam(name = "role", required = false) String[] role, Model model) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        //判断是否有查询条件
        if (null != status && status.length > 0) {
            for (int i = 0; i < status.length; i++) {
                wrapper.like("orderstatus", status[i]);
            }
        }
        if (null != role && role.length > 0) {
            for (int i = 0; i < role.length; i++) {
                wrapper.like("role", role[i]);
            }
        }
        if (null != target && null != typeOfSelect) {
            wrapper.like(typeOfSelect, target);
        }
        //没有查询条件则查找全部，有条件则按照条件查询
        List<Orders> orderList = ordersService.list(wrapper);
        model.addAttribute("orderList", orderVOList(orderList));
        return "orderSelect";
    }

    @RequestMapping("/insert")
    public String insertOrder(@RequestParam(name = "orderstatus") String orderstatus,
                              @RequestParam(name = "orderprice") String orderprice,
                              @RequestParam(name = "carID") String carID,
                              @RequestParam(name = "getonposition") String getonposition,
                              @DateTimeFormat(fallbackPatterns = {"yyyy-MM-dd'T'HH:mm"})
                              @RequestParam(name = "getondate") Date getondate,
                              @RequestParam(name = "threshold") String threshold,
                              @RequestParam(name = "UserID") String UserID,
                              @RequestParam(name = "role")String role,
                              Model model) {
        Orders order = new Orders();
        order.setOrderstatus(orderstatus);
        try {
            order.setOrderprice(new BigDecimal(orderprice));
        } catch (Exception e) {
            order.setOrderprice(new BigDecimal(0));
        }
        order.setCarID(carID);
        order.setGetonposition(getonposition);
        order.setGetontime(getondate);
        order.setThreshold(threshold);
        order.setUserID(Long.valueOf(UserID));
        order.setRole(role);
        ordersService.save(order);

        model.addAttribute("orderList", orderVOList(ordersService.list(null)));

//        addUserVOList("userList", userService.list(null), model);
        return "orderSelect";
    }

    @RequestMapping("/delete")
    public String deleteOrder(@RequestParam(name = "orderID") String orderID, Model model) {
        ordersService.removeById(Long.valueOf(orderID));
        model.addAttribute("orderList",orderVOList(ordersService.list(null)));
        return "orderSelect";
    }

    @RequestMapping("/details")
    public String getDetails(@RequestParam(name = "orderID") String orderID, Model model) {
        model.addAttribute("one",new orderVO(ordersService.getById(orderID)));
        return "order";
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
                              Model model) {
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

    private List<orderVO> orderVOList(List<Orders> list){
        if(null == list || list.size()==0)return new ArrayList<>();
        List<orderVO> voList = new ArrayList<>();
        for (Orders orders : list) {
            voList.add(new orderVO(orders));
        }
        return voList;
    }
}

