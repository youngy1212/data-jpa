package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findHelloBy(); //멤버 전체조회 by뒤에 없음

    List<Member> findTop3HelloBy();

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username")String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String name); //컬렉션
    Member findMemberByUsername(String name); //단건
    Optional<Member> findOptionalByUsername(String name); //단건 Optional

    //Page<Member> findByAge(int age, Pageable pageable);

    //이름생성시 막 만들면 오류남..findByAge2 했다가 오류
    //Slice<Member> findByAge(int age, Pageable pageable);

    //countQuery 분리가능
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)//update 어노테이션이 꼭 들어가야함 //clear 과정 자동
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);


    //JPA 페치조인을 이용
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //그럼 Data JPA로는?
    //공통 메서드 오버라이드
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //JPQL과 엔티티그래프를 섞어서 사용도 가능
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //메서드 이름으로 쿼리에서 특히 편리하다.
    @EntityGraph(attributePaths = {"team"})
    List<Member> findByUsername(String username);

    //Hint
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    //함부로 손대지 말라고 Rock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    //List<UsernameOnly> findProjectionsByUsername(String username);

   <T> List<T> findProjectionsByUsername(String username, Class<T> type );

   @Query(value = "SELECT m.member_id as id,  m.username, t.name as teamName "+
          "FROM member m left join team t ON m.team_id = t.team_id" ,
           countQuery = "SELECT count(*) from member",nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);


   //카운트 쿼리는 꼭 따로 짜야됨 (네이티브 쿼리)
}
