package com.hello.debezium.share.repository;

import com.hello.debezium.share.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
