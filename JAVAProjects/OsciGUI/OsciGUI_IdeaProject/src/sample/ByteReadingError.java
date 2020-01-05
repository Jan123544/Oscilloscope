package sample;

public class ByteReadingError extends RuntimeException{
    private int numRead;
    ByteReadingError(int numRead){
        super();
        this.numRead = numRead;
    }

    public int getNumRead(){
        return numRead;
    }
}
