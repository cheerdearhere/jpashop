package jpabook.jpashop.dto.param;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookForm {
        //items
        private Long id;
        private String name;
        private int price;
        private int stockQuantity;
        //book
        private String author;
        private String isbn;
}
