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

package com.ihsm.bot.lex;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexruntime.AmazonLexRuntime;
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder;
import com.amazonaws.services.lexruntime.model.PostTextRequest;
import com.amazonaws.services.lexruntime.model.PostTextResult;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * @author Frank Tarsillo on 10/3/17.
 */
public class LexItBotRelay implements ChatListener {

    private SymphonyClient symClient;
    private LexItBotDetail lexItBotDetail;

    private AWSCredentials credentials = new BasicAWSCredentials(System.getenv("S3_KEY_ID"), System.getenv("S3_ACCESS_KEY"));
    private AmazonLexRuntime lexClient = AmazonLexRuntimeClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();



    public LexItBotRelay(SymphonyClient symClient, LexItBotDetail lexItBotDetail) {

        this.symClient = symClient;
        this.lexItBotDetail = lexItBotDetail;

    }


    @Override
    public void onChatMessage(SymMessage message) {


        PostTextResult postTextResult = sendLexMessage(message);

        if(postTextResult.getDialogState().equals("ReadyForFulfillment")) {

            message.setMessageText("Completed appointment..");

        }else{

           // message.setMessageText(lexItBotDetail.getBotName() + ": " + postTextResult.getMessage());
            message.setMessageText( postTextResult.getMessage());

        }

        try {
            symClient.getMessagesClient().sendMessage(message.getStream(), message);
        } catch (MessagesException e) {
            e.printStackTrace();
        }


    }


    private PostTextResult sendLexMessage(SymMessage symMessage) {

        PostTextRequest postTextRequest = new PostTextRequest();

        postTextRequest.setBotAlias(lexItBotDetail.getBotAlias());
        postTextRequest.setBotName(lexItBotDetail.getBotName());
        postTextRequest.setRequestAttributes(lexItBotDetail.getRequestAttributes());
        postTextRequest.setSessionAttributes(lexItBotDetail.getSessionAttributes());
        postTextRequest.setUserId(symMessage.getFromUserId().toString());
        postTextRequest.setInputText(symMessage.getMessageText());


        return lexClient.postText(postTextRequest);

    }
}
