INSERT INTO PUBLIC.USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES
    ('john@example.com', 'john123', 'John Smith', '1990-05-12'),
    ('jane@example.com', 'jane456', 'Jane Doe', '1985-08-23'),
    ('mike@example.com', 'mike789', 'Mike Johnson', '1995-03-02'),
    ('susan@example.com', 'susan246', 'Susan Lee', '1988-11-17'),
    ('peter@example.com', 'peter135', 'Peter Brown', '1992-07-09');

INSERT INTO PUBLIC.FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, GENRE, RATING)
VALUES
    ('The Godfather', 'The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.', '1972-03-24', 175, 'Crime, Drama', 'R'),
    ('The Shawshank Redemption', 'Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.', '1994-09-22', 142, 'Drama', 'R'),
    ('The Dark Knight', 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.', '2008-07-18', 152, 'Action, Crime, Drama', 'PG-13'),
    ('Forrest Gump', 'The presidencies of Kennedy and Johnson, the Vietnam War, and the Watergate scandal, and more...', '1994-07-06', 142, 'Drama, Romance', 'PG-13'),
    ('Pulp Fiction', 'The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.', '1994-10-14', 154, 'Crime, Drama', 'R');

INSERT INTO PUBLIC.LIKES (FILM_ID, USER_ID) VALUES
	 (1,1),
	 (1,2),
	 (1,4),
	 (2,2),
	 (2,3),
	 (2,4),
	 (3,3),
	 (3,5);

INSERT INTO PUBLIC.FRIENDS (USER_ID, FRIEND_ID) VALUES
	 (1,2),
	 (1,3),
	 (1,4),
	 (2,3),
	 (2,4),
	 (3,4),
	 (3,5),
	 (5,1);