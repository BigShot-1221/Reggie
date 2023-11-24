import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Scanner;

@SpringBootTest
public class test {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String str1;
        String str2;
        String str3;



        System.out.print("请输入第一个字符串:");
        str1 = scanner.next();

        System.out.print("请输入第二个字符串:");
        str2 = scanner.next();

        StringBuffer stringBuffer = new StringBuffer(str1 + str2);

        System.out.println("合并的结果为:" + stringBuffer.toString());

        System.out.print("请输入要查找的内容:");
        str3 = scanner.next();

        int indexof = stringBuffer.lastIndexOf(str3);
        if (indexof != -1){
            System.out.println("该内容的下标为:" + indexof);
        }else {
            System.out.println("没有该内容");
        }

        System.out.print("请输入要插入的内容:");
        String str4 = scanner.next();
        System.out.print("请输入要插入的下标:");
        int index1 = scanner.nextInt();

        stringBuffer.insert(index1, str4);
        System.out.print("插入后显示为:" + stringBuffer + "\n");



        System.out.print("请输入要删除的内容:");
        String str5 = scanner.next();

        int index2 = stringBuffer.indexOf(str5);
        int index3 = index2 + str5.length();

        stringBuffer.delete(index2, index3);

        System.out.print("删除后显示为:" + stringBuffer + "\n");

        System.out.print("请输入要替换的内容:");
        String str6 = scanner.next();

        System.out.print("请输入被替换的内容:");
        String str7 = scanner.next();

        String str8 = stringBuffer.toString();
        str8 = str8.replace(str7, str6);

        System.out.print("替换后输出为:" + str8);
    }
}
