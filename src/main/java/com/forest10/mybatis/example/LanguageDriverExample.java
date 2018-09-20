package com.forest10.mybatis.example;

import com.forest10.mybatis.enhance.SelectInExtendedLanguageDriver;
import com.forest10.mybatis.enhance.UpdateExtendedLanguageDriver;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author Forest10
 * @date 2018/9/18 下午2:58
 */
public interface LanguageDriverExample {

    /**
     * 复杂版本 select
     *
     * @param userIds
     * @return
     */
    @Select({"<script>", "SELECT *", "FROM user", "WHERE id IN", "<foreach item='item' index='index' collection='list'",
                "open='(' separator=',' close=')'>", "#{item}", "</foreach>", "</script>"})
    List selectUsers(@Param("userIds") List<Integer> userIds);

    /**
     * 简化版本 Select IN
     *
     * @param userIds
     * @return
     */
    @Lang(value = SelectInExtendedLanguageDriver.class)
    List selectUsersUseAnnotation(@Param("userIds") List<Integer> userIds);

    /**
     * 简化版本 Update
     *
     * @param user
     * @return
     */
    @Update("UPDATE user (#{user}) WHERE id =#{userId}")
    @Lang(UpdateExtendedLanguageDriver.class)
    int updateUser(User user);

}
