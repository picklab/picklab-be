package picklab.backend.common.file.application

interface FileStoragePort {
    /**
     * 파일 업로드를 위한 presigned URL을 생성합니다.
     */
    fun generateUploadPresignedUrl(
        contentType: String,
        key: String,
    ): String

    /**
     * 파일의 public read URL을 반환합니다.
     */
    fun getPublicReadUrl(key: String): String
}
