package com.hosein.jobportal.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
class FileSizeExceptionAdvice {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleFileSizeException() {
        return "redirect:/dashboard";
    }
}