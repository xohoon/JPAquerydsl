package xohoon.study.JPAquerydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xohoon.study.JPAquerydsl.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom{
    List<Member> findByUsername(String username);
}

