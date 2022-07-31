package com.A108.Watchme.Service;

import com.A108.Watchme.DTO.GetRoomResDTO;
import com.A108.Watchme.DTO.PostRoomReqDTO;
import com.A108.Watchme.Http.ApiResponse;
import com.A108.Watchme.Repository.*;
import com.A108.Watchme.VO.ENUM.CategoryList;
import com.A108.Watchme.VO.ENUM.RoomStatus;
import com.A108.Watchme.VO.Entity.Category;
import com.A108.Watchme.VO.Entity.log.MemberRoomLog;
import com.A108.Watchme.VO.Entity.member.Member;
import com.A108.Watchme.VO.Entity.room.Room;
import com.A108.Watchme.VO.Entity.room.RoomInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM월 dd일 HH시 mm분");
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final MRLRepository mrlRepository;
    private final RoomInfoRepository roomInfoRepository;
    private final CategoryRepository categoryRepository;

    private final S3Uploader s3Uploader;

    public ApiResponse createRoom(PostRoomReqDTO postRoomReqDTO, MultipartFile images, HttpServletRequest request) {

        ApiResponse result = new ApiResponse();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (!authentication.getAuthorities().toString().equals("[ROLE_ANONYMOUS]")) {
                System.out.println("heelo");
                String url = "https://popoimages.s3.ap-northeast-2.amazonaws.com/StudyRoom.jpg";

                try{
                    url = s3Uploader.upload(images,"Watchme");
                } catch(Exception e){
                    e.printStackTrace();
                }


                UserDetails currUser = (UserDetails) authentication.getPrincipal();
                Member member = memberRepository.findByEmail(currUser.getUsername());
                CategoryList name = CategoryList.valueOf(postRoomReqDTO.getCategoryName());
                Category category = categoryRepository.findByName(name);
                Room room = Room.builder()
                        .roomName(postRoomReqDTO.getRoomName())
                        .member(member)
                        .roomCtg(category)
                        .status(RoomStatus.valueOf(postRoomReqDTO.getStatus()))
                        .view(0)
                        .build();

                RoomInfo roominfo = RoomInfo.builder()
                        .room(room)
                        .maxMember(postRoomReqDTO.getNum())
                        .currMember(0)
                        .endAt(postRoomReqDTO.getEndTime())
                        .description(postRoomReqDTO.getDescription())
                        .imageLink(url)
                        .build();


                roomRepository.save(room);
                roomInfoRepository.save(roominfo);
                joinRoom(room.getId());
                result.setCode(200);
                result.setMessage("SUCCESS ADD&JOIN ROOM");
                result.setResponseData("roomId", room.getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500);
        }

        return result;
    }


    public ApiResponse getRoom(String ctgName, int page) {
        ApiResponse result = new ApiResponse();
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        List<Room> roomList;
        if(ctgName!=null){
            CategoryList name = CategoryList.valueOf(ctgName);
            Category category = categoryRepository.findByName(name);
            roomList = roomRepository.findAllByRoomCtg(category, pageRequest).stream().collect(Collectors.toList());
        }
        else{
            roomList = roomRepository.findAllByOrderByViewDesc(pageRequest).stream().collect(Collectors.toList());
        }

        List<GetRoomResDTO> getRooms = new LinkedList<>();
        for (Room room : roomList) {
             getRooms.add(new GetRoomResDTO().builder()
                    .id(room.getId())
                    .roomImage(room.getRoomInfo().getImageLink())
                    .roomName(room.getRoomName())
                    .roomStatus(room.getStatus())
                    .ctgName(room.getRoomCtg().getName())
                    .description(room.getRoomInfo().getDescription())
                    .endTime(simpleDateFormat.format(room.getRoomInfo().getEndAt()))
                    .secret(room.getRoomInfo().getPwd() != null)
                    .nowNum(room.getRoomInfo().getCurrMember())
                    .maxNum(room.getRoomInfo().getMaxMember())
                    .build()
            );
        }
        result.setResponseData("rooms", getRooms);
        result.setMessage("GETROOMS SUCCESS");
        result.setCode(200);
        return result;
    }


    public ApiResponse joinRooms(Long roomId) {
        ApiResponse result = new ApiResponse();
        try{
            joinRoom(roomId);
            result.setMessage("JOIN SUCCESS");
            result.setCode(200);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500);
        }

        return result;
    }
    public void joinRoom(Long roomId){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!authentication.getAuthorities().toString().equals("[ROLE_ANONYMOUS]")) {
                UserDetails currUser = (UserDetails) authentication.getPrincipal();
                Member member = memberRepository.findByEmail(currUser.getUsername());

                mrlRepository.save(new MemberRoomLog().builder()
                        .room(roomRepository.findById(roomId).get())
                        .member(member)
                        .build()
                );
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
