package org.romanin.musicadvisor2;

public class SpotifyError extends Throwable {

    private final int errorCode;

    public SpotifyError(int errorCode, String errorMessage, Exception e) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

}

class InvalidActionException extends Throwable {

}
