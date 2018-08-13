package com.zip.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.*;

/**
 * 将文件打包成ZIP压缩文件
 */
public final class ZipUtil {

    private ZipUtil() {}

    static final int BUFFER = 8192;

    private static Logger logger = LoggerFactory.getLogger(ZipUtil.class);

    /**
     * 压缩文件组
     * @param srcPath 文件路径的父路径
     * @param paths 文件路径列表
     * @param dstPath 压缩后文件路径（全路径，包含文件名）
     * @throws IOException
     */
    public static void compress(String srcPath, List<String> paths, String dstPath) throws IOException {
        //判空
        if (paths == null || paths.size() == 0) {
            logger.info("待压缩文件组为空");
            return;
        }
        //判空父路径
        if (srcPath == null) {
            srcPath = "";
        }
        //转化为文件组，并处理
        int length = paths.size();
        File[] files = new File[length];
        for (int i = 0; i < length; i++) {
            files[i] = new File(srcPath + paths.get(i));
        }
        compress(files, dstPath);
    }

    /**
     * 压缩文件组
     * @param srcPath
     * @param filePaths 文件路径组
     * @param dstPath 压缩后文件路径（全路径，包含文件名）
     * @throws IOException
     */
    public static void compress(String srcPath, String[] filePaths, String dstPath) throws IOException {
        //判空
        if (filePaths == null || filePaths.length == 0) {
            logger.info("待压缩文件组为空");
            return;
        }
        //判空父路径
        if (srcPath == null) {
            srcPath = "";
        }
        //转化为文件组，并处理
        int length = filePaths.length;
        File[] files = new File[length];
        for (int i = 0; i < length; i++) {
            files[i] = new File(srcPath + filePaths[i]);
        }
        compress(files, dstPath);
    }

    /**
     * 压缩一个文件对象组
     * @param files
     * @param dstPath
     * @throws IOException
     */
    public static void compress(File[] files, String dstPath) throws IOException {
        //判空
        if (files == null || files.length == 0) {
            logger.info("待压缩文件组为空");
            return;
        }
        File dstFile = new File(dstPath);
        FileOutputStream out = null;
        ZipOutputStream zipOut = null;
        try {
            //构建输出流
            out = new FileOutputStream(dstFile);
            //过滤流，维护数据校验和
            CheckedOutputStream cos = new CheckedOutputStream(out,new CRC32());
            //zip输出流
            zipOut = new ZipOutputStream(cos);
            String baseDir = "";
            for (File file : files) {
                compress(file, zipOut, baseDir);
            }
        }
        finally {
            if(null != zipOut){
                try {
                    zipOut.close();
                }catch (Exception e) {
                    logger.error("zipOut未正常关闭", e);
                }
            }
            if(null != out){
                try {
                    out.close();
                }catch (Exception e) {
                    logger.error("FileOutput未正常关闭", e);
                }

            }
        }
    }

    /**
     * 将目标文件夹打包成zip文件
     * @param srcPath 目标文件夹路径
     * @param dstPath 打包后zip文件全路径名（路径+文件名）
     * @throws IOException
     */
    public static void compress(String srcPath , String dstPath) throws IOException{
        File srcFile = new File(srcPath);
        File dstFile = new File(dstPath);
        //判断目标文件夹是否存在
        if (!srcFile.exists()) {
            logger.error("{}不存在", srcPath);
            throw new FileNotFoundException(srcPath + "不存在！");
        }
        FileOutputStream out = null;
        ZipOutputStream zipOut = null;
        try {
            //构建输出流
            out = new FileOutputStream(dstFile);
            //过滤流，维护数据校验和
            CheckedOutputStream cos = new CheckedOutputStream(out,new CRC32());
            //zip输出流
            zipOut = new ZipOutputStream(cos);
            String baseDir = "";
            compress(srcFile, zipOut, baseDir);
        }
        finally {
            if(null != zipOut){
                try {
                    zipOut.close();
                }catch (Exception e) {
                    logger.error("zipOut未正常关闭", e);
                }
            }
            if(null != out){
                try {
                    out.close();
                }catch (Exception e) {
                    logger.error("FileOutput未正常关闭", e);
                }

            }
        }
    }

    /**
     * 将目标文件压缩，判断目标文件是目录还是文件，采用合适的方法
     * @param file 目标文件夹文件对象
     * @param zipOut 压缩输出流对象
     * @param baseDir
     * @throws IOException
     */
    private static void compress(File file, ZipOutputStream zipOut, String baseDir) throws IOException{
        if (file.isDirectory()) {//压缩文件夹
            compressDirectory(file, zipOut, baseDir);
        } else {//压缩文件
            compressFile(file, zipOut, baseDir);
        }
    }

    /**
     * 压缩一个目录
     * @param dir 目标文件夹文件对象
     * @param zipOut 压缩输出流对象
     * @param baseDir
     */
    private static void compressDirectory(File dir, ZipOutputStream zipOut, String baseDir) throws IOException{
        //获取子目录或文件的文件对象组
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            //遍历，并处理子目录和文件
            compress(files[i], zipOut, baseDir + dir.getName() + "/");
        }
    }

    /**
     * 压缩一个文件
     * @param file 目标文件文件对象
     * @param zipOut 压缩流对象
     * @param baseDir
     */
    private static void compressFile(File file, ZipOutputStream zipOut, String baseDir)  throws IOException{
        //判断文件是否存在
        if (!file.exists()){
            return;
        }
        BufferedInputStream bis = null;
        try {
            //构建文件输入流
            bis = new BufferedInputStream(new FileInputStream(file));
            //构建文件压缩zip文件条目
            ZipEntry entry = new ZipEntry(baseDir + file.getParentFile().getName() + "/" + file.getName());
            //开始写入zip文件条目
            zipOut.putNextEntry(entry);
            //写入zip文件流
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                zipOut.write(data, 0, count);
            }
            logger.info("{}文件已压缩", file.getAbsolutePath());

        }finally {
            if(null != bis){
                try {
                    bis.close();
                }catch (Exception e) {
                    logger.error("BufferedInputStream文件输入流未正常关闭", e);
                }

            }
        }
    }

    /**
     * 解压压缩文件
     * @param zipFile 目标压缩文件
     * @param dstPath 加压后文件路径
     * @throws IOException
     */
    public static void decompress(String zipFile , String dstPath)throws IOException{
        File pathFile = new File(dstPath);
        //判断解压文件夹是否存在，不存在建立文件夹
        if(!pathFile.exists()){
            pathFile.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile);
        for(Enumeration entries = zip.entries(); entries.hasMoreElements();){
            ZipEntry entry = (ZipEntry)entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = null;
            OutputStream out = null;
            try{
                in =  zip.getInputStream(entry);
                String outPath = (dstPath+"/"+zipEntryName).replaceAll("\\*", "/");;
                //判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                if(!file.exists()){
                    file.mkdirs();
                }
                //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if(new File(outPath).isDirectory()){
                    continue;
                }
                out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[1024];
                int len;
                while((len=in.read(buf1))>0){
                    out.write(buf1,0,len);
                }
            }
            finally {
                if(null != in){
                    try {
                        in.close();
                    }catch (Exception e) {
                        logger.error("InputStream文件输入流未正常关闭", e);
                    }

                }
                if(null != out){
                    try {
                        out.close();
                    }catch (Exception e) {
                        logger.error("OutputStream文件输出流未正常关闭", e);
                    }

                }
            }
        }
    }
}
