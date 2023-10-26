package jpabook.jpashop.exception;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    /**
     * 상품 추가/수정
     * @param item
     */
    public void save(Item item){
        if(item.getId()==null){
            //id가 없는 경우 = 영속성이 없음 = 저장안됨 = 새로운 대상 추가
            em.persist(item);
        } else{
            //id가 있는 경우 데이터 병합
            em.merge(item);
        }
    }
    public void saveItems(List<Item> itemList){
        for(Item item : itemList){
            save(item);
        }
    }

    /**
     * 전체 목록
     * @return
     */
    public List<Item>findAll(){
        return em.createQuery("select i from Item i ",Item.class).getResultList();
    }

    /**
     * id로 하나 찾기
     * @param id
     * @return
     */
    public Item findOne(Long id){
        return em.find(Item.class,id);
    }

    /**
     * 이름으로 하나 찾기
     * @param name
     * @return
     */
    public List<Item>findByName(String name){
        return em.createQuery("select i from Item i where i.name = :searchKey",Item.class)
                .setParameter("searchKey",name)
                .getResultList();
    }
}
