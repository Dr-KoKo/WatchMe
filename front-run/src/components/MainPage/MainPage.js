import React from "react";
import { useContext } from "react";

import "./MainPage.css";
import { Link } from "react-router-dom";
import groupInfor from "../json/groupInfor"
import roomInfor from "../json/roomInfor"
import userInfor from "../json/userInfor"
import { FetchUrl } from "../../store/communication";


function MainPage() {


  //URL
  const FETCH_URL = useContext(FetchUrl);
  const url = `${FETCH_URL}/MainPage`;

  let userInformation=userInfor[0]["myUserInfor"][0];

  let myGroups=  groupInfor[0]["MainpageMyGroup"];
  let groups=  groupInfor[0]["MainpageGroups"];
  let rooms=  roomInfor[0]["MainpageRooms"];
  let groupNo=0;
  let roomNo=0;
  let myGroupNo=0;


  let MainJSP;
  const mainPageSetting=(event)=>{

    fetch(url+"UserInformation")
    .then((response) => {
      if (response.ok) {
        return response.json();
      } else {
        response.json().then((data) => {
          let errorMessage = "유저정보";
          throw new Error(errorMessage);
        });
      }
    })
    .then((result) => {
      MainJSP=result;
    })
    .catch((err) => {
      console.log("통신실패");
    });

    
  }

  mainPageSetting();
  return (
    <>
    <div id="outer">
      <section id='mainpage__myinfor'>{/*개인과 관련된 섹션. 임시링크들 있음 수정예정 */}
        <div id='mypage__myinfor__title'>오늘도 화이팅, {userInformation["nickname"]}</div>
        <div id='mypage__myinfor__create-room'>{/*방생성관련 */}
          <div className='mypage__myinfor__sub-title'>
            방만들기
          </div>
          <Link to="/RoomCreate">
            <div id='mypage__myinfor__create-room__create'>

               <div id='rooml'></div>
              <div id='room-'></div>
            </div>
          </Link>
        </div> 
        <div id='mypage__myinfor__mystduy-group'>{/*내가 가입한 스터디그룹 관련*/}
          <div className='mypage__myinfor__sub-title'>
            내 스터디그룹
          </div>  
          
          <Link to="/GroupDetail">
            <div id='mypage__myinfor__mystduy-group1' className='mypage__myinfor__mystduy-group-image'>
              { myGroups[myGroupNo]["groupImage"]==="none"?"이미지 없음":myGroups[myGroupNo]["groupImage"]}
              <div className= 'mypage__myinfor__mystduy-group__img'></div>
              <div className='mypage__myinfor__mystduy-group__Title'>{myGroups[myGroupNo]["groupName"]}</div>
            </div>
          </Link>
          <Link to="/GroupDetail">
            <div id='mypage__myinfor__mystduy-group2' className='mypage__myinfor__mystduy-group-image'>
              { myGroups[++myGroupNo]["groupImage"]==="none"?"이미지 없음":myGroups[myGroupNo]["groupImage"]}
                <div className= 'mypage__myinfor__mystduy-group__img'></div>
                <div className='mypage__myinfor__mystduy-group__Title'>{myGroups[myGroupNo]["groupName"]}</div>
            </div>
          </Link>
          
        </div>
        <div id='mypage__myinfor__mystduy'>{/*내가 공부한 총 공부시간 관련. */}
          <div className='mypage__myinfor__sub-title'>
            나의 공부시간
          </div>
          <Link to="MyPage">
            <div id='mypage__myinfor__mystduy__infor'>
              <div id='left'>
                  오늘 공부시간<br></br>
                  이번 주 공부시간<br></br>
                  이번 달 목표시간<br></br>
                  총 공부시간<br></br>
              </div>
              <div id='right'>
                {parseInt(userInformation["studyTimeToday"]/60)?parseInt(userInformation["studyTimeToday"]/60)+"시간":""} {userInformation["studyTimeToday"]%60?userInformation["studyTimeToday"]%60+"분":""}<br></br>
                {parseInt(userInformation["studyTimeWeek"]/60)?parseInt(userInformation["studyTimeWeek"]/60)+"시간":""} {userInformation["studyTimeWeek"]%60?userInformation["studyTimeWeek"]%60+"분":""}<br></br>
                {parseInt(userInformation["studyTimeMonth"]/60)?parseInt(userInformation["studyTimeMonth"]/60)+"시간":""} {userInformation["studyTimeMonth"]%60?userInformation["studyTimeMonth"]%60+"분":""}<br></br>
                {parseInt(userInformation["studyTimeTotal"]/60)?parseInt(userInformation["studyTimeTotal"]/60)+"시간":""} {userInformation["studyTimeTotal"]%60?userInformation["studyTimeTotal"]%60+"분":""}
              </div>
            </div> 
          </Link>
          
        </div>
      </section>
      <section id='mainpage_study-groups'>{/* 스터디 그룹탐색 관련 섹션 연결하는 임시링크들 있음. 수정예정. */}
        <div className='section__top'>
          <div className= 'section__top__text'>인기있는<br></br> 모집한 스터디그룹🥇 </div>
          <Link to="/GroupRecruit"><button className= 'section__top__btn'>더보기</button></Link>
          <div className='section__top__rec'></div>
        </div>
        <div id='mainpage__study-groups__groups'>{/*실제 글부의 정보를 가져와 연결해줌 조 수 높은 그룹이 우선순으로 표기됨. */}


          <Link to="/GroupDetail">
            <div id='mainpage__study-groups__groups__1' className='mainpage__study-groups__groups'>
              <div className='groups__circle'>
                {groups[groupNo]["groupImage"]==="none"?"기본이미지":groups[groupNo]["groupImage"]}
              </div>
              <div className='groups__group-name'>{groups[groupNo]["groupName"]}</div>
              <div className='groups__group-descript'>{groups[groupNo]["groupDiscription"]}</div>
              <div className='groups__mem-no-rec'>{groups[groupNo]["groupMemberNo"]}/{groups[groupNo]["groupMemberMaxNo"]}</div>
            </div>
          </Link>
          <Link to="/GroupDetail">
            <div id='mainpage__study-groups__groups__2' className='mainpage__study-groups__groups'>
              <div className='groups__circle'>
                {groups[++groupNo]["groupImage"]==="none"?"기본이미지":groups[groupNo]["groupImage"]}
              </div>
              <div className='groups__group-name'>{groups[groupNo]["groupName"]}</div>
              <div className='groups__group-descript'>{groups[groupNo]["groupDiscription"]}</div>
              <div className='groups__mem-no-rec'>{groups[groupNo]["groupMemberNo"]}/{groups[groupNo]["groupMemberMaxNo"]}</div>
            </div>
          </Link>
          <Link to="/GroupDetail">
            <div id='mainpage__study-groups__groups__3' className='mainpage__study-groups__groups'>
              <div className='groups__circle'>
                {groups[++groupNo]["groupImage"]==="none"?"기본이미지":groups[groupNo]["groupImage"]}
              </div>
              <div className='groups__group-name'>{groups[groupNo]["groupName"]}</div>
              <div className='groups__group-descript'>{groups[groupNo]["groupDiscription"]}</div>
              <div className='groups__mem-no-rec'>{groups[groupNo]["groupMemberNo"]}/{groups[groupNo]["groupMemberMaxNo"]}</div>
            </div>
          </Link>
          <Link to="/GroupDetail">
            <div id='mainpage__study-groups__groups__4' className='mainpage__study-groups__groups' >
              <div className='groups__circle'>
                {groups[++groupNo]["groupImage"]==="none"?"기본이미지":groups[groupNo]["groupImage"]}</div>
              <div className='groups__group-name'>{groups[groupNo]["groupName"]}</div>
              <div className='groups__group-descript'>{groups[groupNo]["groupDiscription"]}</div>
              <div className='groups__mem-no-rec'>{groups[groupNo]["groupMemberNo"]}/{groups[groupNo]["groupMemberMaxNo"]}</div>
            </div>
          </Link>
       
       
        </div>
      </section>
      <section id='mainpage__banner'>{/* 배너섹션 수정사항 없을예정. */}
        <div id='mainpage__banner__title'>
          WATCH ME는 
          <br></br>혼자여도 같이합니다.</div>
        <div id='mainpage__banner__main'>
          의지박약자여,<br></br>이리로 모여라.
          <br></br>같이 집중하자👊</div>
        <div id='mainpage__banner__description'>
          공부만 시작하면 스마트폰에 눈이 팔린다구요?<br></br>
          누가 나좀 감시해줬으면 좋겠다구요?<br></br>
          <br></br>Watch Me와 함께라면 걱정마세요!<br></br>
          혼자만의 시간을 보낼 수도,<br></br> 그룹을 만들어서 스프린트를 만들수도,<br></br>
          아무와나 함께할 수 있습니다.</div>
      </section>
      <section id='mainpage__meeting-rooms'>{/* 공개미팅룸관련 섹션 연결하는 임시링크잇음. 수정예정. */}
        <div className='section__top'>
            <div className= 'section__top__text'>활활타오르는<br></br> 진행중인 공개룸🔥</div>
            <Link to="/RoomRecruit"><button className= 'section__top__btn'>더보기</button></Link>
            <div className='section__top__rec'></div>
        </div>
        <div id='mainpage__meeting-rooms__rooms'>{/*실제 방의 정보를 가져와 연결해줌 조회수가 높은 방이 우선순으로 표기됨*/}
          <Link to='/RoomDetail'> 
            <div id='mainpage__meeting-rooms__rooms__1'className='mainpage__meeting-rooms__rooms'>
              <div className='mainpage__meeting-rooms__rooms__img'>{/*미팅룸 이미지 내부에 기능들 표기됨*/}
                {rooms[roomNo]["roomImage"]==="none"?"(이미지없음)":rooms[roomNo]["roomImage"]}
                <div className='mainpage__meeting-rooms__rooms__img__funcs'>
                  <p>⏱ 기능</p>
                  ✔ 뽀모도로<br></br>
                  ✔ 캠 + 화면공유<br></br>
                  ✔ 채팅 O<br></br><br></br>
                  <p>📝 규칙</p>
                  ✔ 휴대폰 인식<br></br>
                  ✔ 얼굴 인식<br></br>
                  ✔ 캠 켜기<br></br>
                </div>
              </div>
              <div className='mainpage__meeting-rooms__rooms__infor'>
                <div className='mainpage__meeting-rooms__tags'>{/*태그*/}
                  {rooms[roomNo]["roomTag"]}
                </div>
                <div className='mainpage__meeting-rooms__name'>{/*이름*/}
                  {rooms[roomNo]["roomName"]}
                </div>
                <div className='mainpage__meeting-rooms__descript'>{/*세부설명*/}
                  {rooms[roomNo]["roomDiscription"]}
                </div>
                <div className='mainpage__meeting-rooms__mem-no-rec'>
                  {rooms[roomNo]["romMemberNo"]}/{rooms[roomNo]["roomMemberMaxNo"]}
                </div>
              </div>
            </div>
          </Link>


          <Link to='/RoomDetail'> 
            <div id='mainpage__meeting-rooms__rooms__2'className='mainpage__meeting-rooms__rooms'>
              <div className='mainpage__meeting-rooms__rooms__img'>
                {rooms[++roomNo]["roomImage"]==="none"?"(이미지없음)":rooms[roomNo]["roomImage"]}
                <div className='mainpage__meeting-rooms__rooms__img__funcs'>
                  <p>⏱ 기능</p>
                  ✔ 뽀모도로<br></br>
                  ✔ 캠 + 화면공유<br></br>
                  ✔ 채팅 O<br></br><br></br>
                  <p>📝 규칙</p>
                  ✔ 휴대폰 인식<br></br>
                  ✔ 얼굴 인식<br></br>
                  ✔ 캠 켜기<br></br>
                </div>
              </div>
              <div className='mainpage__meeting-rooms__rooms__infor'>
                <div className='mainpage__meeting-rooms__tags'>
                  {rooms[roomNo]["roomTag"]}
                </div>
                <div className='mainpage__meeting-rooms__name'>
                  {rooms[roomNo]["roomName"]}
                </div>
                <div className='mainpage__meeting-rooms__descript'>
                  {rooms[roomNo]["roomDiscription"]}
                </div>
                <div className='mainpage__meeting-rooms__mem-no-rec'>
                  {rooms[roomNo]["romMemberNo"]}/{rooms[roomNo]["roomMemberMaxNo"]}
                </div>
              </div>
            </div>
          </Link>


          <Link to='/RoomDetail'>         
            <div id='mainpage__meeting-rooms__rooms__3'className='mainpage__meeting-rooms__rooms'>
              <div className='mainpage__meeting-rooms__rooms__img'>
                {rooms[++roomNo]["roomImage"]==="none"?"(이미지없음)":rooms[roomNo]["roomImage"]}
                <div className='mainpage__meeting-rooms__rooms__img__funcs'>
                  <p>⏱ 기능</p>
                  ✔ 뽀모도로<br></br>
                  ✔ 캠 + 화면공유<br></br>
                  ✔ 채팅 O<br></br><br></br>
                  <p>📝 규칙</p>
                  ✔ 휴대폰 인식<br></br>
                  ✔ 얼굴 인식<br></br>
                  ✔ 캠 켜기<br></br>
                </div>
              </div>
              <div className='mainpage__meeting-rooms__rooms__infor'>
                <div className='mainpage__meeting-rooms__tags'>
                  {rooms[roomNo]["roomTag"]}
                </div>
                <div className='mainpage__meeting-rooms__name'>
                  {rooms[roomNo]["roomName"]}
                </div>
                <div className='mainpage__meeting-rooms__descript'>
                  {rooms[roomNo]["roomDiscription"]}
                </div>
                <div className='mainpage__meeting-rooms__mem-no-rec'>
                  {rooms[roomNo]["romMemberNo"]}/{rooms[roomNo]["roomMemberMaxNo"]}
                </div>
              </div>
            </div>
          </Link>


          <Link to='/RoomDetail'> 
            <div id='mainpage__meeting-rooms__rooms__4'className='mainpage__meeting-rooms__rooms'>
              <div className='mainpage__meeting-rooms__rooms__img'>
                {rooms[++roomNo]["roomImage"]==="none"?"(이미지없음)":rooms[roomNo]["roomImage"]}
                <div className='mainpage__meeting-rooms__rooms__img__funcs'>
                  <p>⏱ 기능</p>
                  ✔ 뽀모도로<br></br>
                  ✔ 캠 + 화면공유<br></br>
                  ✔ 채팅 O<br></br><br></br>
                  <p>📝 규칙</p>
                  ✔ 휴대폰 인식<br></br>
                  ✔ 얼굴 인식<br></br>
                  ✔ 캠 켜기<br></br>
                </div>
              </div>
              <div className='mainpage__meeting-rooms__rooms__infor'>
                <div className='mainpage__meeting-rooms__tags'>
                  {rooms[roomNo]["roomTag"]}
                </div>
                <div className='mainpage__meeting-rooms__name'>
                  {rooms[roomNo]["roomName"]}
                </div>
                <div className='mainpage__meeting-rooms__descript'>
                  {rooms[roomNo]["roomDiscription"]}
                </div>
                <div className='mainpage__meeting-rooms__mem-no-rec'>
                  {rooms[roomNo]["romMemberNo"]}/{rooms[roomNo]["roomMemberMaxNo"]}
                </div>
              </div>
            </div>
          </Link>


          <Link to='/RoomDetail'> 
            <div id='mainpage__meeting-rooms__rooms__5'className='mainpage__meeting-rooms__rooms'>
              <div className='mainpage__meeting-rooms__rooms__img'>
                {rooms[++roomNo]["roomImage"]==="none"?"(이미지없음)":rooms[roomNo]["roomImage"]}
                <div className='mainpage__meeting-rooms__rooms__img__funcs'>
                  <p>⏱ 기능</p>
                  ✔ 뽀모도로<br></br>
                  ✔ 캠 + 화면공유<br></br>
                  ✔ 채팅 O<br></br><br></br>
                  <p>📝 규칙</p>
                  ✔ 휴대폰 인식<br></br>
                  ✔ 얼굴 인식<br></br>
                  ✔ 캠 켜기<br></br>
                </div>
              </div>
              <div className='mainpage__meeting-rooms__rooms__infor'>
                <div className='mainpage__meeting-rooms__tags'>
                  {rooms[roomNo]["roomTag"]}
                </div>
                <div className='mainpage__meeting-rooms__name'>
                  {rooms[roomNo]["roomName"]}
                </div>
                <div className='mainpage__meeting-rooms__descript'>
                  {rooms[roomNo]["roomDiscription"]}
                </div>
                <div className='mainpage__meeting-rooms__mem-no-rec'>
                  {rooms[roomNo]["romMemberNo"]}/{rooms[roomNo]["roomMemberMaxNo"]}
                </div>
              </div>
            </div>
          </Link>


          <Link to='/RoomDetail'> 
            <div id='mainpage__meeting-rooms__rooms__6'className='mainpage__meeting-rooms__rooms'>
              <div className='mainpage__meeting-rooms__rooms__img'>
                {rooms[++roomNo]["roomImage"]==="none"?"(이미지없음)":rooms[roomNo]["roomImage"]}
                <div className='mainpage__meeting-rooms__rooms__img__funcs'>
                  <p>⏱ 기능</p>
                  ✔ 뽀모도로<br></br>
                  ✔ 캠 + 화면공유<br></br>
                  ✔ 채팅 O<br></br><br></br>
                  <p>📝 규칙</p>
                  ✔ 휴대폰 인식<br></br>
                  ✔ 얼굴 인식<br></br>
                  ✔ 캠 켜기<br></br>
                </div>
              </div>
              <div className='mainpage__meeting-rooms__rooms__infor'>
                <div className='mainpage__meeting-rooms__tags'>
                  {rooms[roomNo]["roomTag"]}
                </div>
                <div className='mainpage__meeting-rooms__name'>
                  {rooms[roomNo]["roomName"]}
                </div>
                <div className='mainpage__meeting-rooms__descript'>
                  {rooms[roomNo]["roomDiscription"]}
                </div>
                <div className='mainpage__meeting-rooms__mem-no-rec'>
                  {rooms[roomNo]["romMemberNo"]}/{rooms[roomNo]["roomMemberMaxNo"]}
                </div>
              </div>
            </div>
          </Link>


         </div>

      </section></div>
    </>
  );
}

export default MainPage;
