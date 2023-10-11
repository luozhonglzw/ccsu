package cn.ccsu.cecs.test;

import cn.ccsu.cecs.config.ThreadConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


@RestController
public class MainTest {

    @Autowired
    ApplicationContext applicationContext;

    @PostMapping("/test")
    public void test1(@RequestBody String id){
        System.out.println(id);
        applicationContext.publishEvent(new MyEvent(121));
    }


    public static void main(String[] args) throws Exception {
        String filePath = "C:\\Users\\YJR\\Desktop\\2022年-大创网-项目计划书\\21物业02";
        List<File> fileList = getFileList(filePath);

    }

    public static List<File> getFileList(String strPath) throws Exception {
        int fileCount = 1;

        List<File> filelist = new ArrayList<>();
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(files[i].getAbsolutePath()); // 获取文件绝对路径
                } else  { // 判断文件名
                    String strFileName = files[i].getAbsolutePath();
//                    System.out.println("---" + strFileName);
                    filelist.add(files[i]);
                }
            }

        }

        String newFileName = "C:\\Users\\YJR\\Desktop\\2022年-大创网-项目计划书\\21物业02\\";

        // 获取文件的后缀名
        for (int i = 0; i < filelist.size(); i++) {
            fileCount = i / 35 + 1;
            // 获取文件路径
            String absolutePath = filelist.get(i).getAbsolutePath();
            // 获取文件后缀名
            String[] split = filelist.get(i).getName().split("\\.");
            String fileSuffixName = split[split.length - 1];

            int myI = i % 35 + 1;

            // 创建输出流
            String newFile = newFileName + myI + "." + fileSuffixName;
            if(newFile.equalsIgnoreCase(absolutePath)) {
                continue;
            }
            FileOutputStream outputStream = new FileOutputStream(newFile);
            // 创建输入流
            FileInputStream fileInputStream = new FileInputStream(absolutePath);
            byte[] bytes = new byte[fileInputStream.available()];
            int len;
            while((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0 , len);
            }

            outputStream.close();
            fileInputStream.close();
        }

        return filelist;
    }


    @Test
    public void test(){
        for (int i = 1; i < 23; i++) {
            File file = new File("C:\\Users\\YJR\\Desktop\\临时\\文件夹\\" + i);
            file.mkdirs();
        }
    }

}
