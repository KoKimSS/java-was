package codesquad;

import codesquad.business.domain.Member;
import codesquad.business.service.MemberService;
import codesquad.was.server.Server;
import java.io.IOException;



public class Main {

    public static void main(String[] args) throws IOException {
        Server server = new Server(10,8080, 10);

        Member test = Member.factoryMethod("seungsu", "승수", "123123");
        Long saved = MemberService.memberService.save(test);


        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
