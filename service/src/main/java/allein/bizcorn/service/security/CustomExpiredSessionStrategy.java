package allein.bizcorn.service.security;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.model.output.Result;
import com.alibaba.fastjson.JSON;
import org.springframework.security.web.session.SessionInformationExpiredEvent;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomExpiredSessionStrategy implements org.springframework.security.web.session.SessionInformationExpiredStrategy {
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException, IOException {
        HttpServletResponse response=event.getResponse();
        if (!response.isCommitted()) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            Result error;
            error=Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
            response.getWriter().print(JSON.toJSONString(error));
            response.getWriter().flush();
        }
    }
}
