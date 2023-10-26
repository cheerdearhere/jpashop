package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.ItemRepository;
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
