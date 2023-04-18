package cn.itcast.hotel;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.http.HttpHost;
import org.apache.ibatis.annotations.Update;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.alibaba.fastjson.JSON;
import com.sun.media.jfxmedia.logging.Logger;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;

import lombok.extern.java.Log;

import static cn.itcast.hotel.constants.HotelConstants.HOTEL_MAPPING;

/**
 * @Description
 * @Author tanlinqing
 * @Date 2023/4/17 16:46
 */
@SpringBootTest

public class HotelIndexTest {
    @Autowired
    private IHotelService hotelService;
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
    void  testAddDocument() throws IOException{
        Hotel hotel = hotelService.getById(36934L);
        HotelDoc hotelDoc=new HotelDoc(hotel);
        //准备request对象
        IndexRequest request=new IndexRequest("hotel").id(hotelDoc.getId().toString());
        //准备json文档
        request.source(JSON.toJSONString(hotelDoc),XContentType.JSON);
       //  发送请求
        client.index(request,RequestOptions.DEFAULT);

    }
    @Test
    void  testgetDocument() throws IOException{
       GetRequest request=new GetRequest("hotel","36934");
        GetResponse response=client.get(request,RequestOptions.DEFAULT);

        String json=response.getSourceAsString();
        System.out.println(json);
        System.out.println("------------------");
        HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
        System.out.println(hotelDoc);

    }
    //局部更新
    @Test
    void  testUpdateduc() throws IOException{
        UpdateRequest request=new UpdateRequest("hotel","36934");
        request.doc("age",18,"name","megumi");
        client.update(request,RequestOptions.DEFAULT);
    }
    void  testdelduc() throws IOException{
        DeleteRequest request = new DeleteRequest("hotel","36934");
        client.delete(request,RequestOptions.DEFAULT);
    }
    @Test
    public void test() {
        System.out.println("restHighLevelClient初始化成功" + client);
    }
    //批量增加
    @Test
    void bitchadd() throws IOException{
        // 批量查询酒店数据
        List<Hotel> hotels = hotelService.list();
        BulkRequest request = new BulkRequest();
        hotels.stream().forEach(e->{
            HotelDoc hotelDoc = new HotelDoc(e);
            System.out.println(hotelDoc);
            request.add(new IndexRequest("hotel")
                    .id(hotelDoc.getId().toString())
                    .source(JSON.toJSONString(hotelDoc), XContentType.JSON));
        });
        client.bulk(request, RequestOptions.DEFAULT);
        // 1.创建Request
        /*  BulkRequest request = new BulkRequest();*/
        // 2.准备参数，添加多个新增的Request
     /*   for (Hotel hotel : hotels) {
            // 2.1.转换为文档类型HotelDoc
            HotelDoc hotelDoc = new HotelDoc(hotel);
            // 2.2.创建新增文档的Request对象
            request.add(new IndexRequest("hotel")
                    .id(hotelDoc.getId().toString())
                    .source(JSON.toJSONString(hotelDoc), XContentType.JSON));
        }*/
        // 3.发送请求
    }
  //  增加索引
    @Test
    void  createHotelIndex() throws IOException{
        //创建对象
        //准请求参数dsl
        //发送请求
        CreateIndexRequest request = new CreateIndexRequest("hotel");
        request.source(HOTEL_MAPPING, XContentType.JSON);
        client.indices().create(request, RequestOptions.DEFAULT);
    }
 //   删除索引
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
