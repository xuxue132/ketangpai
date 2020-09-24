package com.example.classroomschool.Controller.publics;

import com.example.classroomschool.Controller.Token.TokenUtil;
import com.example.classroomschool.Controller.encryption.MDFive;
import com.example.classroomschool.Model.*;
import com.example.classroomschool.Service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController
@RequestMapping("/api/start")
public class PublicController {
    @Resource
    LoginteacherService teacherService;
    @Resource
    LoginstudentService studentService;
    @Resource
    ScService scService;
    @Resource
    PictureService pictureService;
    @Resource
    TcService tcService;
    @Resource
    CourseService courseService;
    @Resource
    HomeworkService homeworkService;
    @Resource
    TeacherPlusService teacherPlusService;
    @Resource
    StudentPlusService studentPlusService;

    /**
     * 获取账号信息
     * */
    @RequestMapping(value = "/user")
    public Object  getUser(@RequestBody Map<String,String> token) throws JsonProcessingException {
        String account = TokenUtil.getUsername(token.get("token"));
        Object student = studentService.existenceAccount(account);
        Object teacher = teacherService.existenceAccount(account);
        HashMap<String, Object> hs = new HashMap<>();
        if(student==null) {
            hs.put("user", teacher);
        }else {
            hs.put("user",student);
        }
        Picture picture = pictureService.getAddress(account);
        hs.put("address",picture.getAddress());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 登录
     * */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String loginStudent(@RequestBody Map<String,String> date) throws JsonProcessingException, UnsupportedEncodingException, NoSuchAlgorithmException {

        Student student = studentService.SignIn(date.get("account"));
        Teacher teacher = teacherService.SignIn(date.get("account"));

        if(student==null && teacher == null){
            return "0";                                                 //用户不存在
        }else {
            if(student != null) {
                MDFive mdFive = new MDFive();
                if (!student.getPassword().equals(mdFive.EncoderByMd5(date.get("password")))) {
                    return "1";                                         //密码错误
                } else {
                    String token = TokenUtil.sign("ROLE_student", student.getAccount());
                    HashMap<String, Object> hs = new HashMap<>();
                    if (TokenUtil.verify(token)) {
                        hs.put("code", 200);
                    } else {
                        hs.put("code", 500);
                    }
                    hs.put("role", 0);
                    hs.put("token", token);
                    String account = TokenUtil.getUsername(token);
                    Student stu= studentService.existenceAccount(account);
                    hs.put("user",stu);
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.writeValueAsString(hs);
                }
            }else {
                MDFive mdFive = new MDFive();
                if (!teacher.getPassword().equals(mdFive.EncoderByMd5(date.get("password")))) {
                    return "1";
                } else {
                    String token = TokenUtil.sign("ROLE_teacher", teacher.getAccount());
                    HashMap<String, Object> hs = new HashMap<>();
                    if (TokenUtil.verify(token)) {
                        hs.put("code", 200);
                    } else {
                        hs.put("code", 500);
                    }
                    hs.put("role", 1);
                    hs.put("token", token);
                    String account = TokenUtil.getUsername(token);
                    Student stu= studentService.existenceAccount(account);
                    hs.put("user",stu);
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.writeValueAsString(hs);
                }
            }
        }
    }

    /**
     * 学生注册
     * */
    @RequestMapping(value = "/register/student")
    public String addstudent(@RequestBody Map<String,String> date) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String account;
        String mailbox = null;
        String phone = null;
        Student student = new Student();
        MDFive mdFive = new MDFive();
        if(!mdFive.EncoderByMd5(date.get("answer")).equals(date.get("answerPlus"))){
            return "验证码错误";
        }
        if(date.get("account").contains("@")){
            mailbox = date.get("account");
            student.setMailbox(mailbox);
        }else {
            phone = date.get("account");
            student.setPhone(phone);
        }
        Student studentMailbox = studentService.existenceMailbox(mailbox);
        Student studentPhone = studentService.existencePhone(phone);
        Teacher teacherMailbox = teacherService.existenceMailbox(mailbox);
        Teacher teacherPhone = teacherService.existencePhone(phone);
        if(studentMailbox!=null || teacherMailbox!=null) {
            return "邮箱已存在";                                                         //邮箱存在
        }else if(studentPhone!=null || teacherPhone!=null){
            return "电话已存在";                                                         //电话存在
        }
        else {
            while (true){
                account ="s" + String.valueOf((int)((Math.random()*9+1)*100000));
                if(studentService.existenceAccount(account) == null){
                    student.setAccount(account);
                    break;
                }
            }
            student.setPassword(mdFive.EncoderByMd5(date.get("password")));
            student.setName(date.get("name"));
            student.setSchool(date.get("school"));
            student.setSchoolNum(date.get("schoolNum"));
            int as=studentService.insertUser(student);

            int value = (int) (Math.random() * 31 + 10);
            String address = "https://www.ketangpai.com/Public/Common/img/40/"+value+".png";
            Picture picture = new Picture();
            picture.setPicid(account);
            picture.setAddress(address);
            int index = pictureService.inserPic(picture);
            return "注册成功";
        }
    }

    /**
     * 老师注册
     * */
    @RequestMapping(value = "/register/teacher")
    public String addTeacher(@RequestBody Map<String,String> date) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String account;
        String mailbox = null;
        String phone = null;
        Teacher teacher = new Teacher();
        MDFive mdFive = new MDFive();
        if(!mdFive.EncoderByMd5(date.get("answer")).equals(date.get("answerPlus"))){
            return "验证码错误";
        }
        if(date.get("account").contains("@")){
            mailbox = date.get("account");
            teacher.setMailbox(mailbox);
        }else {
            phone = date.get("account");
            teacher.setPhone(phone);
        }

        Student studentMailbox = studentService.existenceMailbox(mailbox);
        Student studentPhone = studentService.existencePhone(phone);
        Teacher teacherMailbox = teacherService.existenceMailbox(mailbox);
        Teacher teacherPhone = teacherService.existencePhone(phone);
        if(studentMailbox!=null || teacherMailbox!=null) {
            return "邮箱已存在";
        }else if(studentPhone!=null || teacherPhone!=null){
            return "电话已存在";
        }
        else {
            while (true){
                account ="t" + String.valueOf((int)((Math.random()*9+1)*100000));
                if(teacherService.existenceAccount(account) == null){
                    teacher.setAccount(account);
                    break;
                }
            }
            teacher.setName(date.get("name"));
            teacher.setSchool(date.get("school"));
            teacher.setPassword(mdFive.EncoderByMd5(date.get("password")));
            int as=teacherService.insertUser(teacher);

            //添加头像图片
            int value = (int) (Math.random() * 31 + 10);
            String address = "https://www.ketangpai.com/Public/Common/img/40/"+value+".png";
            Picture picture = new Picture();
            picture.setPicid(account);
            picture.setAddress(address);
            int index = pictureService.inserPic(picture);
            return "注册成功";
        }
    }

    /**
     * 验证码
     * */
    @RequestMapping(value = "/validCode")
    public String checkNumber() throws ScriptException, UnsupportedEncodingException, NoSuchAlgorithmException, JsonProcessingException {
        String number = "123456789";
        String icon = "加减乘";
        String icon2 = "+-*";
        String equation = "";
        int result;
        ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
        while (true) {
            int value = (int) (Math.random() * 9 + 0);
            int value2 = (int) (Math.random() * 9 + 0);
            int value3 = (int) (Math.random() * 3 + 0);
            equation = number.charAt(value) + "" + icon.charAt(value3) + "" + number.charAt(value2) + "=";
            result = (int) jse.eval(Integer.parseInt(String.valueOf(number.charAt(value))) + "" + icon2.charAt(value3)
                    + "" + Integer.parseInt(String.valueOf(number.charAt(value2))));
            if (result > 0) {
                break;
            }

        }
        HashMap<String,String> hs = new HashMap<>();
        hs.put("code",equation);
        MDFive mdFive = new MDFive();
        hs.put("answerPlus",mdFive.EncoderByMd5(result+""));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(hs);
    }


    /**
     * 学生人数
     * */
    @RequestMapping(value = "/studentNum")
    public int number(@RequestBody Map<String,String> data){
        int course = Integer.parseInt(data.get("courseId"));
        List<Sc> sc = scService.getNumber(course);
        System.out.println(course+"  "+sc.size());
        return sc.size();
    }

    /**
     * 老师课程所有信息
     * */
    @RequestMapping(value = "/courseInfo")
    public Object CourseInfo(@RequestBody Map<String,String> data) throws JsonProcessingException {
//        System.out.println(data.get("courseId"));
        int courseId = Integer.parseInt(data.get("courseId"));
        HashMap<String,Object> hs = new HashMap<>();
        Course course = courseService.getCourseById(courseId);
        List<Homework> homework = homeworkService.getHomework(courseId);
        List<Tc> tc = tcService.getTcByCourse(courseId);
        Collections.reverse(tc);
        List<TeacherPlus> teachers = new ArrayList<TeacherPlus>();
        List<TeacherPlus> teachersStudent = new ArrayList<TeacherPlus>();
        for (Tc value : tc) {
            if(!value.getRole().equals("学生")) {
                teachers.add(teacherPlusService.getTeacher(value.getTeacher(),value.getCourse()));
            }else {
                teachersStudent.add(teacherPlusService.getTeacher(value.getTeacher(),value.getCourse()));
            }
        }

        List<Sc> sc = scService.getNumber(courseId);
        List<Object> students = new ArrayList<Object>();

        for (Sc value : sc) {
            students.add(studentPlusService.getStudent(value.getStudent()));
        }
        for(TeacherPlus value : teachersStudent){
            students.add(value);
        }
        hs.put("course",course);
        hs.put("homework",homework);
        hs.put("teacher",teachers);
        hs.put("tc",tc);
        hs.put("student",students);
        hs.put("address",pictureService.getAddress(courseId+"").getAddress().replace("min","big"));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(hs);
    }


}
