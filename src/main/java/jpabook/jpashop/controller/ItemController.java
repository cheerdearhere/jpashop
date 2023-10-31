package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.dto.param.BookForm;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/new")
    public String createForm(Model model){
        model.addAttribute("form",new BookForm());
        return "items/createItemForm";
    }
    @PostMapping("/new")
    public String addItem(BookForm form){//필요에 따라 validation
        Book book = Book.createBook(form.getId(), form.getName(), form.getPrice(), form.getStockQuantity(),form.getAuthor(),form.getIsbn());
        itemService.saveItem(book);
        return "redirect:/items";
    }
    @GetMapping("")
    public String itemList (Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items",items);
        return "items/itemList";
    }

    @GetMapping("/{itemId}/edit")
    public String itemEditForm(Model model, @PathVariable("itemId") Long itemId){
        //예제의 단순화를 위해 item은 Book으로 고정. 권장 x
        Book item = (Book) itemService.findItemByID(itemId);
        //해당 데어터 입력
        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());
        //파라미터가 너무 많을 경우 자동화 라이브러리 사용
        //또는 여러줄 선택해서 한번에 서용(alt+드래그)
        model.addAttribute("form",form);
        return "items/updateItemForm";
    }
    @PostMapping("/{itemId}/edit")
    public String editItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form){
//        Book book = new Book(); 어설프게 컨트롤러에서 데이터 객체 만들지 말고 그냥 form 객체 넘기기
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());

        itemService.updateItem(itemId, form);
        //별도로 update 로직을 짜지 않고 변경을 감지해서 저장하도록 함
        return "redirect:/items";
    }
}
