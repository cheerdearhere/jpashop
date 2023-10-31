package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.dto.param.BookForm;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }
    @Transactional
    public void updateItem(Long itemId, BookForm bookParam){ //parameter로 넘어온 준영속 상태의 entity
        changeItemProperties(itemId, bookParam.getName(), bookParam.getPrice(),bookParam.getStockQuantity(),bookParam.getAuthor(),bookParam.getIsbn());
    }
    /**
     * 준영속 엔티티에 영속성 추가해서 리턴
     */
    private void changeItemProperties(Long itemId, String name, int price, int stockQuantity, String author, String isbn) {
        Item foundItem = itemRepository.findOne(itemId); //같은 id를 지닌 영속성 entity 조회
//        if(foundItem.getType==="B")
        Book foundBook = (Book)foundItem;
        foundItem.setName(name);
        foundItem.setPrice(price);
        foundItem.setStockQuantity(stockQuantity);
        foundBook.setAuthor(author);
        foundBook.setIsbn(isbn);
//        return foundBook;
    }

    @Transactional
    public void saveItemList(List<Item> itemList){
        itemRepository.saveItems(itemList);
    }
    public List<Item> findItems(){
        return itemRepository.findAll();
    }
    public Item findItemByID(Long id){
        return itemRepository.findOne(id);
    }
    public List<Item> findItemsByName(String Name){
        return itemRepository.findByName(Name);
    }
}
