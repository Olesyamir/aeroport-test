//package fr.uga.miage.m1.Domain.exeptions
//
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.MethodArgumentNotValidException
//import org.springframework.web.bind.annotation.ExceptionHandler
//import org.springframework.web.bind.annotation.RestControllerAdvice
//
//@RestControllerAdvice
//class GlobalExceptionHandler {
//
//    @ExceptionHandler(NoSuchElementException::class)
//    fun handleNotFound(e: NoSuchElementException): ResponseEntity<Map<String, String?>> {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to e.message))
//    }
//
//    @ExceptionHandler(IllegalArgumentException::class)
//    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<Map<String, String?>> {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to e.message))
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException::class)
//    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String?>> {
//        val errorMessage = ex.bindingResult.fieldErrors
//            .joinToString(", ") { "${it.field} ${it.defaultMessage}" }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to errorMessage))
//    }
//
//    @ExceptionHandler(Exception::class)
//    fun handleInternal(ex: Exception): ResponseEntity<Map<String, String>> {
//        val body = mapOf("message" to "Erreur interne du serveur")
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
//    }
//}