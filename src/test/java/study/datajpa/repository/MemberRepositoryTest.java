package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    //추가한 repository를 사용하면 됨.
    @Autowired MemberQueryRepository memberQueryRepository;

    @Test
    public void testMember(){

        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);//JPA 자동제공

        Member findMember = memberRepository.findById(savedMember.getId()).get();
        //널이면 null exception


        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }


    @Test
    public void basicCRUD(){

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);
        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);


    }

    @Test
    public void findByUsernameAndAgeGreaterThan(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void findHelloBy(){
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }


    @Test
    public void testNamedQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for(String s : usernameList){
            System.out.println("s = "+s); //원래는 assertThat 비교해야함
        }
    }


    @Test
    public void findMemberDto(){
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10,team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for(MemberDto dto : memberDto){
            System.out.println("dto = "+dto); //원래는 assertThat 비교해야함
        }
    }

    @Test
    public void findByNames(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        memberRepository.findByNames(Arrays.asList("AAA","BBB"));

    }

    @Test
    public void returnType(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");
        Member aaa1 = memberRepository.findMemberByUsername("AAA");
        Optional<Member> aaa2 = memberRepository.findOptionalByUsername("AAA");

        //AAA가 값이 없다면 aaa는 null일까?
        //빈값 컬렉션을 반환해줌

        //aaa1 는 단건이라 null

    }

    @Test
    public void paging(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));//0페이지부터 3페이지씩
        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //API 반환할때, -> 그냥 반환하며 안되고 DTO로 변환 반환해야함. (이렇게 변환해서 반환하면 괜찮음)
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then
        List<Member> content = page.getContent(); //자동 page 처리
        long totalElements = page.getTotalElements(); //카운트 쿼리

        assertThat(content.size()).isEqualTo(3); //불려온 갯수
        assertThat(page.getTotalElements()).isEqualTo(5); //전체갯수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체페이지
        assertThat(page.hasNext()).isTrue(); //다음페이지가 있나
        assertThat(page.isFirst()).isTrue(); //처음페이지인지

        //when
//        Slice<Member> SlicePage  = memberRepository.findByAge(age,pageRequest);
//
//        //then
//        List<Member> SliceContent = SlicePage.getContent();
//
//        assertThat(SliceContent.size()).isEqualTo(3);
//        assertThat(SlicePage.getNumber()).isEqualTo(0); //페이지 번호
//        assertThat(SlicePage.hasNext()).isTrue(); //다음페이지가 있나
//        assertThat(SlicePage.isFirst()).isTrue(); //처음페이지인지


    }

    @Test
    public void bulkUpdate(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush();
//        em.clear(); //이렇게 flush, clear 하면 아래서 DB에서 다시 조회해옴!!

        Member member5 = memberRepository.findMemberByUsername("member5");
        //여기서 member5는 40살일까? 41살일까 -> 40살 but
        //하지만 DB에서는 41살임 -> 벌크연산을 이미 넣었음.


        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy(){

        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));
        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();

        //패치조인 JPA로 사용
        //List<Member> members = memberRepository.findMemberFetchJoin();

        //then
        //select Team
        for (Member member : members) { //이떄 조회가됨 (지연로딩) n+1문제
            member.getTeam().getName();
        }
    }

    @Test
    public void queryHint(){

        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");
        //아나는 변경하려는게 아니고, DB에서 조회만 하고 끝날꺼야
        //하지만 더티체킹 하는동안은 원본 + 비교본 해서 데이터를 사용험.
        //변경이 안된다고 가정하고, 스냅샷을 만들지 않음.

        em.flush();;

    }

    @Test
    public void lock(){

        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");

    }

    @Test
    public void callCustom(){
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void queryByExample(){

        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        memberRepository.findByUsername("m1"); //정적일떄 사용

        //Probe
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team); //연관관계 AND 세팅

        ExampleMatcher maucher = ExampleMatcher.matching().withIgnorePaths("age");
        //나는 age라는 속성이 있다면 무시할꺼야.

        Example<Member> example = Example.of(member,maucher); //저장하고 그런게 아니라 m1이라는 객체를 찾고싶어
        List<Member> result = memberRepository.findAll(example); //만들어진 m1도메인 객체를 검색

        assertThat(result.get(0).getUsername()).isEqualTo("m1");

    }

    @Test
    public void projections(){

        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);
        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when m1,m2 이름만 뽑고싶어
        List<NestedClosedProjection> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjection.class);

        for(NestedClosedProjection nestedClosedProjection : result){
            System.out.println("nestedClosedProjection = " + nestedClosedProjection);
        }

    }



}