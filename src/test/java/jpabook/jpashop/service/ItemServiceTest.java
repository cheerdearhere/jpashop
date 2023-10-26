package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.Movie;
import jpabook.jpashop.exception.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
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
        Item book = new Book();
        book.setName("book");
        Item album = new Album();
        album.setName("album");
        Item movie = new Movie();
        movie.setName("movie");
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
}