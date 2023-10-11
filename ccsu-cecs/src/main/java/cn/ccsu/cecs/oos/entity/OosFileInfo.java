package cn.ccsu.cecs.oos.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OosFileInfo {
    private String fileName;
    private String postfixName;
    private InputStream inputStream;
}
