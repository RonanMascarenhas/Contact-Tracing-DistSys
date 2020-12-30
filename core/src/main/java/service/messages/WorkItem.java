package service.messages;

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;

public abstract class WorkItem implements Serializable {
    private Instant created = Clock.systemUTC().instant();
    private Instant lastAccessed = created;
    private Status status = Status.TODO;

    /**
     * Updates the status of this WorkItem and records the update time.
     *
     * @param status the new status of the WorkItem
     */
    public void updateStatus(Status status) {
        this.status = status;
        lastAccessed = Clock.systemUTC().instant();
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Instant lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        TODO("To Do"),
        IN_PROGRESS("In Progress"),
        DONE("Done");

        private String readable;

        Status(String readable) {
            this.readable = readable;
        }

        @Override
        public String toString() {
            return readable;
        }
    }
}
