package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
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
    void 상품주문() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");
        member.setAddress(new Address("서울", "종로1가","123-123"));
        memberRepository.save(member);

        Book book = new Book();
        book.setName("check");
        book.setPrice(3000);
        book.setStockQuantity(10);
        itemRepository.save(book);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(),book.getId(),orderCount);
        Order savedOrder = orderService.getOrder(orderId);
        //then
    }
    @Test
    void 상품주문취소() throws Exception{
        //given

        //when

        //then
    }
    @Test
    void 재고수량초과() throws Exception{
        //given

        //when

        //then
    }
}