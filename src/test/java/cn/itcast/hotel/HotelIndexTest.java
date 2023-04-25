package cn.itcast.hotel;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.apache.http.HttpHost;
import org.apache.ibatis.annotations.Update;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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
    @Test
    void testMatchall() throws IOException{
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.matchAllQuery()).size(20);
        SearchResponse response= client.search(request, RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        TotalHits total = searchHits.getTotalHits();
        System.out.println("搜索到"+total+"条数据");
        SearchHit[] hits = searchHits.getHits();
        Stream.of(hits).forEach(e->{
            String json = e.getSourceAsString();
            HotelDoc hotelDoc=JSON.parseObject(json,HotelDoc.class);
            System.out.println("hotelDoc="+hotelDoc);});
    }
    @Test
    void testT() throws ParseException{
       /* if(complexReqInfoVo.getIfUnloading().equals("01")&&complexReqInfoVo.getDeliveryForm().equals("01")){*/
        ArrayList<timeTest> delList = new ArrayList<>();
        timeTest timeTest1 = new timeTest();
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date1 = df2.parse("2023-04-21 10:04");
        Date date2 = df2.parse("2023-04-22 14:04");
        Date date3 = df2.parse("2023-04-23 10:04");
        Date date4 = df2.parse("2023-04-23 10:08");
        Date date5 = df2.parse("2023-04-21 10:06");
        Date date6 = df2.parse("2023-04-21 14:03");
        Date date7 = df2.parse("2023-04-20 10:04");
        Date date8 = df2.parse("2023-04-20 14:04");
        timeTest1.setArrivalEndDate(date2);
        timeTest1.setArrivalStartDate(date1);
        /*timeTest timeTest2 = new timeTest();
        timeTest2.setArrivalEndDate(date8);
        timeTest2.setArrivalStartDate(date7);
        timeTest timeTest3 = new timeTest();
        timeTest3.setArrivalEndDate(date4);
        timeTest3.setArrivalStartDate(date3);*/
        timeTest timeTest5 = new timeTest();
        Date date00= df2.parse("2023-04-21 10:04");
        Date date9 = df2.parse("2023-04-21 14:04");
        timeTest5.setArrivalEndDate(date9);
        timeTest5.setArrivalStartDate(date00);
    //    timeTest timeTest4 = new timeTest();
      /*  timeTest4.setArrivalEndDate(date6);
        timeTest4.setArrivalStartDate(date5);*/
        delList.add(timeTest1);
        delList.add(timeTest5);
//        delList.add(timeTest2);
  //      delList.add(timeTest3);
  //      delList.add(timeTest4);
        for(int i=0;i<delList.size()-1;i++){
                Date   start0=delList.get(i).getArrivalStartDate();
                Date  end0=delList.get(i).getArrivalEndDate();
                for(int j=i+1;j<delList.size();j++){
                    Date start1=delList.get(j).getArrivalStartDate();
                    Date  end1=delList.get(j).getArrivalEndDate();
                    //不重复不处理
                    if(start0.compareTo(end1)>0 || end0.compareTo(start1)<0){
                        System.out.println("时间段不重复");
                    }else{
                        System.out.println("有时间段重复");
                    }
                }

            }

        }
    }