package picklab.backend.file.application

interface FileStoragePort {
    /**
     * 파일 업로드를 위한 presigned URL을 생성합니다.
     */
    fun generateUploadPresignedUrl(
        contentType: String,
        key: String,
        fileSize: Long,
    ): String

    /**
     * 요청으로 들어온 임시저장키가 유효한지 검증합니다.
     */
    fun verifyTempFileExists(key: String)

    /**
     * 임시저장된 파일을 영구저장소로 이동합니다.
     * @return 영구 저장소로 이동된 파일의 public read URL
     */
    fun moveTempFileToPermanent(key: String): String

    /**
     * Storage에 저장된 파일을 삭제합니다.
     */
    fun deleteFile(key: String)

    /**
     * 특정 prefix로 시작하는 모든 키 목록을 조회합니다.
     */
    fun listObjectKeys(prefix: String): List<String>

    /**
     * 요청된 키들 중 실제 존재하는 것만 영구저장소로 이동합니다.
     * @param requestedKeys 요청된 키 목록
     * @param availableKeys prefix로 조회된 실제 존재하는 키 목록
     * @return 영구 저장소로 이동된 파일들의 public read URL 목록
     */
    fun moveValidTempFilesToPermanent(
        requestedKeys: List<String>,
        availableKeys: List<String>,
    ): List<String>
}
