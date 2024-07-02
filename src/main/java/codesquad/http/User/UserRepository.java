package codesquad.http.User;

import codesquad.http.log.Log;
import codesquad.http.repository.MemoryRepository;

import static codesquad.http.repository.MemoryRepository.*;

public class UserRepository {

    public void save(User user) {
        if(isExistUser(user.getUserId())){
            //todo : 예외처리로 변경
            Log.log("이미 저장 된 유저입니다");
            return;
        }
        put("User"+user.getUserId(),user);
    }

    private static boolean isExistUser(String userId) {
        return containsKey("User" + userId);
    }

    public User get(String userId) {
        return (User) MemoryRepository.get("User"+userId).orElseThrow(IllegalArgumentException::new);
    }
}
