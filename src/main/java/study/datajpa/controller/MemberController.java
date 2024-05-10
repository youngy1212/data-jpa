package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> List(@PageableDefault(size = 5,sort = "username") Pageable pageable){ //Pageable 구현체인데, springBootData가 자동세팅해줌
        Page<MemberDto> map = memberRepository.findAll(pageable).map(MemberDto::new);
        return map;    //Dto로 내보내야함 람다로 memberDto new 호출하여 변환
    }

    @PostConstruct
    public void init(){
        for(int i = 0; i<100; i++){
            memberRepository.save(new Member("user"+i,i));
        }
    }

}
