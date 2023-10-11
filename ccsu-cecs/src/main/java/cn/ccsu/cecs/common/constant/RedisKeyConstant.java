package cn.ccsu.cecs.common.constant;

/**
 * 聚合所有Redis的key
 */
public class RedisKeyConstant {

    /**
     * 学生加分申请表-查询全部-分学号-key
     * <p>
     * key: stu:bonus_apply:list:[学生id]
     * {
     * [学年id]:[加分申请表信息]
     * }
     * 这个redis的key采用hash做
     */
    public final static String STUDENT_BONUS_APPLY_LIST_SINGLE_KEY = "stu:bonus_apply:list";

    /**
     * 学生加分申请表-单个申请表-分学号-key
     */
    public final static String STUDENT_BONUS_APPLY_ONE_SINGLE_KEY = "stu:bonus_apply:id";

    /**
     * 加分项名称-分类别id-key
     * 类型：String
     */
    public final static String STUDENT_BONUS_NAME_LIST_SINGLE_KEY = "stu:bonus:name:category_id";

    /**
     * 加分项信息-分类别id-分名称-key
     * 类型：String
     */
    public final static String STUDENT_BONUS_INFO_ONE_SINGLE_KEY = "stu:bonus:name:category_id_name";

    /**
     * 学生成绩-按学年-分学号-key
     * 类型：String
     */
    public final static String STUDENT_SCORE_PERSON_BY_YEAR_SINGLE_KEY = "stu:score:person:year_id";

    /**
     * 学生成绩-按学年-按班级-按分页页面、页码-分学号-key
     * key:stu:score:year_class
     * {
     * [学年id]_[班级id]_[页码]_[条数]   xxx
     * }
     * 这个redis的key采用hash做
     */
    public final static String STUDENT_SCORE_YEAR_CLASS_LIST_KEY = "stu:score:year_class";

    /**
     * 学生成绩-按学年-按年级-按分页页面、页码-分学号-key
     * key:stu:score:year_profession
     * {
     * [学年id]_[专业id]_[页码]_[条数]   xxx
     * }
     * 这个redis的key采用hash做
     */
    public final static String STUDENT_SCORE_YEAR_PROFESSION_LIST_KEY = "stu:score:year_profession";

    /**
     * 老师-加分申请表-分学年-分年级-分专业-分审核状态-分页码-分limit-key
     * key: teacher:bonus_apply:year_grade_profession_approval:[学年id]_[年级id]_[专业id]_[审核状态]
     * {
     * [页码]_[条数] ：  xxx
     * }
     * 这个redis的key采用hash做
     */
    public final static String TEACHER_BONUS_APPLY_LIST_ALL_KEY = "teacher:bonus_apply:year_grade_profession_approval";

    /**
     * 老师-分数条数-分学年-分专业-分年级-key
     * key:teacher:score:rows:year_profession_grade
     * {
     * [学年id]_[专业id]_[年级id]  ： xxx
     * }
     * 这个redis的key采用hash做
     */
    public final static String TEACHER_SCORE_ROWS_KEY = "teacher:score:rows:year_profession_grade";

    /**
     * 学生-分数条数-分类别-分学年-分学生id-key
     * key:stu:score:rows:category_year_stu_id
     * {
     * [类别id]_[学年id]_[学生id]  ： xxx
     * }
     * 这个redis的key采用hash做
     */
    public final static String STUDENT_SCORE_ROWS_KEY = "stu:score:rows:category_year_stu_id";

    /**
     * 老师-分学年-分年级-分专业-分页-分码-key
     * key:  teacher:score:year_grade_profession
     * {
     * [学年id]_[年级id]_[专业id]_[页码]_[条数]  ：  xxx
     * }
     * 这个redis的key采用hash做
     */
    public final static String TEACHER_SCORE_ALL_LIST_KEY = "teacher:score:year_grade_profession";

    /**
     * 老师-加分项名称-分页-分码-key
     * key:  teacher:bonus:name:list
     * {
     * [页码]_[条数]  ： xxx
     * }
     * 这个redis的key采用hash做
     */
    public static final String TEACHER_BONUS_NAME_LIST_KEY = "teacher:bonus:name:list";

    /**
     * 学生-加分细节-分学年-key
     */
    public static final String STUDENT_SCORE_DETAILS_BY_YEAR_CATEGORY_LIST_KEY = "stu:score_details";

    /**
     * 学生-oosId-分加分申请表id-key
     */
    public static final String STUDENT_OOS_IMAGES_BY_APPLY_ID_LIST_KEY = "stu:oos:oos_images_id_list";

    /**
     * 学生信息-分学号-分学生id-key
     */
    public static final String STUDENT_STUDENT_INFO_SINGLE_KEY = "stu:student:info";

    /**
     * 学生加分申请表信息-学生id-学年id-hash
     */
    public static final String STUDENT_BONUS_APPLY_INFO_KEY = "stu:bonus_apply";

}
