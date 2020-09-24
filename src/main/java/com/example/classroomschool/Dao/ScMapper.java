package com.example.classroomschool.Dao;

import com.example.classroomschool.Model.Sc;
import com.example.classroomschool.Model.ScExample;
import java.util.List;
import java.util.Map;

import com.example.classroomschool.Model.Tc;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScMapper {
    int countByExample(ScExample example);

    int deleteByExample(ScExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Sc record);

    int ChangeTop(int id,int CourseId,String top);

    int insertSelective(Sc record);

    List<Sc> selectByExample(ScExample example);

    Sc selectByPrimaryKey(Integer id);

    int deleteByCourse(Integer course);

    Sc selectSc(int student, int course);

    int SortNumber(int course,int student,int displayNum);

    int CourseDelete(int student,int course);

    int pigeonholeC(int student,int course,String pigeonhole);

    int updateByExampleSelective(@Param("record") Sc record, @Param("example") ScExample example);

    int updateByExample(@Param("record") Sc record, @Param("example") ScExample example);

    int updateByPrimaryKeySelective(Sc record);

    int updateByPrimaryKey(Sc record);

    List<Sc> selectAllSc(int course);

    List<Sc> ListStudent(int student);

    int pigeonholeTheCourse(int course, String pigeonhole);
}