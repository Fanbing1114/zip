package com.zip.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public final class FileUtil {

    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {}

    /**
     * 递归删除空的子目录
     * @param dir 父目录
     */
    public static void removeEmptyDir(File dir) {
        if (dir.isDirectory()) {
            File[] fs = dir.listFiles();//得到子目录
            if (fs != null && fs.length > 0) {//有子目录
                int length = fs.length;
                for (int i = 0; i < length; i++) {
                    File tmpFile = fs[i];
                    //清除空子目录
                    if (tmpFile.isDirectory()) {
                        removeEmptyDir(tmpFile);
                    }
                    //如果空子目录清除完，当前目录也为空，清除
                    if (tmpFile.isDirectory() && tmpFile.listFiles().length <= 0) {
                        tmpFile.delete();
                    }
                }
            }
            //如果空子目录清除完，当前目录也为空，清除
            if (dir.isDirectory() && dir.listFiles().length == 0) {
                dir.delete();
            }
        }
    }

    /**
     * 删除文件组
     * @param paths 文件组路径列表
     */
    public static void removeFiles(String srcPath, List<String> paths) {
        if (paths == null || paths.size() == 0) {
            return;
        }
        //转化为文件组，并处理
        int length = paths.size();
        File[] files = new File[length];
        for (int i = 0; i < length; i++) {
            files[i] = new File(srcPath + paths.get(i));
        }
        removeFiles(files);
    }

    /**
     * 删除文件组
     * @param filePaths 文件组路径
     */
    public static void removeFiles(String srcPath, String[] filePaths) {
        //判空
        if (filePaths == null || filePaths.length == 0) {
            return;
        }
        //转化为文件组，并处理
        int length = filePaths.length;
        File[] files = new File[length];
        for (int i = 0; i < length; i++) {
            files[i] = new File(srcPath + filePaths[i]);
        }
        removeFiles(files);
    }

    /**
     * 删除文件组
     * @param files 文件组
     */
    public static void removeFiles(File[] files) {
        // 判空
        if(files == null || files.length == 0) {
            return;
        }
        // 循环处理文件
        for (File file : files) {
            // 判断文件是否存在
            if (!file.exists()) {
                continue;
            }
            // 判断并处理文件夹
            if (file.isDirectory()) {
                removeDirectory(file);
            }
            //删除文件或文件目录
            file.delete();
            logger.info(file.getAbsolutePath() + "文件已删除");
        }
    }

    /**
     * 删除文件夹
     * @param file 文件对象
     */
    private static void removeDirectory(File file) {
        File[] files = file.listFiles();//获取子目录及文件
        removeFiles(files);//删除
    }
}
