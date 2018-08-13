package com.zip.service;

import com.zip.constant.ZipConstant;
import com.zip.util.FileUtil;
import com.zip.util.TimeUtil;
import com.zip.util.ZipUtil;
import com.zip.dao.ZipDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@PropertySource("classpath:path.properties")
@EnableTransactionManagement
public class ZipService {

    private Logger logger = LoggerFactory.getLogger(ZipService.class);

    @Autowired
    private ZipDao zipDao;

    @Value("${srcPath}")
    private String srcPath;

    @Value("${zipPath}")
    private String zipPath;

    //                秒   分   时   天    月   星期   （年份）
    // @Scheduled(cron="0 0 0 1 1/3 *")
    @Scheduled(cron="0/30   *   *    *    *    ?        ")
    @Transactional
    public void zipFiles() throws IOException {

        logger.info("开始压缩文件:被压缩文件父路径src_path = {},压缩后文件父路径zip_path = {}", srcPath, zipPath);

        // 压缩文件
        String now = TimeUtil.getTimeBeforeSomeMonth(ZipConstant.NOW_0_DAY);
        String old = TimeUtil.getTimeBeforeSomeMonth(ZipConstant.BEFORE_3_MONTH);// 计算时间
        List<String> paths = zipDao.selectPaths();// 测试，获取三个月之前至今的文件路径信息
        //List<String> paths = zipDao.selectFilePathBetweenOldNow(old, now);// 获取三个月之前至今的文件路径信息
        ZipUtil.compress(srcPath, paths,  zipPath + now + ZipConstant.ZIP);// 打包
        logger.info("{}至{}打包完毕", old, now);

        // 更改数据库状态
        int length = paths.size();
        for (int i = 0; i < length; i++){
            //zipDao.updateStatusByFilePath(paths.get(i));
        }
        logger.info("数据库状态更改完毕");

        // 删除六个月前的文件
        String time = TimeUtil.getTimeBeforeSomeMonth(ZipConstant.BEFORE_6_MONTH);
        List<String> pathsBeforeTime = zipDao.selectPaths(); // 测试，获取6个月之前的文件路径
        //List<String> pathsBeforeTime = zipDao.selectFilePathBeforeTime(time);// 获取6个月之前的文件路径
        FileUtil.removeFiles(srcPath, pathsBeforeTime);// 删除文件组
        FileUtil.removeEmptyDir(new File(srcPath));// 删除空文件夹
        logger.info("{}路径下{}之前的文件及空文件夹删除完毕", srcPath, time);
    }
}
