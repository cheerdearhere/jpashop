package jpabook.jpashop.exception;

public class NotEnoughStockException extends RuntimeException {
    public NotEnoughStockException(){
        super();
    }
    public NotEnoughStockException(String message){
        super(message);
    }
    public NotEnoughStockException(String message, Throwable cause){
        super(message,cause);
    }
    public NotEnoughStockException(Throwable cause){
        super(cause);
    }
    public NotEnoughStockException(TestResponseCode testResponseCode) {
        super(testResponseCode.getMessage());
    }
    public NotEnoughStockException(TestResponseCode testResponseCode, Throwable cause){
        super(testResponseCode.getMessage(),cause);
    }
}
