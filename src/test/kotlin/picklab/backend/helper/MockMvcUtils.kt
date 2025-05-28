package picklab.backend.helper

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.test.web.servlet.MvcResult

inline fun <reified T> MvcResult.extractBody(mapper: ObjectMapper): T =
    mapper.readValue(this.response.contentAsString, object : TypeReference<T>() {})
