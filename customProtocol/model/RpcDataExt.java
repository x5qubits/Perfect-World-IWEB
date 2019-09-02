package customProtocol.model;

import com.goldhuman.IO.Protocol.Rpc;

public abstract class RpcDataExt extends Rpc.Data {

    public ServerType serverType;
    public Opcodes opcode;

}
