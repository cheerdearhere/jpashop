package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    //dependency injection
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     * @param memberId
     * @param itemId
     * @param count
     * @return
     */
    @Transactional
    public Long order(Long memberId,Long itemId, int count){
        //find each Entity
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());//실무에서는 입력받아 사용

        //주문 상품
        OrderItem orderItem = OrderItem.createOrderItem(item,item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member,delivery,orderItem);

        //주문 저장
        orderRepository.save(order);// cascade를 했기 대문에 따로 persist를 할 필요가 없다.
        //만약 order가 다른 곳에서 다른 용도로 사용된다면 cascade를 하지말고 각각 따로 작성

        //결과 반환
        return order.getId();
    }

    /**
     * 이전에 작업해 놓은 경우 편리
     * @param orderId
     */
    @Transactional
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findOne(orderId);
        order.orderCancel();
    }

    public Order getOrder(Long orderId){
        return orderRepository.findOne(orderId);
    }
// 임시
//    public List<Order> findOrders(OrderSearch orderSearch){
//        return orderRepository.findAll(orderSearch);
//    }

}
