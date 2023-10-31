package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("")
    public String orderFrom(Model model){
        List<Member> members = memberService.findMemebers();
        List<Item> itemList = itemService.findItems();
        model.addAttribute("members",members);
        model.addAttribute("items",itemList);
        return "order/orderForm";
    }
    @PostMapping("")
    public String addOrder(@RequestParam("memberId") Long memberId, //form-summit의 name을 기준으로 받음
                           @RequestParam("itemId") Long itemId,
                           @RequestParam("count") int count){
        Long orderedId = orderService.order(memberId,itemId,count); //컨트롤러에서는 entity(member)를 받기보다 필요 값만 전달
        //주문 상세페이지를 만든 경우 orderedId 사용
        return "redirect:/order/orders";
    }
    @GetMapping("/orders")
    public String orderList(Model model, @ModelAttribute("orderSearch") OrderSearch orderSearch){
        List<Order> searchedOrders = orderService.findOrders(orderSearch);
        model.addAttribute("orders",searchedOrders);
        return "order/orderList";
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/order/orders";
    }

}
