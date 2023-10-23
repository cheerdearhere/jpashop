# jpashop
    JPA를 Spring boot에 적용하는 프로젝트  
        - JPA
        - Spring boot
        - gradle
        - lombok
        - h2 db
        - thymeleaf

# jar에서 테스트 진행
    \reactWorkspace\jpashop> ./gradlew clean build
    BUILD SUCCESSFUL in 43s
    8 actionable tasks: 8 executed
    \reactWorkspace\jpashop\build> java -jar jpashop-0.0.1-SNAPSHOT.jar
    JRE java 버전이 맞지 않으면 에러

#  remain query parameter log
    기본 로그 추가(application.yml 파일)
    logging:
        level:
            org.hibernate:
                sql: debug # 로그 수준 지정
                type: trace # parameter 정보 표시
    외부 라이브러리 적용
    spring-boot-data-source-decorator library 사용
    implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0' //부트에 미리 입력된 라이브러리가 아니라 버전 연결
    운영에서는 성능 저하의 원인이 될수있으므로 꼭 체크
