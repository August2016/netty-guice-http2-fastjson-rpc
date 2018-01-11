package example;

/**
 * Created by liuchengjun on 2018/1/8.
 */
public class SampleServiceImpl implements SampleService {
    public void setA() {
        System.out.println("A");
    }
    public void setB(String a) {
        System.out.println(a);
    }
    public String setC(String c) {
        System.out.println(c);
        return c;
    }

}
