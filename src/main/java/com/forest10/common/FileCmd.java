package com.forest10.common;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Forest10
 * @date 2019-01-29 16:38
 */
public class FileCmd {

    /**
     * 得到某一个目录下所有的文件, 使用getCanonicalFile规避系统间不同
     */
    public static List<File> getAllFile(List<File> fileList, File file,
        List<String> excludeFileName) throws IOException {

        if (Objects.isNull(file) || !file.exists()) {
            return Collections.emptyList();
        }
        File[] filesList = file.listFiles();
        for (File innerFile : filesList) {
            if (excludeFileName.contains(innerFile.getName())) {
                continue;
            }
            if (innerFile.isFile()) {
                fileList.add(innerFile);
            } else {
                getAllFile(fileList, innerFile.getCanonicalFile(), excludeFileName);
            }

        }
        return fileList;
    }
}
