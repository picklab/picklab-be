package picklab.backend.notification.domain.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Service
class SseEmitterService {
    
    private val logger = LoggerFactory.getLogger(SseEmitterService::class.java)
    
    // 사용자별 SseEmitter 연결을 관리하는 Map
    private val emitters = ConcurrentHashMap<Long, SseEmitter>()
    
    companion object {
        private const val DEFAULT_TIMEOUT = 60L * 1000 * 60 // 60분
    }
    
    /**
     * 사용자의 SSE 연결을 생성하고 관리합니다
     */
    fun createEmitter(memberId: Long): SseEmitter {
        val emitter = SseEmitter(DEFAULT_TIMEOUT)
        
        // 기존 연결이 있다면 완료 처리
        emitters[memberId]?.complete()
        
        // 새로운 연결 저장
        emitters[memberId] = emitter
        
        // 연결 완료 시 정리
        emitter.onCompletion {
            logger.info("SSE 연결이 완료되었습니다. memberId: $memberId")
            emitters.remove(memberId)
        }
        
        // 연결 타임아웃 시 정리
        emitter.onTimeout {
            logger.info("SSE 연결이 타임아웃되었습니다. memberId: $memberId")
            emitters.remove(memberId)
        }
        
        // 연결 에러 시 정리
        emitter.onError {
            logger.error("SSE 연결에 에러가 발생했습니다. memberId: $memberId", it)
            emitters.remove(memberId)
        }
        
        // 연결 성공 이벤트 전송
        try {
            emitter.send(
                SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결이 성공했습니다.")
            )
        } catch (e: Exception) {
            logger.error("초기 연결 이벤트 전송 실패. memberId: $memberId", e)
            emitters.remove(memberId)
            emitter.completeWithError(e)
        }
        
        return emitter
    }
    
    /**
     * 특정 사용자에게 이벤트를 전송합니다
     */
    fun sendEventToUser(memberId: Long, eventName: String, data: Any): Boolean {
        val emitter = emitters[memberId] ?: return false
        
        try {
            emitter.send(
                SseEmitter.event()
                    .name(eventName)
                    .data(data)
            )
            return true
        } catch (e: Exception) {
            logger.error("이벤트 전송 실패. memberId: $memberId, eventName: $eventName", e)
            // 전송 실패 시 연결 제거
            emitters.remove(memberId)
            emitter.completeWithError(e)
            return false
        }
    }
    
    /**
     * 현재 연결된 사용자 수를 반환합니다
     */
    fun getConnectedUserCount(): Int {
        return emitters.size
    }
    
    /**
     * 특정 사용자가 연결되어 있는지 확인합니다
     */
    fun isUserConnected(memberId: Long): Boolean {
        return emitters.containsKey(memberId)
    }
} 