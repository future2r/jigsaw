package name.ulbricht.jigsaw.greetings;

import java.util.Objects;
import java.time.LocalDateTime;

public final class Greetings {

    public static Greetings createMessage(final String message){
        return new Greetings(LocalDateTime.now(), System.getProperty("user.name"), message);
    }

    public Greetings withSource(final String source) {
        return new Greetings(this.time, source, this.message);
    }

    public Greetings atTime(final LocalDateTime time) {
        return new Greetings(time, this.source, this.message);
    }

    private final LocalDateTime time;
    private final String source;
    private final String message;

    private Greetings(final LocalDateTime time, final String source, final String message) {
        this.time = Objects.requireNonNull(time);
        this.source = Objects.requireNonNull(source);
        this.message = Objects.requireNonNull(message);
    }

    public LocalDateTime getTime(){
        return this.time;
    }

    public String getSource() {
        return this.source;
    }

    public String getMessage() {
        return this.message;
    }
}