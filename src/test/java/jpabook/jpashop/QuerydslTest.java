package jpabook.jpashop;

//import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class QuerydslTest {
    @Autowired
    EntityManager em;

//    @Test
//    @DisplayName(value="테스트")
//    void querydslTest(){
//        Book book = new Book();
//        em.persist(book);
//        JPAQueryFactory query = new JPAQueryFactory(em);
//        QBook qBook =
//    }
}
