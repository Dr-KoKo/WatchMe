package com.A108.Watchme.Service;

import com.A108.Watchme.Config.properties.AppProperties;
import com.A108.Watchme.DTO.*;
import com.A108.Watchme.DTO.myPage.MyData;
import com.A108.Watchme.DTO.myPage.MyGroup;
import com.A108.Watchme.DTO.myPage.WrapperMy;
import com.A108.Watchme.Exception.AuthenticationException;
import com.A108.Watchme.Http.ApiResponse;
import com.A108.Watchme.Repository.*;
import com.A108.Watchme.VO.ENUM.*;
import com.A108.Watchme.VO.Entity.MemberGroup;
import com.A108.Watchme.VO.Entity.group.Group;
import com.A108.Watchme.VO.Entity.log.MemberRoomLog;
import com.A108.Watchme.VO.Entity.member.Member;
import com.A108.Watchme.VO.Entity.member.MemberInfo;
import com.A108.Watchme.VO.Entity.RefreshToken;
import com.A108.Watchme.oauth.entity.UserPrincipal;
import com.A108.Watchme.oauth.token.AuthToken;
import com.A108.Watchme.oauth.token.AuthTokenProvider;
import com.A108.Watchme.utils.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final static long THREE_DAYS_MSEC = 259200000;
    private final static String REFRESH_TOKEN = "refresh_token";

    private final MemberRepository memberRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final MemberGroupRepository memberGroupRepository;
    private final MRLRepository mrlRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public ApiResponse memberInsert(SignUpRequestDTO signUpRequestDTO, String url) throws ParseException {
        ApiResponse result = new ApiResponse();
        String encPassword = bCryptPasswordEncoder.encode(signUpRequestDTO.getPassword());
        Member member = memberRepository.save(Member.builder()
                .email(signUpRequestDTO.getEmail())
                .nickName(signUpRequestDTO.getNickName())
                .role(Role.MEMBER)
                .pwd(encPassword)
                .status(Status.YES)
                .providerType(ProviderType.EMAIL)
                .build());

        memberInfoRepository.save(MemberInfo.builder()
                .member(member)
                .gender(signUpRequestDTO.getGender())
                .name(signUpRequestDTO.getName())
                .birth(signUpRequestDTO.getBirth())
                .point(0)
                .imageLink(url)
                .score(0)
                .build());

        result.setMessage("MEMBER INSERT SUCCESS");
        result.setResponseData("DATA", "Success");
        return result;
    }

    public ApiResponse login(HttpServletRequest request, HttpServletResponse response, LoginRequestDTO loginRequestDTO) {
        ApiResponse result = new ApiResponse();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Date now = new Date();
        Member member = memberRepository.findByEmail(loginRequestDTO.getEmail());

        AuthToken accessToken = tokenProvider.createAuthToken(member.getId(),
                ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode()
                , new Date(now.getTime() + appProperties.getAuth().getTokenExpiry()));

        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

        AuthToken refreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        Optional<RefreshToken> oldRefreshToken = refreshTokenRepository.findByEmail(member.getEmail());

        // 원래 RefreshToken이 있으면 갱신해줘야함
        if(oldRefreshToken.isPresent()){
            RefreshToken  token = refreshTokenRepository.findById(oldRefreshToken.get().getId()).get();
            token.setToken(refreshToken.getToken());
            refreshTokenRepository.save(token);
        }
        // 없으면 생성
        else{
            refreshTokenRepository.save(RefreshToken.builder()
                    .token(refreshToken.getToken())
                    .email(member.getEmail())
                    .build());
        }

        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        result.setCode(200);
        result.setMessage("LOGIN SUCCESS");
        result.setResponseData("accessToken", accessToken.getToken());
        return result;
    }
    public ApiResponse memberInsert(SocialSignUpRequestDTO socialSignUpRequestDTO, HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ParseException {
        ApiResponse result = new ApiResponse();

        String email = ((UserPrincipal) authentication.getPrincipal()).getName();
        Member member= memberRepository.findByEmail(email);

        Date now = new Date();
        memberInfoRepository.save(MemberInfo.builder()
                .member(member)
                .gender(socialSignUpRequestDTO.getGender())
                .name(socialSignUpRequestDTO.getName())
                .birth(socialSignUpRequestDTO.getBirth())
                .point(0)
                .score(0)
                .build());

        AuthToken accessToken = tokenProvider.createAuthToken(member.getId(),
                ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode()
                , new Date(now.getTime() + appProperties.getAuth().getTokenExpiry()));

        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

        AuthToken refreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        Optional<RefreshToken> oldRefreshToken = refreshTokenRepository.findByEmail(member.getEmail());

        // 원래 RefreshToken이 있으면 갱신해줘야함
        if(oldRefreshToken.isPresent()){
            RefreshToken  token = refreshTokenRepository.findById(oldRefreshToken.get().getId()).get();
            token.setToken(refreshToken.getToken());
            refreshTokenRepository.save(token);
        }
        // 없으면 생성
        else{
            refreshTokenRepository.save(RefreshToken.builder()
                    .token(refreshToken.getToken())
                    .email(member.getEmail())
                    .build());
        }

        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        result.setMessage("SOCAIL LOGIN SUCCESS");
        result.setCode(200);
        result.setResponseData("accessToken", accessToken.getToken());
        return result;
    }

    public ApiResponse findEmail(FindEmailRequestDTO findEmailRequestDTO) {
        ApiResponse result = new ApiResponse();

        System.out.println(findEmailRequestDTO.getNickName());
        Member member = memberRepository.findByNickName(findEmailRequestDTO.getNickName());

        if(member == null || !member.getMemberInfo().getName().equals(findEmailRequestDTO.getName())){
            result.setMessage("FIND EMAIL FAIL");
            result.setCode(400);
        }
        else {
            result.setMessage("FIND EMAIL SUCCESS");
            result.setResponseData("email", member.getEmail());
            result.setCode(200);
        }

        return result;

    }

    public ApiResponse memberGroup() {
        ApiResponse result = new ApiResponse();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.getPrincipal().equals("anonymousUser")) {
            Long memberId = Long.parseLong(((UserDetails) authentication.getPrincipal()).getUsername());

            Optional<Member> check = memberRepository.findById(memberId);

            if (check.isPresent()) {
                Member currUser = check.get();

                List<WrapperMy> wraperList = new LinkedList<>();

                List<Group> myGroupList = memberGroupRepository.findByMemberId(memberId).stream().map(x->x.getGroup()).collect(Collectors.toList());

                for (Group g:
                        myGroupList) {

                    MyGroup myGroup = MyGroup.builder()
                            .id(g.getId())
                            .name(g.getGroupName())
                            .description(g.getGroupInfo().getDescription())
                            .ctg(g.getCategory().stream().map(x->x.getCategory().getName().toString()).collect(Collectors.toList()))
                            .imgLink(g.getGroupInfo().getImageLink())
                            .build();


                    // myData
                    // myData.studyTime
                    List<Long> roomIdList = g.getSprints().stream().map(x -> x.getRoom().getId()).collect(Collectors.toList());
                    List<MemberRoomLog> memberRoomLogList = mrlRepository.findByMemberIdAndRoomIdIn(currUser.getId(), roomIdList);

                    int studyTime = 0;
                    for (MemberRoomLog mrl :
                            memberRoomLogList) {
                        studyTime += mrl.getStudyTime();
                    }

                    // myData.penalty
                    List<Integer> penalty = new ArrayList<>(Mode.values().length);
                    /*List<PenaltyLog> penaltyLogList = penaltyLogRegistory.findAllByMemberIdAndSprintIn(currUser.getId(), g.getSprints());

                    for (Mode m :
                            Mode.values()) {
                        penalty.add(rule.ordinal(), (int) (long) penaltyLogList.stream().filter(x -> x.getRule().getRuleName().ordinal() == rule.ordinal()).count());
                    }*/

                    MemberGroup currMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(currUser.getId(), g.getId()).get();

                    MyData myData =  MyData.builder()
                            .studyTime(studyTime)
                            .penalty(penalty)
                            .joinDate(format.format(currMemberGroup.getCreatedAt()))
                            .build();

                    wraperList.add(WrapperMy.builder()
                            .myGroup(myGroup)
                            .myData(myData)
                            .build());
                }

                result.setCode(200);
                result.setMessage("SUCCESS");
                result.setResponseData("myGroups", wraperList);

            } else {
                result.setCode(501);
                result.setMessage("no such user");
            }

        } else {
            result.setCode(501);
            result.setMessage("login first");
        }


        return result;

    }
}
