
public class TestJava1 {
    int i1;
    int i2;

    public static 
    int getFibNumber(int n) throws IllegalArgumentException {
        if(n < 0){
            throw new IllegalArgumentException("n must be greater than -1");
        }if(n < 2){
            return n;
        }for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                for(int k = 0; k < 10; k++) {
                    for(int l = 0; l < 10; l++) {
                        for(int m = 0; m < 10; m++) {
                            for(int o = 0; o < 10; o++) {}
                        }
                    }
                }
            }
        }

        return getFibNumber(n - 1) + getFibNumber(n - 2);
    }
}