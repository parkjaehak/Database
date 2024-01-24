package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import java.sql.Connection;
import java.sql.SQLException;

/**
 * transaction - TransactionManager
 */

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    private final PlatformTransactionManager transactionManager2;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money){
        // transaction start
        TransactionStatus status = transactionManager2.getTransaction(new DefaultTransactionDefinition());

        try {
            bizLogic(fromId, toId, money); // business logic
            transactionManager2.commit(status); // 성공시 commit

        } catch (Exception e) {
            transactionManager2.rollback(status); // 실패시 rollback
            throw new IllegalStateException(e);
        }
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money); // 돈을 주는 쪽
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money); // 돈을 받는 쪽
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
