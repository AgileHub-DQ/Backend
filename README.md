

## [AgileHub 바로가기](https://www.agilehub.store)

<img src="https://github.com/AgileHub-DQ/Backend/assets/82764703/e4b936dc-2717-4bb7-b8a3-5794d9ff0c6a" width="30%">

<br>
<br>

<b> 🚀 애자일 기반 이슈 추적 웹 서비스 🚀 </b>

<b>애자일</b>은 변화에 빠르게 적응하고 반복적으로 개선하는 프로젝트 관리 방법이에요.
<br>
복잡한 설명 없이도, 우리 서비스로 당신의 프로젝트를 더욱 유연하고 효과적으로 관리해 보세요!
<br>
<br>

## 서비스 소개


![image2](https://github.com/AgileHub-DQ/Backend/assets/82764703/1c62b721-3f5c-4ff7-809e-39540faa19af)
![이슈](https://github.com/AgileHub-DQ/Backend/assets/82764703/83517937-9ecb-450e-9e79-9ae8099eb350)
![스프린트](https://github.com/AgileHub-DQ/Backend/assets/82764703/fb6fdd90-acc8-4ae9-bf37-a60e19b91682)
![백로그용GIF](https://github.com/AgileHub-DQ/Backend/assets/82764703/dbb4d960-16e8-41ee-baa2-d6e5abb4a9d5)
![타임라인](https://github.com/AgileHub-DQ/Backend/assets/82764703/7d02b3e1-62d5-48b8-bfae-6197e84f68c4)
![멤버](https://github.com/AgileHub-DQ/Backend/assets/82764703/efbe79ed-9f18-4508-9b63-853ac9e217b6)


<br>

</div>

## 🛠️ 기술 스택

### 백엔드

<img width="60%" alt="스크린샷 2024-01-22 오후 11 43 16" src="https://github.com/AgileHub-DQ/Backend/assets/82764703/5ae46555-02d5-41e0-bf05-f9b6cdfa809e">

### 인프라

<img width="60%" alt="스크린샷 2024-01-22 오후 11 44 27" src="https://github.com/AgileHub-DQ/Backend/assets/82764703/80aa0b22-01a3-4b9d-98b9-3d569c9f1647">

<br>

## 서비스 요청 흐름도

<img width="70%" alt="스크린샷 2024-01-22 오후 11 44 27" src="https://github.com/AgileHub-DQ/Backend/assets/82764703/0ee31d45-7faa-42cd-9705-040bbc0d39ef">

## CI/CD

<img width="70%" alt="스크린샷 2024-01-22 오후 11 44 27" src="https://github.com/AgileHub-DQ/Backend/assets/82764703/161395f7-eae6-4ff0-ab98-8de072dad09d">

## 모니터링 구조도

<img width="70%" alt="스크린샷 2024-01-22 오후 11 44 27" src="https://github.com/AgileHub-DQ/Backend/assets/82764703/29bdf578-f76b-41dd-9c6f-bbc5ca9b5038">

## 프로젝트 특징

### 1. Docker 이미지를 GitHub Actions로 빌드 밎 배포하는 시간 단축 (13분 -> 5분)
[배포 하는데 걸리던 시간 13분을 5분으로 줄이기](https://babgeuleus.tistory.com/entry/%EB%B0%B0%ED%8F%AC-%ED%95%98%EB%8A%94%EB%8D%B0-%EA%B1%B8%EB%A6%AC%EB%8D%98-%EC%8B%9C%EA%B0%84-13%EB%B6%84%EC%9D%84-5%EB%B6%84%EC%9C%BC%EB%A1%9C-%EC%A4%84%EC%9D%B4%EA%B8%B0)
- 멀티스테이지 빌드 사용으로 이미지 크기 감소
- Docker 캐싱을 활용하여 빌드 시간 단축
- Gradle 빌드 옵션 최적화 (병렬 빌드 사용 및 테스트 제외)
- **성과**
    - 처음에 이미지 빌드 시 총 700MB였던 것이 적용 후 320MB로 줄어들어, 50% 이상의 용량 최적화달성
    - 배포 시간 13분에서 5분으로 단축 (약 62% 감소)
 
### 2. GitHub Actions를 활용한 CI/CD 파이프라인 구축 및 보안 문제 해결 
[self-hosted runners를 활용한 CI/CD 파이프라인 구축](https://babgeuleus.tistory.com/entry/CICD-%ED%8C%8C%EC%9D%B4%ED%94%84%EB%9D%BC%EC%9D%B8-%EA%B5%AC%EC%B6%95-Github-Actions-self-hosted-runners)
<br>
[GitHub Actions를 활용한 CI/CD 구축](https://babgeuleus.tistory.com/entry/ci-cd)
<br>
[Jenkins vs GitHub Actions](https://azure-capston.atlassian.net/wiki/x/AYAe)

- 팀원들의 CI/CD 학습 부담 경감을 위해 GitHub Actions와 Jenkins 비교 및 문서화
- GitHub Actions의 간편한 워크플로우 구축과 SSH 보안 문제 해결을 위해 self-hosted runners 도입

### 3. 개발 서버에서 간단한 에러를 신속하게 확인하고 해결
[Logback 파일 설정](https://babgeuleus.tistory.com/entry/%EC%84%9C%EB%B2%84-%EC%97%90%EB%9F%AC%EB%A5%BC-%EB%B9%A0%EB%A5%B4%EA%B2%8C-%EB%B0%9C%EA%B2%AC%ED%95%98%EA%B3%A0-%ED%95%B4%EA%B2%B0%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95-%EB%A1%9C%EA%B7%B8-%EB%B6%84%EC%84%9D%EC%9D%98-%ED%95%B5%EC%8B%AC)
<br>
[volume경로 잘못된 설정으로 생긴 로그 추출 못하는 문제 해결](https://babgeuleus.tistory.com/entry/%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85-docker-volume-%EC%A0%9C%EB%8C%80%EB%A1%9C-%EC%84%A4%EC%A0%95%ED%95%98%EC%9E%90)
<br>
[모니터링 구축](https://azure-capston.atlassian.net/wiki/x/FoB0Ag)
<br><br>
**[문제점]** <br>
개발 서버에서 발생한 간단한 에러조차 신속하게 확인하고 해결하는 데 어려움 <br>

**[문제 해결을 위한 접근법]** <br>
1. **vim nohup.out 실행**
  - 로그파일 생성에 예상보다 많은 시간 소요
  - 자주 ssh 세션이 멈추는 문제로 인해 결국 예외가 발생한 부분을 확인하지 못하는 어려움을 겪음
2. **서버 종료 후 java -jar 명령어로 직접 실행**
  - 서버를 종료하고 jar 파일을 직접 실행하여 예외 발생 시 로컬에서 터미널을 통해 바로 확인할 수 있게 해 에러를 해결
3. **배포를 jar파일에서 docker image로 변경**
  - 예외 발생 시 docker logs [컨테이너 ID]를 통해 컨테이너 상태와 로그를 더 쉽게 확인 가능
  - 하지만 이전 에러 기록을 찾는 것이 번거로움
4. **Logback 파일 설정**
  - Prod 서버에서 ERROR 로그 레벨 부터 로그파일 생성
  - 로그 파일의 최대 용량과 보존 기간을 매일 최대 50MB, 최대 7일 보존 기간으로 설정
5. **로키와 그라파나를 이용한 로그 모니터링**
  - 스프링 액추에이터를 이용해 로그와 JVM memory 사용률 추출
  - 프로메테우스로 JVM 메모리 사용률 pull 해서 그라파나로 시각화
  - 프롬테일에서 액추에이터에서 추출한 로그내역 로키에 push, 로키는 pull하여 그라파나로 시각화
 



<br>
<br>


## 추가정보

### 👉 [팀위키](https://azure-capston.atlassian.net/wiki/x/3IAH)

### 👉 [Backend API Swagger Link](https://api.agilehub.store/swagger-ui/index.html)

### 👉 ERD

<img width="70%" alt="스크린샷 2024-01-22 오후 11 44 27" src="https://github.com/AgileHub-DQ/Backend/assets/82764703/0f6dcc71-7dc1-4559-922a-80076ec4f64e">

### 👉 로그인 요청 흐름도

<img width="70%" alt="스크린샷 2024-01-22 오후 11 44 27" src="https://github.com/AgileHub-DQ/Backend/assets/82764703/2581aad8-8c02-4a7d-a213-6caeac67dcc9">

### 👉 이메일 인증 요청 흐름도

<img width="70%" alt="스크린샷 2024-01-22 오후 11 44 27" src="https://github.com/AgileHub-DQ/Backend/assets/82764703/7addb7b2-601c-4b4c-87c4-117abcc1ddc7">

<br>

### 👉 멤버

| <img src="https://avatars.githubusercontent.com/u/82764703?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/68385400?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/107299318?v=4" width="130" height="130"> | <img src ="https://avatars.githubusercontent.com/u/109426768?v=4" width="130" height="130"> | 
|:-----------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------:| :------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------:|
|                           [BE: 김민상](https://github.com/minsang-alt)                           |                              [BE: 최재영](https://github.com/Enble)        |                              [FE: 신승혜](https://github.com/drimh)                               |                             [FE: 주원희](https://github.com/wonhee126)                       |


