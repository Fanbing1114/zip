package com.zip.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ZipDao {

    //测试方法
    @Select("select path from zip")
    List<String> selectPaths();

    @Select(" select file_path from tb_datum as da " +
            " left join tb_indemnify_medical as me " +
            " on da.contact_id = me.reference_number " +
            " where me.apply_date between #{old} and #{now}")
    List<String> selectFilePathBetweenOldNow(@Param("old") String old, @Param("now") String now);

    @Select(" select file_path from tb_datum as da " +
            " left join tb_indemnify_medical as me " +
            " on da.contact_id = me.reference_number " +
            " where me.apply_date < #{time}")
    List<String> selectFilePathBeforeTime(String time);

    @Update(" update tb_datum set status = 'N' where file_path = #{filePath}")
    void updateStatusByFilePath(String filePath);

}
