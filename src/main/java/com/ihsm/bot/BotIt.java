/*
 *
 *
 *  Copyright (C) 2016 IHS Markit.
 *  All Rights Reserved
 *
 *
 *  NOTICE:  All information contained herein is, and remains
 *  the property of Markit and its suppliers,
 *  if any.  The intellectual and technical concepts contained
 *  herein are proprietary to Markit and its suppliers
 *  and may be covered by U.S. and Foreign Patents, patents in
 *  process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material
 *  is strictly forbidden unless prior written permission is obtained
 *  from Markit.
 */
package com.ihsm.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.client.services.ChatServiceListener;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.HashSet;
import java.util.Set;


/**
 * Simple example of the ChatService.
 * <p>
 * It will send a message to a call.home.user and listen/create new Chat sessions.
 * <p>
 * <p>
 * <p>
 * REQUIRED VM Arguments or System Properties:
 * <p>
 * -Dtruststore.file=
 * -Dtruststore.password=password
 * -Dsessionauth.url=https://(hostname)/sessionauth
 * -Dkeyauth.url=https://(hostname)/keyauth
 * -Duser.call.home=frank.tarsillo@markit.com
 * -Duser.cert.password=password
 * -Duser.cert.file=bot.user2.p12
 * -Duser.email=bot.user2@domain.com
 * -Dpod.url=https://(pod host)/pod
 * -Dagent.url=https://(agent server host)/agent
 * -Dreceiver.email=bot.user2@markit.com or bot user email
 *
 * @author  Frank Tarsillo
 */
//NOSONAR
public class BotIt implements ChatListener, ChatServiceListener {


    private final Logger logger = LoggerFactory.getLogger(BotIt.class);

    private SymphonyClient symClient;

    public BotIt() {

        init();

    }

    public static void main(String[] args) {

        new BotIt();

    }

    public void init() {


        try {

            SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig(true);


            //Create an initialized client
            symClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.V4,symphonyClientConfig);


            //Will notify the bot of new Chat conversations.
            symClient.getChatService().addListener(this);

            //A message to send when the BOT comes online.
            SymMessage aMessage = new SymMessage();

            //V4 will wrap the text in a PresentationMl div.
            aMessage.setMessageText("Hello master, I'm alive again....");


            //Creates a Chat session with that we will send a message to.
            Chat chat = new Chat();
            chat.setLocalUser(symClient.getLocalUser());
            Set<SymUser> remoteUsers = new HashSet<>();
            remoteUsers.add(symClient.getUsersClient().getUserFromEmail(symphonyClientConfig.get(SymphonyClientConfigID.RECEIVER_EMAIL)));
            chat.setRemoteUsers(remoteUsers);


            //Add the chat to the chat service, in case the "master" continues the conversation.
            symClient.getChatService().addChat(chat);


            //Send a message to the master user.
            symClient.getMessageService().sendMessage(chat, aMessage);


            logger.info("Finished");



        } catch (MessagesException | UsersClientException e) {
            logger.error("error", e);
        }

    }



    //Chat sessions callback method.
    @Override
    public void onChatMessage(SymMessage message) {
        if (message == null)
            return;

        logger.debug("TS: {}\nFrom ID: {}\nSymMessage: {}\nSymMessage Type: {}",
                message.getTimestamp(),
                message.getFromUserId(),
                message.getMessage(),
                message.getMessageType());


        //Lets find out the chat session associated with this message
        Chat chat = symClient.getChatService().getChatByStream(message.getStreamId());

        if(chat!=null) {
            logger.debug("New message is related to chat with users: {}", remoteUsersString(chat.getRemoteUsers()));
        }else{
            return;
        }


        //Do something here

        //commandHandler(message,chat);

       //freeTextHandler(message,chat);




    }

    private void commandHandler(SymMessage message, Chat chat){




        //////////////////////////////
        //COMMAND DRIVEN
        //Split up the text line
        String[] chunks = message.getMessageText().replace("\u00a0", " ").replace("&nbsp;", " ").split("\\s+");

        //Response to send back
        String response="Usage /add or /update";

        if (chunks.length > 0) {

            //Usually the first word (/action)
            String command = chunks[0].toLowerCase().trim();

            logger.info("MESSAGE: [{}] Command: [{}]", message.getMessageText(), command);


            switch (command) {
                case "/add":
                    logger.debug("Add command received from {} ", message.getFromUserId());
                    response = "You sent the (Add) command";
                    break;
                case "/update":
                    logger.debug("Update command received from {} ", message.getFromUserId());
                    response = "You sent the (Update) command";
                    break;
                default:
                    logger.debug("Send usage");
                    break;

            }
        } else {
            logger.debug("Send usage");

        }


        message.setMessageText(response);
        try {
            symClient.getMessageService().sendMessage(chat,message);

        } catch (MessagesException e) {
            logger.error("Failed..",e);
        }

        ////////////////////////////////////////


    }

    private void freeTextHandler(SymMessage message, Chat chat){


        String command ="";

        //////////////////////////////
        //FreeText driven, which will force you to scan..and messy!

        if(message.getMessageText().toLowerCase().contains("add")){
            command="add";
        }else if(message.getMessageText().toLowerCase().contains("update")){
            command="update";
        }


        //Response to send back
        String response="Usage add or update commands within a line?";

        logger.info("MESSAGE: [{}] Command: [{}]", message.getMessageText(), command);


            switch (command) {
                case "add":
                    logger.debug("Add command received from {} ", message.getFromUserId());
                    response = "You sent the (Add) command";
                    break;
                case "update":
                    logger.debug("Update command received from {} ", message.getFromUserId());
                    response = "You sent the (Update) command";
                    break;
                default:
                    logger.debug("Send usage");
                    break;

            }


        message.setMessageText("FreeText:" + response);
        try {
            symClient.getMessageService().sendMessage(chat,message);

        } catch (MessagesException e) {
            logger.error("Failed..",e);
        }

        ////////////////////////////////////////


    }



    @Override
    public void onNewChat(Chat chat) {

        chat.addListener(this);

        logger.debug("New chat session detected on stream {} with {}", chat.getStream().getStreamId(), remoteUsersString(chat.getRemoteUsers()));


    }

    @Override
    public void onRemovedChat(Chat chat) {

    }

    private  String remoteUsersString(Set<SymUser> symUsers){

        String output = "";
        for(SymUser symUser: symUsers){
            output += "[" + symUser.getId() + ":" + symUser.getDisplayName() + "] ";

        }

        return output;
    }

}
