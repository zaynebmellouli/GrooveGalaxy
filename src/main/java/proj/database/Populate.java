package proj.database;

import proj.CL;
import proj.server_client.data_objects.User;
import proj.server_client.data_objects.Media;
import proj.server_client.data_objects.MediaContent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;

public class Populate {

    private Populate() {}

    public static void populate() throws DataBaseConnectionException, SQLException, IOException {
        Connection connection = (new DataBaseConnector()).getConnection();


        User user1 = new User(1, "Alice", "abcde",
                Base64.getEncoder().encodeToString(CL.readAESKey("Keys/Key_ServClient_Alice.key").getEncoded())
                , Base64.getEncoder().encodeToString((CL.readAESKey("Keys/Key_Family_Lu.key")).getEncoded()));
        User user2 = new User(2, "Bob", "7894",
                Base64.getEncoder().encodeToString(CL.readAESKey("Keys/Key_ServClient_Bob.key").getEncoded())
                , Base64.getEncoder().encodeToString((CL.readAESKey("Keys/Key_Family_Musterman.key")).getEncoded()));
        User user3 = new User(3, "Charlie", "pass123",
                Base64.getEncoder().encodeToString(CL.readAESKey("Keys/Key_ServClient_Charlie.key").getEncoded())
                , Base64.getEncoder().encodeToString((CL.readAESKey("Keys/Key_Family_Garcia.key")).getEncoded()));
        User user4 = new User(4, "David", "davidpass",
                Base64.getEncoder().encodeToString(CL.readAESKey("Keys/Key_ServClient_David.key").getEncoded())
                , Base64.getEncoder().encodeToString((CL.readAESKey("Keys/Key_Family_Smith.key")).getEncoded()));
        User user5 = new User(5, "Pedro", "pedropassword",
                Base64.getEncoder().encodeToString(CL.readAESKey("Keys/Key_ServClient_Pedro.key").getEncoded())
                , Base64.getEncoder().encodeToString((CL.readAESKey("Keys/Key_Family_Smith.key")).getEncoded()));
        User user6 = new User(6, "Amelia", "ameliapass",
                Base64.getEncoder().encodeToString(CL.readAESKey("Keys/Key_ServClient_Amelia.key").getEncoded())
                , Base64.getEncoder().encodeToString((CL.readAESKey("Keys/Key_Family_Patel.key")).getEncoded()));
        User user7 = new User(7, "Leila", "ameliapass",
                Base64.getEncoder().encodeToString(CL.readAESKey("Keys/Key_ServClient_Leila.key").getEncoded())
                , Base64.getEncoder().encodeToString((CL.readAESKey("Keys/Key_Family_Smith.key")).getEncoded()));
        User user8 = new User(8, "Maria", "mariaapass",
                Base64.getEncoder().encodeToString(CL.readAESKey("Keys/Key_ServClient_Maria.key").getEncoded())
                , Base64.getEncoder().encodeToString((CL.readAESKey("Keys/Key_Family_Giovanni.key")).getEncoded()));
        User user9 = new User(9, "Emily", "emilyypass",
                Base64.getEncoder().encodeToString(CL.readAESKey("Keys/Key_ServClient_Emily.key").getEncoded())
                , Base64.getEncoder().encodeToString((CL.readAESKey("Keys/Key_Family_Johnson.key")).getEncoded()));
        User user10 = new User(10, "Sophia", "sophiapass",
                Base64.getEncoder().encodeToString(CL.readAESKey("Keys/Key_ServClient_Sophia.key").getEncoded())
                , Base64.getEncoder().encodeToString((CL.readAESKey("Keys/Key_Family_Johnson.key")).getEncoded()));

        Media media1 = new Media(1,1, "MP3", "Lynyrd Skynyrd","Free Bird",  "Rock");
        Media media2 = new Media(2,2, "MP3", "Pink Floyd", "Breathe", "Alternative Rock");
        Media media3 = new Media(3,3, "MP3", "Helene Fischer","Herzbeben",  "Pop");

        Media media4 = new Media(4,4, "MP3", "OneRepublic", "I Lived", "Pop");
        Media media5 = new Media(5,5, "MP3", "OneRepublic", "I Lived", "Pop");
        Media media6 = new Media(6,7, "MP3", "OneRepublic", "I Lived", "Pop");

        Media media7 = new Media(7,6, "MP3", "Earth, Wind and Fire","Let's Groove",  "Disco");
        Media media8 = new Media(8,8, "MP3", "Gloria Gaynor","I will survive",  "Funk");
        Media media9 = new Media(9,9, "MP3", "Michael Jackson", "Rock With You", "Disco Funk");
        Media media10 = new Media(10,10, "MP3", "Michael Jackson", "Rock With You", "Disco Funk");

        MediaContent mediaContent1 = new MediaContent(1,1, "Free Bird",
                "If I leave here tomorrow\n" +
                        "Would you still remember me?\n" +
                        "For I must be traveling on now\n" +
                        "'Cause there's too many places I've got to see\n" +
                        "But if I stay here with you, girl\n" +
                        "Things just couldn't be the same\n" +
                        "'Cause I'm as free as a bird now\n" +
                        "And this bird you cannot change\n" +
                        "Oh, oh, oh, oh\n" +
                        "And the bird you cannot change\n" +
                        "And this bird, you cannot change\n" +
                        "Lord knows, I can't change\n" +
                        "Bye-bye baby, it's been sweet love, yeah, yeah\n" +
                        "Though this feelin' I can't change\n" +
                        "But please don't take it so badly\n" +
                        "'Cause Lord knows, I'm to blame\n" +
                        "But if I stay here with you, girl\n" +
                        "Things just couldn't be the same\n" +
                        "'Cause I'm as free as a bird now\n" +
                        "And this bird you cannot change\n" +
                        "Oh, oh, oh, oh\n" +
                        "And the bird you cannot change\n" +
                        "And this bird, you cannot change\n" +
                        "Lord knows, I can't change\n" +
                        "Lord help me, I can't change\n" +
                        "Lord, I can't change\n" +
                        "Won't you fly high, free bird, yeah",
                "src/main/resources/songs/Free Bird.mp3");
        MediaContent mediaContent2 = new MediaContent(2,2,"Breathe",
                "Breathe, breathe in the air\n" +
                        "Don't be afraid to care\n" +
                        "Leave, but don't leave me\n" +
                        "Look around, choose your own ground\n" +
                        "Long you live and high you fly\n" +
                        "Smiles you'll give and tears you'll cry\n" +
                        "And all you touch and all you see\n" +
                        "Is all your life will ever be\n" +
                        "Run, rabbit, run\n" +
                        "Dig that hole, forget the sun\n" +
                        "When, at last, the work is done\n" +
                        "Don't sit down, it's time to dig another one\n" +
                        "Long you live and high you fly\n" +
                        "But only if you ride the tide\n" +
                        "Balanced on the biggest wave\n" +
                        "You race towards an early grave",
                "src/main/resources/songs/Breathe.mp3"
                );
        MediaContent mediaContent3 = new MediaContent(3,3, "Herzbeben",
                "Durch meine Venen fließt der Bass, hämmert gegen meine Sehnen\n" +
                "        Auf das Leben ist Verlass, es hat noch viel zu geben\n" +
                "        Und ich nehm deine Hand, muss tanzen, immer weiter\n" +
                "        Ich vergesse den Verstand, der Horizont wird breiter\n" +
                "        Herzbeben, lass uns leben, wir woll'n was erleben\n" +
                "        Herzbeben, vorwärts, Herz, lass es beben, beben\n" +
                "        Herzbeben, deinem Beat total ergeben\n" +
                "        Lass mich leben, Herzbeben, lass es beben\n" +
                "        Herzbeben, lass uns leben, wir woll'n was erleben\n" +
                "        Herzbeben, vorwärts, Herz, lass es beben, beben\n" +
                "        Herzbeben, lass uns durch die Decke heben\n" +
                "        Herzbeben, lass uns leben, lass doch unsre Körper reden\n" +
                "        Reiß dich los, ich will schweben dir entgegen\n" +
                "        Herzbeben, lass uns leben\n" +
                "        Komm und reiß dich los, ja, wir haben uns gefunden\n" +
                "        Unsre Seelen sind verbunden\n" +
                "        Herzbeben\n" +
                "        Ja, mein Herzschlag ist, was zählt, der Beat macht mich lebendig\n" +
                "        Hab dich längst schon ausgewählt, bin völlig überwältigt\n" +
                "        Komm mit auf meine Umlaufbahn, bring mich aus dem Takt\n" +
                "        Vergessen wir den Lebensplan, genießen den Kontakt\n" +
                "        Spürst du den Rhythmus? Dann steig mit ein\n" +
                "        Das kann unsere Reise sein\n" +
                "        Herzbeben, lass uns leben, wir woll'n was erleben\n" +
                "        Herzbeben, vorwärts, Herz, lass es beben, beben\n" +
                "        Herzbeben, lass uns durch die Decke heben\n" +
                "        Herzbeben, lass uns leben, lass doch unsre Körper reden\n" +
                "        Reiß dich los, ich will schweben dir entgegen\n" +
                "        Herzbeben, lass uns leben\n" +
                "        Komm und reiß dich los, ja, wir haben uns gefunden\n" +
                "        Unsre Seelen sind verbunden\n" +
                "        Herzbeben\n" +
                "        Verlangsamt sich der Puls der Nacht, doch mein\n" +
                "        Mein Atem möcht dich finden\n" +
                "        Der Tag ist längst nicht aufgewacht, wenn sich\n" +
                "        Wenn sich die Energien verbünden\n" +
                "        Spürst du immer noch mein Herz?\n" +
                "        Herzbeben\n" +
                "                Herzbeben\n" +
                "        Herzbeben, lass uns leben, wir woll'n was erleben\n" +
                "        Herzbeben, vorwärts, Herz, lass es beben, beben\n" +
                "        Herzbeben, deinem Beat total ergeben\n" +
                "        Lass mich leben, Herzbeben, lass es beben\n" +
                "        Reiß dich los, ich will schweben dir entgegen\n" +
                "        Herzbeben, lass uns leben\n" +
                "        Komm und reiß dich los, ja, wir haben uns gefunden\n" +
                "        Unsre Seelen sind verbunden\n" +
                "        Herzbeben\"",
                "src/main/resources/songs/Herzbeben.mp3");

        MediaContent mediaContent4 = new MediaContent(4,4, "I Lived",
                "Hope when you take that jump\n" +
                        "You don't fear the fall\n" +
                        "Hope when the water rises\n" +
                        "You built a wall\n" +
                        "Hope when the crowd screams out\n" +
                        "It's screaming your name\n" +
                        "Hope if everybody runs\n" +
                        "You choose to stay\n" +
                        "Hope that you fall in love\n" +
                        "And it hurts so bad\n" +
                        "The only way you can know\n" +
                        "You give it all you have\n" +
                        "And I hope that you don't suffer\n" +
                        "But take the pain\n" +
                        "Hope when the moment comes you'll say\n" +
                        "I, I did it all\n" +
                        "I, I did it all\n" +
                        "I owned every second that this world could give\n" +
                        "I saw so many places\n" +
                        "The things that I did\n" +
                        "Yeah, with every broken bone\n" +
                        "I swear I lived\n" +
                        "Hope that you spend your days\n" +
                        "But they all add up\n" +
                        "And when that sun goes down\n" +
                        "Hope you raise your cup\n" +
                        "Oh, oh\n" +
                        "I wish that I could witness\n" +
                        "All your joy\n" +
                        "And all your pain\n" +
                        "But until my moment comes, I'll say\n" +
                        "I, I did it all\n" +
                        "I, I did it all\n" +
                        "I owned every second that this world could give\n" +
                        "I saw so many places\n" +
                        "The things that I did\n" +
                        "Yeah, with every broken bone\n" +
                        "I swear I lived\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh, oh, whoa-oh-oh\n" +
                        "(With every broken bone)\n" +
                        "I swear I lived\n" +
                        "(With every broken bone)\n" +
                        "I swear I\n" +
                        "I, I did it all\n" +
                        "I, I did it all\n" +
                        "I owned every second that this world could give\n" +
                        "I saw so many places\n" +
                        "The things that I did\n" +
                        "Yeah with every broken bone\n" +
                        "I swear I lived\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "I swear I lived\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh, oh, whoa-oh-oh",
                "src/main/resources/songs/I Lived.mp3"
                );

        MediaContent mediaContent5 = new MediaContent(5,5, "I Lived",
                "Hope when you take that jump\n" +
                        "You don't fear the fall\n" +
                        "Hope when the water rises\n" +
                        "You built a wall\n" +
                        "Hope when the crowd screams out\n" +
                        "It's screaming your name\n" +
                        "Hope if everybody runs\n" +
                        "You choose to stay\n" +
                        "Hope that you fall in love\n" +
                        "And it hurts so bad\n" +
                        "The only way you can know\n" +
                        "You give it all you have\n" +
                        "And I hope that you don't suffer\n" +
                        "But take the pain\n" +
                        "Hope when the moment comes you'll say\n" +
                        "I, I did it all\n" +
                        "I, I did it all\n" +
                        "I owned every second that this world could give\n" +
                        "I saw so many places\n" +
                        "The things that I did\n" +
                        "Yeah, with every broken bone\n" +
                        "I swear I lived\n" +
                        "Hope that you spend your days\n" +
                        "But they all add up\n" +
                        "And when that sun goes down\n" +
                        "Hope you raise your cup\n" +
                        "Oh, oh\n" +
                        "I wish that I could witness\n" +
                        "All your joy\n" +
                        "And all your pain\n" +
                        "But until my moment comes, I'll say\n" +
                        "I, I did it all\n" +
                        "I, I did it all\n" +
                        "I owned every second that this world could give\n" +
                        "I saw so many places\n" +
                        "The things that I did\n" +
                        "Yeah, with every broken bone\n" +
                        "I swear I lived\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh, oh, whoa-oh-oh\n" +
                        "(With every broken bone)\n" +
                        "I swear I lived\n" +
                        "(With every broken bone)\n" +
                        "I swear I\n" +
                        "I, I did it all\n" +
                        "I, I did it all\n" +
                        "I owned every second that this world could give\n" +
                        "I saw so many places\n" +
                        "The things that I did\n" +
                        "Yeah with every broken bone\n" +
                        "I swear I lived\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "I swear I lived\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh, oh, whoa-oh-oh",
                "src/main/resources/songs/I Lived.mp3"
        );

        MediaContent mediaContent6 = new MediaContent(6,7, "I Lived",
                "Hope when you take that jump\n" +
                        "You don't fear the fall\n" +
                        "Hope when the water rises\n" +
                        "You built a wall\n" +
                        "Hope when the crowd screams out\n" +
                        "It's screaming your name\n" +
                        "Hope if everybody runs\n" +
                        "You choose to stay\n" +
                        "Hope that you fall in love\n" +
                        "And it hurts so bad\n" +
                        "The only way you can know\n" +
                        "You give it all you have\n" +
                        "And I hope that you don't suffer\n" +
                        "But take the pain\n" +
                        "Hope when the moment comes you'll say\n" +
                        "I, I did it all\n" +
                        "I, I did it all\n" +
                        "I owned every second that this world could give\n" +
                        "I saw so many places\n" +
                        "The things that I did\n" +
                        "Yeah, with every broken bone\n" +
                        "I swear I lived\n" +
                        "Hope that you spend your days\n" +
                        "But they all add up\n" +
                        "And when that sun goes down\n" +
                        "Hope you raise your cup\n" +
                        "Oh, oh\n" +
                        "I wish that I could witness\n" +
                        "All your joy\n" +
                        "And all your pain\n" +
                        "But until my moment comes, I'll say\n" +
                        "I, I did it all\n" +
                        "I, I did it all\n" +
                        "I owned every second that this world could give\n" +
                        "I saw so many places\n" +
                        "The things that I did\n" +
                        "Yeah, with every broken bone\n" +
                        "I swear I lived\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh, oh, whoa-oh-oh\n" +
                        "(With every broken bone)\n" +
                        "I swear I lived\n" +
                        "(With every broken bone)\n" +
                        "I swear I\n" +
                        "I, I did it all\n" +
                        "I, I did it all\n" +
                        "I owned every second that this world could give\n" +
                        "I saw so many places\n" +
                        "The things that I did\n" +
                        "Yeah with every broken bone\n" +
                        "I swear I lived\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "I swear I lived\n" +
                        "Oh, whoa-oh-oh-oh, oh-oh\n" +
                        "Oh, whoa-oh-oh, oh, whoa-oh-oh",
                "src/main/resources/songs/I Lived.mp3"
        );


        MediaContent mediaContent7 = new MediaContent(7,6,"Let's Groove",
        "Let this groove, get you to move,\n" +
                "                It's alright, alright\n" +
                "        Let this groove, set in your shoes,\n" +
                "                Stand up, alright\n" +
                "        Gonna tell you what you can do,\n" +
                "        With my love, alright\n" +
                "        Let you know girl you're looking good\n" +
                "        You're out of sight and alright\n" +
                "        Move yourself and glide like a 747\n" +
                "        Loose yourself in the sky among the clouds in the heavens\n" +
                "        Let this groove, light up your fuse, alright\n" +
                "        Let this groove, set in your shoes\n" +
                "        Stand up, alright\n" +
                "        Let me tell you what you can do\n" +
                "            With my love, alright\n" +
                "        Gotta let you know girl you're looking good\n" +
                "        You're out of sight, you're alright\n" +
                "        Tell the DJ to play your favorite tune\n" +
                "        Then you know it's okay\n" +
                "        What you found is happiness, now\n" +
                "        Let this groove, get you to move, alright\n" +
                "        Let this groove set in your shoes\n" +
                "        Stand up, alright\n" +
                "        You will find peace of mind on the floor\n" +
                "        Take a little time, come and see, you and me\n" +
                "        Make a little sign, I'll be there after a while\n" +
                "        If you want my love\n" +
                "        We can boogie on down, down, down, down\n" +
                "        Let's groove tonight\n" +
                "        Share the spice of life\n" +
                "        Baby slice it right\n" +
                "        We're gonna groove tonight",
                "src/main/resources/songs/Let's Groove.mp3");

        MediaContent mediaContent8 = new MediaContent(8,8,"I will survive",
                "At first I was afraid, I was petrified\n" +
                        "Kept thinking I could never live without you by my side\n" +
                        "But then I spent so many nights thinking how you did me wrong\n" +
                        "And I grew strong\n" +
                        "And I learned how to get along\n" +
                        "And so you're back\n" +
                        "From outer space\n" +
                        "I just walked in to find you here with that sad look upon your face\n" +
                        "I should have changed that stupid lock, I should have made you leave your key\n" +
                        "If I'd known for just one second you'd be back to bother me\n" +
                        "Go on now, go, walk out the door\n" +
                        "Just turn around now\n" +
                        "'Cause you're not welcome anymore\n" +
                        "Weren't you the one who tried to hurt me with goodbye?\n" +
                        "You think I'd crumble?\n" +
                        "You think I'd lay down and die?\n" +
                        "Oh no, not I, I will survive\n" +
                        "Oh, as long as I know how to love, I know I'll stay alive\n" +
                        "I've got all my life to live\n" +
                        "And I've got all my love to give and I'll survive\n" +
                        "I will survive, hey, hey\n" +
                        "It took all the strength I had not to fall apart\n" +
                        "Kept trying hard to mend the pieces of my broken heart\n" +
                        "And I spent oh-so many nights just feeling sorry for myself\n" +
                        "I used to cry\n" +
                        "But now I hold my head up high and you see me\n" +
                        "Somebody new\n" +
                        "I'm not that chained-up little person still in love with you\n" +
                        "And so you felt like dropping in and just expect me to be free\n" +
                        "Well, now I'm saving all my lovin' for someone who's loving me\n" +
                        "Go on now, go, walk out the door\n" +
                        "Just turn around now\n" +
                        "'Cause you're not welcome anymore\n" +
                        "Weren't you the one who tried to break me with goodbye?\n" +
                        "You think I'd crumble?\n" +
                        "You think I'd lay down and die?\n" +
                        "Oh no, not I, I will survive\n" +
                        "Oh, as long as I know how to love, I know I'll stay alive\n" +
                        "I've got all my life to live\n" +
                        "And I've got all my love to give and I'll survive\n" +
                        "I will survive\n" +
                        "Oh\n" +
                        "Go on now, go, walk out the door\n" +
                        "Just turn around now\n" +
                        "'Cause you're not welcome anymore\n" +
                        "Weren't you the one who tried to break me with goodbye?\n" +
                        "You think I'd crumble?\n" +
                        "You think I'd lay down and die?\n" +
                        "Oh no, not I, I will survive\n" +
                        "Oh, as long as I know how to love, I know I'll stay alive\n" +
                        "I've got all my life to live\n" +
                        "And I've got all my love to give and I'll survive\n" +
                        "I will survive\n" +
                        "I will survive",
                "src/main/resources/songs/I Will Survive.mp3");

    MediaContent mediaContent9 = new MediaContent(9,9,"Rock With You",
            "Girl, close your eyes\n" +
                    "Let that rhythm get into you\n" +
                    "Don't try to fight it\n" +
                    "There ain't nothing that you can do\n" +
                    "Relax your mind\n" +
                    "Lay back and groove with mine\n" +
                    "You gotta feel that heat\n" +
                    "And we can ride the boogie\n" +
                    "Share that beat of love\n" +
                    "I wanna rock with you (all night)\n" +
                    "Dance you into day (sunlight)\n" +
                    "I wanna rock with you (all night)\n" +
                    "We're gonna rock the night away (rock, right)\n" +
                    "Out on the floor\n" +
                    "There ain't nobody there but us\n" +
                    "Girl, when you dance\n" +
                    "There's a magic that must be love\n" +
                    "Just take it slow\n" +
                    "'Cause we got so far to go\n" +
                    "When you feel that heat\n" +
                    "And we're gonna ride the boogie\n" +
                    "Share that beat of love\n" +
                    "I wanna rock with you (all night)\n" +
                    "Dance you into day (sunlight)\n" +
                    "I wanna rock with you (all night)\n" +
                    "We gon' rock the night away (rock, right)\n" +
                    "And when the groove is dead and gone (yeah)\n" +
                    "You know that love survives\n" +
                    "So we can rock forever, on\n" +
                    "I wanna rock with you\n" +
                    "I wanna groove with you\n" +
                    "Wanna rock (all night)\n" +
                    "With you, girl (sunlight)\n" +
                    "Rock with you, rock with you, yeah (all night)\n" +
                    "Dance the night away (rock, right)\n" +
                    "I wanna rock with you, yeah (all night)\n" +
                    "Rock you into day (sunlight)\n" +
                    "I wanna rock with you (all night)\n" +
                    "Rock the night away (rock, right)\n" +
                    "Feel the heat, feel the beat (all night)\n" +
                    "(Woo)\n" +
                    "Rock you into day (sunlight)\n" +
                    "I wanna rock (all night)\n" +
                    "Rock the night away (rock, right)",
            "src/main/resources/songs/Rock With You.mp3");

        MediaContent mediaContent10 = new MediaContent(10,9,"Rock With You",
                "Girl, close your eyes\n" +
                        "Let that rhythm get into you\n" +
                        "Don't try to fight it\n" +
                        "There ain't nothing that you can do\n" +
                        "Relax your mind\n" +
                        "Lay back and groove with mine\n" +
                        "You gotta feel that heat\n" +
                        "And we can ride the boogie\n" +
                        "Share that beat of love\n" +
                        "I wanna rock with you (all night)\n" +
                        "Dance you into day (sunlight)\n" +
                        "I wanna rock with you (all night)\n" +
                        "We're gonna rock the night away (rock, right)\n" +
                        "Out on the floor\n" +
                        "There ain't nobody there but us\n" +
                        "Girl, when you dance\n" +
                        "There's a magic that must be love\n" +
                        "Just take it slow\n" +
                        "'Cause we got so far to go\n" +
                        "When you feel that heat\n" +
                        "And we're gonna ride the boogie\n" +
                        "Share that beat of love\n" +
                        "I wanna rock with you (all night)\n" +
                        "Dance you into day (sunlight)\n" +
                        "I wanna rock with you (all night)\n" +
                        "We gon' rock the night away (rock, right)\n" +
                        "And when the groove is dead and gone (yeah)\n" +
                        "You know that love survives\n" +
                        "So we can rock forever, on\n" +
                        "I wanna rock with you\n" +
                        "I wanna groove with you\n" +
                        "Wanna rock (all night)\n" +
                        "With you, girl (sunlight)\n" +
                        "Rock with you, rock with you, yeah (all night)\n" +
                        "Dance the night away (rock, right)\n" +
                        "I wanna rock with you, yeah (all night)\n" +
                        "Rock you into day (sunlight)\n" +
                        "I wanna rock with you (all night)\n" +
                        "Rock the night away (rock, right)\n" +
                        "Feel the heat, feel the beat (all night)\n" +
                        "(Woo)\n" +
                        "Rock you into day (sunlight)\n" +
                        "I wanna rock (all night)\n" +
                        "Rock the night away (rock, right)",
                "src/main/resources/songs/Rock With You.mp3");
    try {
            // 1. Insert data into the users table
            DatabaseUtils.addUser(connection, user1);
            DatabaseUtils.addUser(connection, user2);
            DatabaseUtils.addUser(connection, user3);
            DatabaseUtils.addUser(connection, user4);
            DatabaseUtils.addUser(connection, user5);
            DatabaseUtils.addUser(connection, user6);
            DatabaseUtils.addUser(connection, user7);
            DatabaseUtils.addUser(connection, user8);
            DatabaseUtils.addUser(connection, user9);
            DatabaseUtils.addUser(connection, user10);


            // 2. Insert data into the media table
            DatabaseUtils.addMedia(connection, media1);
            DatabaseUtils.addMedia(connection, media2);
            DatabaseUtils.addMedia(connection, media3);
            DatabaseUtils.addMedia(connection, media4);
            DatabaseUtils.addMedia(connection, media5);
            DatabaseUtils.addMedia(connection, media6);
            DatabaseUtils.addMedia(connection, media7);
            DatabaseUtils.addMedia(connection, media8);
            DatabaseUtils.addMedia(connection, media9);
            DatabaseUtils.addMedia(connection, media10);

            // 3. Insert data into the media_content table
            DatabaseUtils.addMediaContent(connection, mediaContent1);
            DatabaseUtils.addMediaContent(connection, mediaContent2);
            DatabaseUtils.addMediaContent(connection, mediaContent3);
            DatabaseUtils.addMediaContent(connection, mediaContent4);
            DatabaseUtils.addMediaContent(connection, mediaContent5);
            DatabaseUtils.addMediaContent(connection, mediaContent6);
            DatabaseUtils.addMediaContent(connection, mediaContent7);
            DatabaseUtils.addMediaContent(connection, mediaContent8);
            DatabaseUtils.addMediaContent(connection, mediaContent9);
            DatabaseUtils.addMediaContent(connection, mediaContent10);


    } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
