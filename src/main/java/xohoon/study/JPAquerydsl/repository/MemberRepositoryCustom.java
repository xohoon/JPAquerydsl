package xohoon.study.JPAquerydsl.repository;

import xohoon.study.JPAquerydsl.dto.MemberSearchCondition;
import xohoon.study.JPAquerydsl.dto.MemberTeamDto;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSearchCondition condition);
}
