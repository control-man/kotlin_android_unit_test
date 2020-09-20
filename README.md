# kotlin_android_unit_test


코루틴빌더 스코프 등 블로그  
https://12bme.tistory.com/582  


코루틴 테스트 공식문서 (runblocking 와 runblockingTest의 차이점)
https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/


코루틴 테스팅 Android Dev 영상
https://www.youtube.com/watch?v=KMb0Fs8rCRs&feature=youtu.be


mockk
https://github.com/mockk/mockk


runblockingTest
1. Creates a new coroutine
2. Allows us to execute suspend functions immedaiately (실제로 delay하지 않고 실행할 수 있다.)

Difference with runblocking?
runblockingTest skips delays


블로깅
https://craigrussell.io/2019/11/unit-testing-coroutine-suspend-functions-using-testcoroutinedispatcher/


coroutineScope와 runblocking 차이 또 CoroutineScope(coroutineContext) 는 다르다
https://eso0609.tistory.com/82

1.CoroutineScope는 runBlocking 내부에서만 사용 가능
2.runBlocking에서는 자식 스레드가 완료될 때 까지 현재 스레드를 block 한다.
3.coroutineScope에서는 자식 스레드가 완료될 때 까지 현재 스레드를 block 하지 않는다.
4.coroutineScope 자체가 async/await 이걸 잘생각해야한다. 스레드를 점유하지않고 또다른 일을 할 수있긴함


coroutineScope 의 async / await동작을 보여주는
https://stackoverflow.com/questions/53535977/coroutines-runblocking-vs-coroutinescope

코루틴에 관한 좋은글 및 SupervisorJob에 대한 설명, coroutineScope {...} 에대한 설명
https://seunghyun.in/android/7/ <- 설명이 되게 잘되어있음. 천천히 정독 계속 해보자 ★★★★

어떨때 RunBlocking RunBlockingTest를 써주는지 알려줌 runBlocking {...} 으로 테스트 할 수 없는경우에 대한 예시도 있음.


코루틴스코프, 코루틴컨텍스트의 개념을 잘 생각하자.
스코프가 달라짐으로인해서 Runblocking 이나 corutineScope 안이지만 같은 자식이 아니라서
더이상 기다려 주지 못하는경우가 발생해서 명시적으로 join을 호출해주거나 해야함.

