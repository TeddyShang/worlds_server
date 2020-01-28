package worlds.server;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class MediaMetaDataNotFoundAdvice {

  @ResponseBody
  @ExceptionHandler(MediaMetaDataNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String mediaMetaDataNotFoundHandler(MediaMetaDataNotFoundException ex) {
    return ex.getMessage();
  }
}