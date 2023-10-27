package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.Movie;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemService itemService;

    @Test
    @Rollback(false)
    void 새상품저장 (){
        //given
        Item book = new Book();
        //when
        itemService.saveItem(book);
        //then
        assertEquals( itemRepository.findOne(book.getId()),book);
    }
    @Test
    void 기존상품저장(){
        //given
        Item book  = new Book();
        book.setName("before");
        itemService.saveItem(book);
        //when
        book.setName("after");
        itemService.saveItem(book);
        //then
        assertEquals(itemRepository.findByName("before").size(),0);
        assertEquals(itemRepository.findByName("after").size(), 1);
    }
    @Test
    void 상품여러개조회(){
        //given
        Item book = new Book();
        Item album = new Album();
        Item movie = new Movie();
        List<Item> itemList = new ArrayList<>();
        itemList.add(book);
        itemList.add(album);
        itemList.add(movie);
        //when
        itemService.saveItemList(itemList);
        //then
        org.assertj.core.api.Assertions.assertThat(itemService.findItems()).isEqualTo(itemList);
    }
    @Test
    void 상품명단조회(){
        //given
        Item book = new Book();
        Item album = new Album();
        Item movie = new Movie();
        List<Item> itemList = new ArrayList<>();
        itemList.add(book);
        itemList.add(album);
        itemList.add(movie);
        //when
        itemService.saveItemList(itemList);
        itemList = itemRepository.findAll();
        Long savedId = itemList.get(0).getId();
        //then
        assertEquals(itemRepository.findOne(savedId).getId(),savedId);
    }
    @Test
    void 상품이름조회(){
        //given
        Item book = createItem("book",3000,20,0);
        Item album = createItem("album",2000,14,1);
        Item movie = createItem("movie",2300,2,2);
        List<Item> itemList = new ArrayList<>();
        itemList.add(book);
        itemList.add(album);
        itemList.add(movie);
        //when
        itemService.saveItemList(itemList);
        itemList = itemService.findItems();
        //then
        assertEquals(itemRepository.findOne(itemList.get(0).getId()),itemRepository.findByName("book").get(0));
        assertNotEquals(itemRepository.findOne(itemList.get(0).getId()),itemRepository.findByName("album").get(0));
    }
    @Test
    void 재고감소(){
        //given
        int totalStock = 10;
        Item book = createItem("check",3000, totalStock, 0);
        //when
        int stock = 2;
        int overStock = 11;
        book.removeStock(stock);
        //then
        assertEquals(totalStock-stock, book.getStockQuantity(), "재고 감소 처리");
        assertThrows(NotEnoughStockException.class,()->{
            book.removeStock(overStock);
        },"재고를 초과한 수량 요청이 처리됨");
    }

    private Item createItem(String name, int price, int stockQuantity,int type){
        Item item;
        if(type == 0){
            item = new Book();
        }else if( type == 1){
            item = new Album();
        }else if( type == 2){
            item = new Movie();
        }else{
            throw new IllegalStateException();
        }
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        return item;
    }
}