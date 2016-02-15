package sample;

public class Movie {

    private String title, genre, rating, length, director, starringActor;
    private int scoreOutOfTen;

    public Movie(){
        this("", "", "", "", "", "", 0);
    }

    public Movie(String title, String genre, String rating, String length, String director, String starringActor, int scoreOutOfTen){
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

    public int getScoreOutOfTen() {
        return scoreOutOfTen;
    }

    public void setScoreOutOfTen(int scoreOutOfTen) {
        this.scoreOutOfTen = scoreOutOfTen;
    }

    public String toString(){
        return title + " " + genre + " " + rating + " " + length + " " + director + " " + starringActor + " " + scoreOutOfTen + "/10";
    }
}
