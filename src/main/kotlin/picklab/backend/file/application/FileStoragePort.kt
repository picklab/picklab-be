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
}
