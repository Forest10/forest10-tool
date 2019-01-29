package com.forest10.common;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * @author Forest10
 * @date 2019-01-29 16:38
 */
public class FileCmd {

    /**
     * 得到某一个目录下所有的文件
     *
     */
    public static List<File> getAllFile(List<File> fileList, String path,
        List<String> excludeFileName) {
        File file = new File(path);
        if (!file.exists()) {
            return Collections.emptyList();
        }
        File[] list = file.listFiles();
        for (File innerFile : list) {
            if (excludeFileName.contains(innerFile.getName())) {
                continue;
            }
            if (innerFile.isFile()) {
                fileList.add(innerFile);
            } else {
                getAllFile(fileList, innerFile.getAbsolutePath(), excludeFileName);
            }

        }
        return fileList;
    }
}
