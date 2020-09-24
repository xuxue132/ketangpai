package com.example.classroomschool.Dao;

import com.example.classroomschool.Model.Sw;
import com.example.classroomschool.Model.SwExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SwMapper {
    int countByExample(SwExample example);

    int deleteByExample(SwExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Sw record);

    int insertSelective(Sw record);

    List<Sw> selectByExample(SwExample example);

    Sw selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Sw record, @Param("example") SwExample example);

    int updateByExample(@Param("record") Sw record, @Param("example") SwExample example);

    int updateByPrimaryKeySelective(Sw record);

    int updateByPrimaryKey(Sw record);

    int updateRead(int student, int homeworkId, String review);

    int updateScore(int student, int homeworkId, String review, int score);

    Sw selectHomework(int student, int homework);

    int deleteByHomeworkId(int homeworkId);
}