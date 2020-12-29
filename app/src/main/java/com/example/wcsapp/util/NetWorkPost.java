package com.example.wcsapp.util;

public class NetWorkPost {

    public class URLInfos{
        public static final String URL = "http://192.168.91.202:9003/api/TaskAPI/";
        public static final String PUTONSHELVES = URL+"PUTONSHELVES";
        public static final String GETTASKLIST = URL+"GETTASKLIST";
        public static final String GETTASKLISTEXACT = URL+"GETTASKLISTEXACT";
        public static final String PUTOFFSHELVES = URL+"PUTOFFSHELVES";
        public static final String MOVINGGOODS = URL+"MOVINGGOODS";
        public static final String PDALOGIN = URL+"PDALOGIN";
    }


    public NetWorkPost() {

    }

    public static String logininfo(String user, String pwd){

        String login = "{\"USERNAME\":\""+user+"\",\"PASSWORD\":\""+pwd+"\"}";

        return login;
    }


}
