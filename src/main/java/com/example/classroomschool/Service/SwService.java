package com.example.classroomschool.Service;

import com.example.classroomschool.Dao.SwMapper;
import com.example.classroomschool.Model.Sw;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SwService {
    @Resource
    SwMapper swMapper;

    public int readHomework(int student, int homeworkId, String review) {
        return swMapper.updateRead(student,homeworkId,review);
    }

    public int scoringHomework(int student, int homeworkId, String review, int score) {
        return swMapper.updateScore(student,homeworkId,review,score);
    }

    public int handIn(Sw sw) {
        return swMapper.insert(sw);
    }

    public Sw getHomework(int student, int homework) {
        return swMapper.selectHomework(student,homework);
    }

    public int update(Sw sw) {
        return swMapper.updateByPrimaryKey(sw);
    }

    public int delete(int homeworkId){
        return swMapper.deleteByHomeworkId(homeworkId);
    }
}
