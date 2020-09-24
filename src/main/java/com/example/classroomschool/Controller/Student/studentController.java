package com.example.classroomschool.Controller.Student;

import com.example.classroomschool.Controller.Token.TokenUtil;
import com.example.classroomschool.Controller.encryption.MDFive;
import com.example.classroomschool.Model.*;
import com.example.classroomschool.Service.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class studentController {
    @Resource
    ScCourseService scCourseService;
    @Resource
    LoginstudentService loginstudentService;
    @Resource
    ScService scService;
    @Resource
    TcService tcService;
    @Resource
    CourseService courseService;
    @Resource
    LoginteacherService loginteacherService;
    @Resource
    HomeworkService homeworkService;
    @Resource
    SwService swService;


    /**
     * 学生课程
     * */
    @RequestMapping(value = "/course")
    public List<ScCourse>  AllCourse(@RequestBody Map<String,String> token)  {
        String account = TokenUtil.getUsername(token.get("token"));
        Student student = loginstudentService.existenceAccount(account);
        List<ScCourse>  scCourses = scCourseService.getCourse(student.getId());
        for(int i=0;i<scCourses.size();i++){
            List<Sc> sc = scService.getNumber(scCourses.get(i).getCourseId());
            scCourses.get(i).setStudentNum(sc.size());
        }
        return scCourses;
    }

    /**
     * 课程置顶
     * */
    @RequestMapping(value = "/changeTop")
    public String changeTheTop(@RequestBody Map<String,String> data){
        String top = "0";
        int id = Integer.parseInt(data.get("id"));
        int CourseId = Integer.parseInt(data.get("courseId"));
        if(data.get("top").equals("0")){
            top = "1";
        }else {
            top = "0";
        }
        int index = scService.CTop(id,CourseId,top);
        if(index==1) {
            if(top.equals("1")) {
                return "置顶成功";
            }else {
                return "已取消置顶";
            }
        }else{
            return "失败";
        }
    }

    /**
     * 学生加入课程
     * */
    @RequestMapping(value = "/courseJoin")
    public String joinCourse(@RequestBody Map<String,String> data){
        String courseKey = data.get("key");
        int student = Integer.parseInt(data.get("id"));
        Course course = courseService.getCourse(courseKey);
        if(course != null) {
            Sc sc = scService.getSc(student, course.getId());
            if (sc != null) {
                return "此课程已加入";
            }else {
                Tc tc = tcService.getTc(course.getMaster(), course.getId());
                if (tc != null) {
                    if (tc.getPigeonhole().equals("1")) {
                        return "加课码已失效";
                    }
                }
            }
        }
        List<Sc> scStudent = scService.getListSc(student);
        if(course == null){
            return "加课码不存在";
        } else if(course.getDeleting().equals("1")){
            return "加课码已停用";
        }
        else {
            Sc sc = new Sc();
            sc.setStudent(student);
            sc.setCourse(course.getId());
            sc.setPigeonhole("0");
            sc.setTop("1");
            if(scStudent == null || scStudent.size()==0){
                sc.setDisplayNum(1);
            }else {
                sc.setDisplayNum(scStudent.size()+1);
            }
            int index = scService.joinCourse(sc);
            return "加课成功";
        }
    }

    /**
     * 学生退课
     * */
    @RequestMapping(value = "/courseOut")
    public String outCourse(@RequestBody Map<String,String> data) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        int classId = Integer.parseInt(data.get("courseId"));
        String password = data.get("password");
        int student = Integer.parseInt(data.get("id"));
        String account = TokenUtil.getUsername(data.get("token"));
        Student student1 = loginstudentService.existenceAccount(account);
        MDFive mdFive = new MDFive();
        if(student1.getPassword().equals(mdFive.EncoderByMd5(password))){
            int index = scService.deleteCourse(student,classId);
            if(index==1) {
                return "退课成功";
            }else {
                return "失败";
            }
        }else {
            return "密码错误";
        }


    }

    /**
     * 学生课程归档
     * */
    @RequestMapping(value = "/changePigeonhole")
    public String PigeonholeStudentCourse(@RequestBody Map<String,String> data){
        int course = Integer.parseInt(data.get("courseId"));
        int student = Integer.parseInt(data.get("id"));
        int pigeonhole = Integer.parseInt(data.get("pigeonhole"));

        if(pigeonhole == 0) {
            int index = scService.pigeonholeCourse(student,course,"1");
            if (index == 1) {
                return "归档成功";
            } else {
                return "失败";
            }
        }else {
            int index = scService.pigeonholeCourse(student,course,"0");
            if (index == 1) {
                return "取消归档";
            } else {
                return "失败";
            }
        }
    }

    @RequestMapping(value = "/teacherName")
    public String name(@RequestBody Map<String,String> data){
        int id = Integer.parseInt(data.get("id"));
        Teacher teacher = loginteacherService.teachersName(id);
        return teacher.getName();
    }


    /**
     * 学生课程排序
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/courseSort")
    public String SortNum(@RequestBody Map<String,Object> list){
        List<Object> scCourses = (List<Object>) list.get("list");
        for(int i = 0; i<scCourses.size(); i++){

            int course = (int) ((Map<String, Object>) scCourses.get(i)).get("courseId");
            int student = (int) ((Map<String, Object>) scCourses.get(i)).get("student");
            int index = scService.sortNum(course,student,i+1);
        }
        return "成功";
    }

    /**
     * 学生作业信息显示
     * */
    @RequestMapping(value = "/homeworkShow")
    public List<Homework> homeworkShow(@RequestBody Map<String,String> data) throws ParseException {
        int course = Integer.parseInt(data.get("courseId"));
        return homeworkService.getHomework(course);
    }
    /**
     * 学生交作业作业
     * */
    @RequestMapping(value = "/homeworkSubmit")
    public String HandInHomework(@RequestBody Map<String,String> data) throws ParseException {
        String account = TokenUtil.getUsername(data.get("token"));
        Student student = loginstudentService.existenceAccount(account);
        int studentId = student.getId();
        int homework = Integer.parseInt(data.get("homeworkId"));
        Sw sw2 = swService.getHomework(studentId,homework);
        Sw sw = new Sw();
        sw.setStudent(studentId);
        sw.setHomework(homework);
        sw.setStatus("0");
        sw.setAnswer(data.get("answer"));
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");//设置日期格式
        sw.setTime(df.format(new Date()));
        sw.setReview("0");
        if(sw2==null) {
            swService.handIn(sw);
            return "提交成功";
        }else {
            sw.setId(sw2.getId());
            swService.update(sw);
            return "修改成功";
        }
    }
}
