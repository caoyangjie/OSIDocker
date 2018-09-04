/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.model;

import java.io.Serializable;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 18:18 2018/9/3
 * @修改说明：
 * @修改日期： 18:18 2018/9/3
 * @版本号： V1.0.0
 */
public class UserEducational implements Serializable {

    private String userId;
    private String flowNo;
    //出生日期
    private String birthDate;
    //层次
    private String branchCollege;
    //证书编号
    private String certificateNum;
    //入学时间
    private String enrollmentDate;
    //毕业时间
    private String graduationDate;
    //毕业
    private String graduationOrCompletion;
    //学习形式
    private String instructionalMode;
    //学制
    private String lengthOfSchooling;
    //形么
    private String name;
    //头像
    private String photo;
    //头像名称
    private String photoName;
    //校长姓名
    private String principalName;
    //专业
    private String profession;
    //学历类别
    private String qualificationType;
    //学校名称
    private String schoolName;
    //性别
    private String sex;

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBranchCollege() {
        return branchCollege;
    }

    public void setBranchCollege(String branchCollege) {
        this.branchCollege = branchCollege;
    }

    public String getCertificateNum() {
        return certificateNum;
    }

    public void setCertificateNum(String certificateNum) {
        this.certificateNum = certificateNum;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getGraduationDate() {
        return graduationDate;
    }

    public void setGraduationDate(String graduationDate) {
        this.graduationDate = graduationDate;
    }

    public String getGraduationOrCompletion() {
        return graduationOrCompletion;
    }

    public void setGraduationOrCompletion(String graduationOrCompletion) {
        this.graduationOrCompletion = graduationOrCompletion;
    }

    public String getInstructionalMode() {
        return instructionalMode;
    }

    public void setInstructionalMode(String instructionalMode) {
        this.instructionalMode = instructionalMode;
    }

    public String getLengthOfSchooling() {
        return lengthOfSchooling;
    }

    public void setLengthOfSchooling(String lengthOfSchooling) {
        this.lengthOfSchooling = lengthOfSchooling;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(String qualificationType) {
        this.qualificationType = qualificationType;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }
}
