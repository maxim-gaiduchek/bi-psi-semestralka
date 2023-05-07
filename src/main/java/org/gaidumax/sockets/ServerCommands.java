package org.gaidumax.sockets;

public interface ServerCommands {

    String END = "\7\10";
    String SERVER_MOVE = "102 MOVE" + END;
    String SERVER_TURN_LEFT = "103 TURN LEFT" + END;
    String SERVER_TURN_RIGHT = "104 TURN RIGHT" + END;
    String SERVER_PICK_UP = "105 GET MESSAGE" + END;
    String SERVER_LOGOUT = "106 LOGOUT" + END;
    String SERVER_KEY_REQUEST = "107 KEY REQUEST" + END;
    String SERVER_OK = "200 OK" + END;
    String SERVER_LOGIN_FAILED = "300 LOGIN FAILED" + END;
    String SERVER_SYNTAX_ERROR = "301 SYNTAX ERROR" + END;
    String SERVER_LOGIC_ERROR = "302 LOGIC ERROR" + END;
    String SERVER_KEY_OUT_OF_RANGE_ERROR = "303 KEY OUT OF RANGE" + END;
}
