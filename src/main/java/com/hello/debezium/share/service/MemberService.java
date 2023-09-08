package com.hello.debezium.share.service;

import com.hello.debezium.share.entity.Member;
import com.hello.debezium.share.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public String register(String name, int age) {
        Member save = memberRepository.save(new Member(name, age));
        return save.getId();
    }

    @Transactional
    public String modify(Member member) {
        Member save = memberRepository.save(member);
        return save.getId();
    }

    @Transactional
    public String remove(String id) {
        memberRepository.deleteById(id);
        return id;
    }
}
