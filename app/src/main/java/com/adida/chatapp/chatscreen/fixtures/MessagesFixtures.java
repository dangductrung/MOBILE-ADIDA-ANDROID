package com.adida.chatapp.chatscreen.fixtures;

import com.adida.chatapp.chatscreen.models.Message;
import com.adida.chatapp.chatscreen.models.User;

/*
 * Created by troy379 on 12.12.16.
 */
public final class MessagesFixtures extends FixturesData {
    private MessagesFixtures() {
        throw new AssertionError();
    }

    public static Message getImageMessage(String url) {
        Message message = new Message(getRandomId(), getUser(), null);
        message.setImage(new Message.Image(url));
        return message;
    }

    public static Message getImageMessage(String url, String id) {
        Message message = new Message(getRandomId(), getUser(id), null);
        String a = url;
        message.setImage(new Message.Image(url));
        return message;
    }

    public static Message getVoiceMessage() {
        Message message = new Message(getRandomId(), getUser(), null);
        message.setVoice(new Message.Voice("http://example.com", rnd.nextInt(200) + 30));
        return message;
    }

    public static Message getTextMessage() {
        return getTextMessage(getRandomMessage());
    }

    public static Message getTextMessage(String text) {
        return new Message(getRandomId(), getUser(), text);
    }

//    public static ArrayList<Message> getMessages(Date startDate) {
//        ArrayList<Message> messages = new ArrayList<>();
//        for (int i = 0; i < 10/*days count*/; i++) {
//            int countPerDay = rnd.nextInt(5) + 1;
//
//            for (int j = 0; j < countPerDay; j++) {
//                Message message;
//                if (i % 2 == 0 && j % 3 == 0) {
//                    message = getImageMessage();
//                } else {
//                    message = getTextMessage();
//                }
//
//                Calendar calendar = Calendar.getInstance();
//                if (startDate != null) calendar.setTime(startDate);
//                calendar.add(Calendar.DAY_OF_MONTH, -(i * i + 1));
//
//                message.setCreatedAt(calendar.getTime());
//                messages.add(message);
//            }
//        }
//        return messages;
//    }

    public static User getUser() {
        boolean even = rnd.nextBoolean();
        return new User(
                //even ? "0" : "1",
                "0",
                even ? names.get(0) : names.get(1),
                even ? avatars.get(0) : avatars.get(1),
                true);
    }

    public static User getUser(String id) {
        boolean even = rnd.nextBoolean();
        return new User(
                //even ? "0" : "1",
                id,
                even ? names.get(0) : names.get(1),
                even ? avatars.get(0) : avatars.get(1),
                true);
    }


}
