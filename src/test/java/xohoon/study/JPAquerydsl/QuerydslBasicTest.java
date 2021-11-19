package xohoon.study.JPAquerydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import xohoon.study.JPAquerydsl.entity.Member;
import xohoon.study.JPAquerydsl.entity.QMember;
import xohoon.study.JPAquerydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static xohoon.study.JPAquerydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamA);
        Member member4 = new Member("member4", 40, teamB);
        Member member5 = new Member("member5", 50, teamB);
        Member member6 = new Member("member6", 60, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.persist(member5);
        em.persist(member6);
    }

    @Test
    public void startJPAL() {
        String sql = "select m from Member m where m.username = :username";
        Member findMember = em.createQuery(sql, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test // runtime 오류 발생 x -> compile 오
    public void startQuerydsl() {
//        QMember m = new QMember("m"); -> 방법1 같은 테이블을 조인할때만 사용 나머지는 왠만하면 방법3
//        QMember m1 = QMember.member; -> 방법2

        Member findMember = queryFactory
                .select(member) // -> 방법3(static import)
                .from(member)
                .where(member.username.eq("member1")) // parameter binding 처리
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() { // and 1
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1").and(member.age.eq(10)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam() { // and 2(선호),, 중간에 null이 들어가면 무시.. 동적쿼리에 유리
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void resultFetch() {
        // list 조회
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        // 단건조회
        Member fetchOne = queryFactory
                .selectFrom(member)
                .fetchOne();

        // 처음 한 건 조회
        Member fetchFirst = queryFactory
                .selectFrom(member)
                .fetchFirst();

        // 페이징 사용
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();

        results.getTotal();
        List<Member> content = results.getResults();

        // count 쿼리로 변경
        long total = queryFactory
                .selectFrom(member)
                .fetchCount();
    }
}
