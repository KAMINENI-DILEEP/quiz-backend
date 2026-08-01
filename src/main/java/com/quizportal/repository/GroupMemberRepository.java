package com.quizportal.repository;

import com.quizportal.entity.GroupMember;
import com.quizportal.entity.StudentGroup;
import com.quizportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepository
        extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByGroup(StudentGroup group);

    List<GroupMember> findByUser(User user);

    boolean existsByGroupAndUser(StudentGroup group, User user);

    void deleteByGroupAndUser(StudentGroup group, User user);
}