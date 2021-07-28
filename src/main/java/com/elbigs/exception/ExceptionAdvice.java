package com.elbigs.exception;

import com.elbigs.dto.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

//    private final ResponseService responseService;

    private final MessageSource messageSource;

    //
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResponseDto defaultException(HttpServletRequest request, Exception e) {
        CommonResponseDto res = new CommonResponseDto();
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("msg", getMessage("unKnown.msg"));
        m.put("code", Integer.valueOf(getMessage("unKnown.code")));
        res.setErrors(m);

        e.printStackTrace();
        ;
        return res;
    }

    //
    @ExceptionHandler(CUserNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected CommonResponseDto userNotFound(HttpServletRequest request, CUserNotFoundException e) {
        CommonResponseDto res = new CommonResponseDto();
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("msg", getMessage("userNotFound.msg"));
        m.put("code", Integer.valueOf(getMessage("userNotFound.code")));
        res.setErrors(m);
        return res;
    }

    @ExceptionHandler(LoginFailedException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResponseDto loginFailed(HttpServletRequest request, LoginFailedException e) {

        CommonResponseDto res = new CommonResponseDto();
        Map<String, Object> m = new HashMap<String, Object>();
        String[] arr = {getMessage("emailSigninFailed.msg")};
        m.put("user_id", arr);
        res.setErrors(m);

        return res;

    }

    @ExceptionHandler(CAuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponseDto authenticationEntryPointException(HttpServletRequest request, CAuthenticationEntryPointException e) {
        CommonResponseDto res = new CommonResponseDto();
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("msg", getMessage("entryPointException.msg"));
        m.put("code", Integer.valueOf(getMessage("entryPointException.code")));
        res.setErrors(m);
        return res;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CommonResponseDto accessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        CommonResponseDto res = new CommonResponseDto();
        Map<String, Object> m = new HashMap<String, Object>();
        String[] arr = {getMessage("accessDenied.msg")};
        m.put("user_id", arr);
        res.setErrors(m);
        return res;
    }
////
//    @ExceptionHandler(CCommunicationException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public CommonResult communicationException(HttpServletRequest request, CCommunicationException e) {
//        return responseService.getFailResult(Integer.valueOf(getMessage("communicationError.code")), getMessage("communicationError.msg"));
//    }
//
//    @ExceptionHandler(CUserExistException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public CommonResult communicationException(HttpServletRequest request, CUserExistException e) {
//        return responseService.getFailResult(Integer.valueOf(getMessage("existingUser.code")), getMessage("existingUser.msg"));
//    }
//
//    @ExceptionHandler(CNotOwnerException.class)
//    @ResponseStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
//    public CommonResult notOwnerException(HttpServletRequest request, CNotOwnerException e) {
//        return responseService.getFailResult(Integer.valueOf(getMessage("notOwner.code")), getMessage("notOwner.msg"));
//    }
//
//    @ExceptionHandler(CResourceNotExistException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public CommonResult resourceNotExistException(HttpServletRequest request, CResourceNotExistException e) {
//        return responseService.getFailResult(Integer.valueOf(getMessage("resourceNotExist.code")), getMessage("resourceNotExist.msg"));
//    }
//
//    @ExceptionHandler(CForbiddenWordException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public CommonResult forbiddenWordException(HttpServletRequest request, CForbiddenWordException e) {
//        return responseService.getFailResult(Integer.valueOf(getMessage("forbiddenWord.code")), getMessage("forbiddenWord.msg", new Object[]{e.getMessage()}));
//    }

    // code정보에 해당하는 메시지를 조회합니다.
    private String getMessage(String code) {
        return getMessage(code, null);
    }

    // code정보, 추가 argument로 현재 locale에 맞는 메시지를 조회합니다.
    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
