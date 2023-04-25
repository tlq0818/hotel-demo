package cn.itcast.hotel.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author tanlinqing
 * @Date 2023/4/25 15:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult {
    private  long total;
    private List<HotelDoc> hotels;

}
