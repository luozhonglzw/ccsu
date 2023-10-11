package cn.ccsu.cecs.common.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Objects;

public class IOUtils {
    public static InputStream cloneInputStream(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }

        baos.flush();
        baos.close();
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public static File multipartFileToFile(MultipartFile file) throws Exception {
        File toFile = null;
        if (!file.equals("") && file.getSize() > 0) {
            InputStream ins;
            ins = file.getInputStream();
            toFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    // 获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
