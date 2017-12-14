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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.services.ChatServiceListener;

import java.util.HashMap;
import java.util.Map;


/**
 * LexItBot is an example of an integration with AWS LEX bots.  It acts as a simple relay between the Symphony endpoint and
 * a AWS Lex bot.  The idea here is to handle all input through Lex NLP engines and responses through Lambda.
 *
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
 * -Dpod.url=https://(pod host)/pod
 * -Dagent.url=https://(agent server host)/agent
 * -Dreceiver.email=bot.user2@markit.com or bot user email
 *
 * @author Frank Tarsillo
 */
//NOSONAR
public class LexItBot implements ChatServiceListener {


    private final Logger logger = LoggerFactory.getLogger(LexItBot.class);

    private SymphonyClient symClient;

    private LexItBotDetail lexItBotDetail = new LexItBotDetail();


    public LexItBot(){

        this(System.getProperty("bot.name"), System.getProperty("bot.alias"));
    }



    public LexItBot(String botName, String botAlias) {

        lexItBotDetail.setBotName(botName);
        lexItBotDetail.setBotAlias(botAlias);

        Map<String, String> sessionAttributes = new HashMap<>();


//        sessionAttributes.put("InventoryFilterRequest","{\n" +
//                "  \"identityUrl\": \"https://identity.svcs.mdevlab.com/identity\",\n" +
//                "  \"keyUrl\": \"https://identity.svcs.mdevlab.com/identity\",\n" +
//                "  \"cmtkServiceUrl\": \"https://q2-mim-ux.a3.mdevlab.com/api/mim-server/\",\n" +
//                "  \"cmtkUser\": \"demo.user1\",\n" +
//                "  \"cmtkPass\": \"Something\",\n" +
//                "  \"filterType\": \"REGION\",\n" +
//                "  \"filterFieldInfo\": {\n" +
//                "    \"operation\": \"equals\",\n" +
//                "    \"values\": [\n" +
//                "      \"EMEA\"\n" +
//                "    ]\n" +
//                "  }\n" +
//                "}");

        lexItBotDetail.setSessionAttributes(sessionAttributes);

        init();

    }

    public static void main(String[] args) {

        if(args.length == 2) {
            new LexItBot(args[0], args[1]);
        }else{


            System.out.println("You need to provide a (lexbotname) (lexbotalias) to start");

            new LexItBot();
        }
    }

    //Start it up..
    public void init() {


        SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig(true);

        //Create an initialized client
        symClient = SymphonyClientFactory.getClient(
                SymphonyClientFactory.TYPE.V4, symphonyClientConfig);

        if(symClient!=null)
            symClient.getChatService().addListener(this);
    }


    @Override
    public void onNewChat(Chat chat) {

        chat.addListener(new LexItBotRelay(symClient, lexItBotDetail));

    }

    @Override
    public void onRemovedChat(Chat chat) {

    }
}




