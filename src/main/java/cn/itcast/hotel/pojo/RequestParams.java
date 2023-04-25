package cn.itcast.hotel.pojo;

import lombok.Data;

/**
 * @Description
 * @Author tanlinqing
 * @Date 2023/4/25 15:36
 */
@Data
public class RequestParams {
    private  String key;
    private  Integer page;
    private  Integer size;
    private  String sortBy;


}
