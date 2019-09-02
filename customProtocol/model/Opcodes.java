package customProtocol.model;

public enum Opcodes {

    ChatBroadCast(120),
    GMControlGame(380),
    ClearStorehousePasswd(3402),
    DebugCommand(873),
    ForbidRole(360),
    ForbidUser(5035),
    ForbidchatRole(873),
    PutRoleData(8002),
    GetRoleData(8003),
    DebugAddCash(521),
    PrivateChat(96),
    SysSendMail(4214),
    ForwardChat(8000),
    GetMaxOnline(375),
    GetUser(3002),
    PutUser(3001),
    PublicChat(79),
    GMRestartServer(358);

    private final int opcode;

    Opcodes(int opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return this.opcode;
    }

}
