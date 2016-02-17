package sample.BackupBeforeTabs;

public class Movie {

    private String title, genre, rating, length, director, starringActor;
    private double scoreOutOfTen;

    public Movie(){
        this("", "", "", "", "", "", 0);
    }

    public Movie(String title, String genre, String rating, String length, String director, String starringActor, double scoreOutOfTen){
        this.title = title;
        this.genre = genre;
        this.rating = rating;
        this.length = length;
        this.director = director;
        this.starringActor = starringActor;
        this.scoreOutOfTen = scoreOutOfTen;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getStarringActor() {
        return starringActor;
    }

    public void setStarringActor(String starringActor) {
        this.starringActor = starringActor;
    }

    public double getScoreOutOfTen() {
        String score = "" + scoreOutOfTen;
        if(score.length() > 4)
            return scoreOutOfTen = Double.parseDouble(score.substring(0, 4));
        else return scoreOutOfTen;
    }

    public void setScoreOutOfTen(double scoreOutOfTen) {
        this.scoreOutOfTen = scoreOutOfTen;
    }

    public String toString(){
        return title + " " + genre + " " + rating + " " + length + " " + director + " " + starringActor + " " + scoreOutOfTen + "/10";
    }

    public void changeProptery(String propertyName, String newVal){
        switch(propertyName){
            case "title":
                title = newVal;
                break;
            case "genre":
                genre = newVal;
                break;
            case "rating":
                rating = newVal;
                break;
            case "length":
                length = newVal;
                break;
            case "director":
                director = newVal;
                break;
            case "starringActor":
                starringActor = newVal;
                break;
            case "scoreOutOfTen":
                scoreOutOfTen = Double.parseDouble(newVal);
                break;
            default:
                System.out.println("Recieved invalid property name: " + propertyName);
                break;
        }
    }
}
