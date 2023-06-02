package study.hitchhiking.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import study.hitchhiking.VO.userVO;
import study.hitchhiking.pojo.Car;
import study.hitchhiking.service.CarService;
import study.hitchhiking.service.UserService;

import java.math.BigDecimal;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 吴建豪
 * @since 2023-03-24
 */
@Controller
@RequestMapping("/car")
public class CarController {
    @Autowired
    private CarService carService;

    @Autowired
    private UserService userService;


    @RequestMapping("/update")
    public String updateCar(@RequestParam(name = "carID") String carID,
                            @RequestParam(name = "seatnumber") String seatnumber,
                            @RequestParam(name = "platenumber") String platenumber,
                            @RequestParam(name = "fuelconsumption") String fuelconsumption,
                            @RequestParam(name = "oiltype") String oiltype,
                            Model model) {
        QueryWrapper<Car> wrapper = new QueryWrapper<>();
        wrapper.eq("carID", carID);

        Car car = carService.getOne(wrapper);
        car.setSeatnumber(Integer.valueOf(seatnumber));
        car.setPlatenumber(platenumber);
        car.setFuelconsumption(new BigDecimal(fuelconsumption));
        car.setOiltype(oiltype);
        carService.updateById(car);


        model.addAttribute("one", new userVO(userService.getById(car.getUserID()), carService));

        wrapper = new QueryWrapper<>();
        wrapper.eq("userID", car.getUserID());
        model.addAttribute("cars", carService.list(wrapper));
        return "user";
    }

    @RequestMapping("/details")
    public String userDetails(@RequestParam(name = "carID") String carID, Model model) {
        Car car = carService.getById(carID);
        model.addAttribute("one", car);

        return "car";
    }

    @RequestMapping("/delete")
    public String deleteCar(@RequestParam(name = "carID") String carID, Model model) {
        QueryWrapper<Car> wrapper = new QueryWrapper<>();
        wrapper.eq("carID", carID);

        Car car = carService.getOne(wrapper);
        userVO user = new userVO(userService.getById(car.getUserID()), carService);

        carService.removeById(Long.valueOf(carID));
        //System.out.println("userID="+userID);

        model.addAttribute("one", user);

        wrapper = new QueryWrapper<>();
        wrapper.eq("userID", car.getUserID());
        model.addAttribute("cars", carService.list(wrapper));

        return "user";
    }

    @RequestMapping("/insert")
    public String insertCar(@RequestParam(name = "userID") String userID,
                            @RequestParam(name = "seatnumber") String seatnumber,
                            @RequestParam(name = "platenumber") String platenumber,
                            @RequestParam(name = "fuelconsumption") String fuelconsumption,
                            @RequestParam(name = "oiltype") String oiltype,
                            Model model) {
        Car car = new Car();
        car.setUserID(Long.valueOf(userID));
        car.setSeatnumber(Integer.valueOf(seatnumber));
        car.setPlatenumber(platenumber);
        car.setFuelconsumption(new BigDecimal(fuelconsumption));
        car.setOiltype(oiltype);
        carService.save(car);

        model.addAttribute("one", new userVO(userService.getById(userID), carService));


        QueryWrapper<Car> wrapper = new QueryWrapper<>();
        wrapper.eq("userID", userID);
        model.addAttribute("cars", carService.list(wrapper));
        return "user";
    }
}

