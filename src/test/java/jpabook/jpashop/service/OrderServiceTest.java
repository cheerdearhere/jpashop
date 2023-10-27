package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ItemRepository itemRepository;
    @Test
    void 상품주문() {
        //given
        Member member = createMember("kim", "서울", "종로1가", "123-123");
        Item book = createItem("check", 3000, 10);
        memberRepository.save(member);
        itemRepository.save(book);
        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(),book.getId(),orderCount);
        Order savedOrder = orderService.getOrder(orderId);

        //then
        assertEquals(OrderStatus.ORDER, savedOrder.getStatus(),"상품 주문시 상태는 ORDER");
        assertEquals(1, savedOrder.getOrderItems().size(),"주문한 상품의 종류 수");
        assertEquals(3000 * 2, savedOrder.getTotalPrice(), "상품가격 * 수량");
        assertEquals(10-2, book.getStockQuantity(),"주문한 수량만큼 재고 변경");
    }
    @Test
    void 재고수량초과() {
        //given
        int totalStock = 10;
        Member member = createMember("kim", "서울", "종로1가", "123-123");
        Item book = createItem("check", 3000, totalStock);
        //when
        int orderCount = 11;
        //then
        assertThrows(RuntimeException.class,()->{ //여러단계를 거칠경우 custom Exception은 작동하지 않을 수 있음
            orderService.order(member.getId(), book.getId(),orderCount);
        },"재고를 초과한 수량의 주문요청이 수행됨 (주문 수량:"+orderCount+"/재고 수량:"+totalStock+")");
    }
    @Test
    void 상품주문취소() {
        //given
        int totalStock = 10;
        Member member = createMember("kim","NewYork","fine street","132-456");
        Item book = createItem("jpa protector",10000,totalStock);
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        //when
        orderService.cancelOrder(orderId);
        //then
        Order getOrder = orderService.getOrder(orderId);
        assertEquals(totalStock,book.getStockQuantity(),"재고가 복원되지 않음");
        assertEquals(OrderStatus.CANCEL,getOrder.getStatus(),"주문상태 변경 안됨");
    }
//  data  for test
    private Member createMember(String name, String city, String street, String zipcode){
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address(city, street, zipcode));
        memberRepository.save(member);
        return member;
    }
    private Item createItem(String name, int price, int stockQuantity){
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        itemRepository.save(book);
        return book;
    }
}