package cn.itcast.hotel.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;

/**
 * @Description
 * @Author tanlinqing
 * @Date 2023/4/25 15:32
 */

@RestController
@RequestMapping("/hotel")
public class hotelController {
    @Autowired
    private IHotelService hotelService;
    @PostMapping("list")
    public PageResult search(@RequestBody RequestParams params ){
        return  hotelService.search(params);
    }
}
