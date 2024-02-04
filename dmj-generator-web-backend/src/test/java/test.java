import com.dmj.dmjpermission.client.DmjClient;
import com.dmj.web.MainApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = MainApplication.class)
public class test {
    @Resource
    DmjClient dmjClient;
    @Test
    public void test(){
        dmjClient.getToken(new Object());
    }
}
