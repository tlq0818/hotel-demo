package cn.itcast.hotel;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson.JSON;

import static cn.itcast.hotel.constants.HotelConstants.HOTEL_MAPPING;

/**
 * @Description
 * @Author tanlinqing
 * @Date 2023/4/17 16:46
 */

public class HotelIndexTest {
    private RestHighLevelClient client;

    @BeforeEach
    void setUp() {
        this.client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://81.70.180.150:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException{
        this.client.close();
    }
    @Test
    public void test() {
        System.out.println("restHighLevelClient初始化成功" + client);
    }
    /*增加索引*/
    @Test
    void  createHotelIndex() throws IOException{
        //创建对象
        //准请求参数dsl
        //发送请求
        CreateIndexRequest request = new CreateIndexRequest("hotel");
        request.source(HOTEL_MAPPING, XContentType.JSON);
        client.indices().create(request, RequestOptions.DEFAULT);
    }
    /*删除索引*/
    @Test
    void testDeleteHotelIndex() throws IOException {
        // 1.创建Request对象
        DeleteIndexRequest request = new DeleteIndexRequest("hotel");
        // 2.发送请求
        client.indices().delete(request, RequestOptions.DEFAULT);
    }
    @Test
    void testExistsHotelIndex() throws IOException {
        // 1.创建Request对象
        GetIndexRequest request = new GetIndexRequest("hotel");
        // 2.发送请求
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        // 3.输出
        System.err.println(exists ? "索引库已经存在！" : "索引库不存在！");
    }

}
