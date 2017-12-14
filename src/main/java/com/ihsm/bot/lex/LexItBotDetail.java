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

import java.util.Map;

/**
 * @author Frank Tarsillo on 10/3/17.
 */
public class LexItBotDetail {

    private String botName;
    private String botAlias;
    private Map<String,String> requestAttributes;
    private Map<String,String> sessionAttributes;


    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getBotAlias() {
        return botAlias;
    }

    public void setBotAlias(String botAlias) {
        this.botAlias = botAlias;
    }

    public Map<String, String> getRequestAttributes() {
        return requestAttributes;
    }

    public void setRequestAttributes(Map<String, String> requestAttributes) {
        this.requestAttributes = requestAttributes;
    }

    public Map<String, String> getSessionAttributes() {
        return sessionAttributes;
    }

    public void setSessionAttributes(Map<String, String> sessionAttributes) {
        this.sessionAttributes = sessionAttributes;
    }
}
