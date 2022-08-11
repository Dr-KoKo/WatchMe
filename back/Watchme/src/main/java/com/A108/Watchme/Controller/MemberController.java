package com.A108.Watchme.Controller;

import com.A108.Watchme.DTO.*;
import com.A108.Watchme.Http.ApiResponse;
import com.A108.Watchme.Repository.MemberRepository;
import com.A108.Watchme.Repository.RefreshTokenRepository;
import com.A108.Watchme.Service.MemberService;
import com.A108.Watchme.Service.S3Uploader;
import com.A108.Watchme.VO.Entity.member.Member;
import com.A108.Watchme.utils.CookieUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.Optional;

@RestController
//@RequestMapping("/members")
public class MemberController {
    @Autowired
    private MemberService memberService;
    private final static String REFRESH_TOKEN = "refresh_token";
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private S3Uploader s3Uploader;

    @PostMapping(value="/auth/signup", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseBody
    public ApiResponse signUp( @RequestPart(value = "data") SignUpRequestDTO signUpRequestDTO,@RequestPart(value = "files",required = false) MultipartFile images) throws ParseException {
        String url="https://popoimages.s3.ap-northeast-2.amazonaws.com/Watchme/user.png";
        try{
            url = s3Uploader.upload(images, "Watchme");
        } catch (Exception e){
            e.printStackTrace();
        }
        return memberService.memberInsert(signUpRequestDTO, url);
    }
    @PostMapping("/login")
    @ResponseBody
    public ApiResponse login(@RequestBody @Validated LoginRequestDTO loginRequestDTO, HttpServletResponse response, HttpServletRequest request){

        ApiResponse apiResponse = memberService.login(request,response, loginRequestDTO);
        System.out.println(apiResponse.getResponseData());
        return apiResponse;
    }
    @PostMapping("/auth/logout")
    @ResponseBody
    public ApiResponse logout(HttpServletRequest request,
                              HttpServletResponse response, Authentication authentication){
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("LOGOUT SUCCESS");
        apiResponse.setCode(200);

        return apiResponse;
    }

    @PostMapping("/auth/social-signup")
    @ResponseBody
    public ApiResponse socialSignUp(@RequestBody SocialSignUpRequestDTO socialSignUpRequestDTO, HttpServletRequest request,
                                    HttpServletResponse response, Authentication authentication) throws ParseException {
        return memberService.memberInsert(socialSignUpRequestDTO, request, response ,authentication);
    }

    @PostMapping("/auth/find-email")
    @ResponseBody
    public ApiResponse findEmail(@RequestBody FindEmailRequestDTO findEmailRequestDTO){
        System.out.println(findEmailRequestDTO.getNickName());
        ApiResponse result = memberService.findEmail(findEmailRequestDTO);
        return result;
    }


    @GetMapping(value = "/members/mygroup")
    @ResponseBody
    public ApiResponse memberGroup() {
        return memberService.memberGroup();
    }

    @PostMapping("/find-password")
    public ApiResponse findPW(@RequestBody FindPwDTO findPwDTO) {
        return memberService.findPW(findPwDTO);
    }

    @PostMapping("/reset-password")
    public ApiResponse resetPW(@RequestBody ResetPwDTO resetPwDTO){
        return memberService.resetPW(resetPwDTO);
    }

    @PostMapping("/reset-password-mainpages")
    public ApiResponse resetPwMp(@RequestBody ResetPwMpDTO resetPwMpDTO){
        return memberService.resetPwMp(resetPwMpDTO);
    }

    @PostMapping(value="/update", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseBody
    public ApiResponse memberUpdate(@RequestPart(value = "data") UpdateRequestDTO updateRequestDTO, @RequestPart(value = "files", required = false) MultipartFile image) throws ParseException {
        // 프로필 이미지 수정시 삭제?
        return memberService.memberUpdate(updateRequestDTO, image);
    }

    @PostMapping("/emails-check")
    @ResponseBody
    public ApiResponse emailCheck(@RequestBody CheckEmailDTO checkEmailDTO){
        return memberService.emailCheck(checkEmailDTO);
    }

    @PostMapping("/nickName-check")
    @ResponseBody
    public ApiResponse nickNameCheck(@RequestBody CheckNickNameDTO checkNickNameDTO){
        return memberService.nickNameCheck(checkNickNameDTO);

    }

    @GetMapping("/members")
    @ResponseBody
    public ApiResponse memberInfo(HttpServletResponse response){
        ApiResponse apiResponse;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication.getPrincipal().equals("anonymousUser")){
            apiResponse = new ApiResponse();
            apiResponse.setCode(400);
            apiResponse.setMessage("NOT LOGIN");
            return apiResponse;
        }

        // 일반 로그인의 경우
        Long currUserId = Long.parseLong(((UserDetails)authentication.getPrincipal()).getUsername());
        Optional<Member> checkCurrUser = memberRepository.findById(currUserId);

        if(checkCurrUser.isPresent()){
            apiResponse = memberService.getMyPage(checkCurrUser.get(), response);
        } else{
            apiResponse = new ApiResponse();
            apiResponse.setCode(400);
            apiResponse.setMessage("NO SUCH USER");
        }

        return apiResponse;

    }

}
