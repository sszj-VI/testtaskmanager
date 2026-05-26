package com.example.testtaskmanager.mapper;

import com.example.testtaskmanager.entity.Task;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskMapper {

    @Insert("""
            INSERT INTO task(title, description, status, created_time, updated_time)
            VALUES(#{title}, #{description}, #{status}, #{createdTime}, #{updatedTime})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Task task);
    @Deprecated
    @Select("""
            SELECT id, title, description, status, created_time, updated_time
            FROM task
            ORDER BY id DESC
            """)
    List<Task> findAll();

    @Select("""
            SELECT id, title, description, status, created_time, updated_time
            FROM task
            WHERE id = #{id}
            """)
    Task findById(Long id);

    @Update("""
            UPDATE task
            SET status = #{status}, updated_time = #{updatedTime}
            WHERE id = #{id}
            """)
    int updateStatus(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("updatedTime") LocalDateTime updatedTime);

    @Delete("""
            DELETE FROM task
            WHERE id = #{id}
            """)
    int deleteById(Long id);

    @Select("""
        <script>
        SELECT id, title, description, status, created_time, updated_time
        FROM task
        <where>
            <if test="status != null and status != ''">
                status = #{status}
            </if>
        </where>
        ORDER BY id DESC
        LIMIT #{pageSize} OFFSET #{offset}
        </script>
        """)
    List<Task> findPage(
            @Param("status") String status,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );

    @Select("""
        <script>
        SELECT COUNT(*)
        FROM task
        <where>
            <if test="status != null and status != ''">
                status = #{status}
            </if>
        </where>
        </script>
        """)
    Long count(@Param("status") String status);
}
