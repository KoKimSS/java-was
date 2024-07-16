package codesquad.business.controller;

import codesquad.business.domain.Member;
import codesquad.was.exception.BadRequestException;
import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.exception.InternalServerException;
import codesquad.was.handler.Handler;
import codesquad.business.service.MemberService;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SingUpHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(SingUpHandler.class);
    private final MemberService memberService;

    // 싱글톤 으로 구현
    public static SingUpHandler singUpHandler = new SingUpHandler(MemberService.memberService);

    private SingUpHandler(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public HttpResponse handlePOSTRequest(HttpRequest request) throws InternalServerException, BadRequestException {
        HttpResponse response = new HttpResponse();
        registration(request, response);
        return response;
    }

    public void registration(HttpRequest request, HttpResponse response) throws InternalServerException, BadRequestException {
        // todo : request 에 대한 validation 을 처리하는 기능 구현
        String userId = null;
        String username = null;
        String password = null;
        try {
            // 세개 중 하나라도 없으면 회원가입 로직 skip
            userId = request.getParameter("userId");
            username = request.getParameter("name");
            password = request.getParameter("password");
        } catch (Exception e) {
            throw new BadRequestException("잘못된 요청 정보입니다.");
        }

        try {
            Member member = Member.factoryMethod(userId, username, password);
            memberService.save(member);
            // 처리 완료 후 리디렉션
            HttpResponse.setRedirect(response, HttpStatusCode.FOUND, "/index.html");
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}


