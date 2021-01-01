package service.entities;

import java.util.Date;

// todo update this once Ronan migrates objects

enum Result {
    POSITIVE,
    NEGATIVE,
    UNDETERMINED
}


public class TestResult {

    private Date date;
    private Result r;

    public TestResult() {
    }

    public TestResult(Date date, Result r) {
        this.date = date;
        this.r = r;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Result getResult() {
        return r;
    }

    public void setResult(Result r) {
        this.r = r;
    }

    @Override
    public String toString() {
        return String.format("TestResult[Date='%s', Result='%s']", date, r);
    }


}
